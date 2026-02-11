package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public final class MainView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private final Persistence persistence;
    private DisposableObserver<History<FloplessState>> observe;
    private DisposableObserver<Action.Effect> effectObserve;
    private FloplessState currentState;
    private FloplessState pendingDeleteState;
    private final GaussianBlur blur = new GaussianBlur(10);

    @FXML
    private BorderPane mainLayout;
    @FXML
    private StackPane deleteOverlay;
    @FXML
    private Label deleteModalBody;
    @FXML
    private Button deleteCancelButton;
    @FXML
    private Button deleteConfirmButton;

    public MainView(Stage stage, FloplessLoop loop, Persistence persistence) {
        this.stage = stage;
        this.loop = loop;
        this.persistence = persistence;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupActions();
        setupSubscription();
        setupEffectSubscription();
    }

    private void setupActions() {
        deleteCancelButton.setOnAction(_ -> loop.accept(new Action.Effect.DeleteStateConfirmDismissed()));
        deleteConfirmButton.setOnAction(_ -> {
            if (pendingDeleteState != null) {
                loop.accept(new Action.Effect.DeleteStateRequested(pendingDeleteState));
            }
            loop.accept(new Action.Effect.DeleteStateConfirmDismissed());
        });
    }

    private void setupSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(History<FloplessState> history) {
                currentState = history.present();
            }

            @Override
            public void onError(Throwable e) {
                throw new IllegalStateException(e);
            }

            @Override
            public void onComplete() {
                // Not supported
            }
        };
        loop.subscribe(observe);
        stage.setOnHiding(_ -> disposeAll());
    }

    private void setupEffectSubscription() {
        effectObserve = new DisposableObserver<>() {
            @Override
            public void onNext(Action.Effect effect) {
                Platform.runLater(() -> handleEffect(effect));
            }

            @Override
            public void onError(Throwable e) {
                throw new IllegalStateException(e);
            }

            @Override
            public void onComplete() {
                // Not supported
            }
        };
        loop.subscribeEffects(effectObserve);
    }

    private void handleEffect(Action.Effect effect) {
        switch (effect) {
            case Action.Effect.DeleteStateConfirmRequested request -> showDeleteOverlay(request.state());
            case Action.Effect.DeleteStateConfirmDismissed _ -> hideDeleteOverlay();
            default -> {
                // No-op
            }
        }
    }

    private void showDeleteOverlay(FloplessState state) {
        pendingDeleteState = state;
        var chartName = new Strategy().name(state);
        deleteModalBody.setText("Delete " + chartName + "?\nThis action cannot be undone.");
        deleteOverlay.setManaged(true);
        deleteOverlay.setVisible(true);
        mainLayout.setEffect(blur);
        deleteCancelButton.requestFocus();
    }

    private void hideDeleteOverlay() {
        pendingDeleteState = null;
        deleteOverlay.setVisible(false);
        deleteOverlay.setManaged(false);
        mainLayout.setEffect(null);
    }

    private void persistState() {
        try {
            persistence.save(currentState);
        } catch (Exception e) {
            System.out.println("[MAIN VIEW] " + e);
        }
    }

    private void disposeAll() {
        persistState();
        hideDeleteOverlay();
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
        if (effectObserve != null && !effectObserve.isDisposed()) {
            effectObserve.dispose();
        }
    }
}

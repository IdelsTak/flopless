package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class ActionSidebarView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private DisposableObserver<FloplessState> observe;
    @FXML
    private RadioButton foldRadio;
    @FXML
    private ToggleGroup actionGroup;
    @FXML
    private RadioButton checkRadio;
    @FXML
    private RadioButton callRadio;
    @FXML
    private RadioButton raiseRadio;
    @FXML
    private RadioButton allinRadio;
    @FXML
    private TextField limperAmountField;
    @FXML
    private Button decrementActionAmountButton;
    @FXML
    private TextField actionAmountField;
    @FXML
    private Button incrementActionAmountButton;
    @FXML
    private Button decrementLimperAmountButton;
    @FXML
    private Button incrementLimperAmountButton;

    public ActionSidebarView(Stage stage, FloplessLoop loop) {
        this.stage = stage;
        this.loop = loop;

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupActions();
        setupSubscription();
    }

    private void setupActions() {
        foldRadio.setOnAction(_ -> {
            if (foldRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Fold()));
            }
        });
        checkRadio.setOnAction(_ -> {
            if (checkRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Check()));
            }
        });
        callRadio.setOnAction(_ -> {
            if (callRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Call()));
            }
        });
        allinRadio.setOnAction(_ -> {
            if (allinRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.AllIn()));
            }
        });
//        raiseRadio.setOnAction(_ -> {
//            if (raiseRadio.isSelected()) {
//                loop.accept(new Action.Effect.GridActionSelected(new GameAction.Money.Raise(BigDecimal.ZERO)));
//            }
//        });
    }

    private void setupSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(FloplessState state) {
                Platform.runLater(() -> render(state));
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
        stage.setOnHiding(_ -> dispose());
    }

    private void render(FloplessState state) {
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

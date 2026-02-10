package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public final class LibraryView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private final Persistence persistence;
    private final Strategy strategy;
    private DisposableObserver<History<FloplessState>> observe;
    private String activeStateName;
    @FXML
    private VBox rangesListBox;
    @FXML
    private Button newRangeButton;
    @FXML
    private ScrollPane scroll;

    public LibraryView(Stage stage, FloplessLoop loop, Persistence persistence) {
        this.persistence = persistence;
        this.stage = stage;
        this.loop = loop;
        strategy = new Strategy();

        activeStateName = "";
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        var savedStates = persistence.loadAll();
        Platform.runLater(() -> renderLibrary(savedStates));
        setupActions();
        setupSubscription();

        if (!savedStates.isEmpty()) {
            Platform.runLater(() -> {
                loop.accept(new Action.User.LoadState(savedStates.getFirst()));
            });
        }
    }

    private void renderLibrary(List<FloplessState> states) {
        Node toScrollToTmp = null;
        rangesListBox.getChildren().clear();
        for (var state : states) {
            var item = new HBox();
            item.getStyleClass().add("grid-item");
            item.setMaxWidth(rangesListBox.getWidth() * 0.97);
            var name = strategy.name(state);
            if (name.equals(activeStateName)) {
                item.getStyleClass().add("active");
                toScrollToTmp = item;
            }

            var label = new Label(name);
            label.setWrapText(true);
            label.setMinHeight(55);
            item.setOnMouseClicked(_ -> loop.accept(new Action.User.LoadState(state)));
            item.getChildren().add(label);
            rangesListBox.getChildren().add(item);
        }
        if (toScrollToTmp != null) {
            var toScrollTo = toScrollToTmp;
            Platform.runLater(() -> new ScrollTo().reveal(scroll, toScrollTo));
        }
    }

    private void setupActions() {
        newRangeButton.setOnAction(_ ->
          loop.accept(new Action.User.LoadState(FloplessState.initial())));
    }

    private void setupSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(History<FloplessState> history) {
                Platform.runLater(() -> render(history.present()));
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
        var name = strategy.name(state);
        activeStateName = name;
        var savedStates = persistence.loadAll();
        renderLibrary(savedStates);
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

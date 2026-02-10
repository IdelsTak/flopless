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
        renderLibrary(savedStates);
        setupActions();
        setupSubscription();
    }

    private void renderLibrary(List<FloplessState> states) {
        rangesListBox.getChildren().clear();
        for (var state : states) {
            var item = new HBox();
            item.getStyleClass().add("grid-item");
            var name = strategy.name(state);
            if (name.equals(activeStateName)) {
                item.getStyleClass().add("active");
            }

            var label = new Label(name);
            label.setWrapText(true);
            label.setMinHeight(50);
            item.getChildren().add(label);
            item.setOnMouseClicked(_ -> loop.accept(new Action.User.LoadState(state)));
            rangesListBox.getChildren().add(item);
        }
    }

    private void setupActions() {
        newRangeButton.setOnAction(_ -> loop.accept(new Action.User.LoadState(FloplessState.initial())));
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

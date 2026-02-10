package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.*;

public final class ToolbarView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private DisposableObserver<History<FloplessState>> observe;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button clearGridButton;
    @FXML
    private Button saveButton;

    public ToolbarView(Stage stage, FloplessLoop loop) {
        this.stage = stage;
        this.loop = loop;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupKeyboardShortcuts();
        setupActions();
        setupSubscription();
    }

    private void setupKeyboardShortcuts() {
        undoButton.sceneProperty().addListener((_, _, scene) -> {
            if (scene == null) {
                return;
            }
            scene.getAccelerators().put(
              new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
              undoButton::fire
            );
            scene.getAccelerators().put(
              new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
              redoButton::fire
            );
            scene.getAccelerators().put(
              new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
              saveButton::fire
            );
        });
    }

    private void setupActions() {
        undoButton.setOnAction(_ -> loop.accept(new Action.User.Undo()));
        redoButton.setOnAction(_ -> loop.accept(new Action.User.Redo()));
        saveButton.setOnAction(_ -> loop.accept(new Action.User.Save()));
        clearGridButton.setOnAction(_ -> loop.accept(new Action.User.RangeClear()));
    }

    private void setupSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(History<FloplessState> history) {
                Platform.runLater(() -> render(history));
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

    private void render(History<FloplessState> history) {
        var state = history.present();
        clearGridButton.setDisable(state.selectedRange().coordinates().isEmpty());
        undoButton.setDisable(!history.canUndo());
        redoButton.setDisable(!history.canRedo());
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

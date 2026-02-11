package com.github.idelstak.flopless.fx;

import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.view.*;
import java.io.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public final class FloplessWindow {

    public Stage open(Stage owner) {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("FloplessWindow.open must run on the JavaFX Application Thread");
        }

        try {
            var stage = new Stage();
            if (owner != null) {
                stage.initOwner(owner);
            }
            var loader = new FXMLLoader(FloplessWindow.class.getResource("/fxml/main.fxml"));
            var persistence = new JsonPersistence();
            FloplessState initialState = FloplessState.initial();
            var loop = new FloplessLoop(initialState, new ReducedState(persistence));
            loader.setControllerFactory(type -> {
                if (type.equals(GridView.class)) {
                    return new GridView(stage, loop);
                }
                if (type.equals(SidebarView.class)) {
                    return new SidebarView(stage, loop);
                }
                if (type.equals(ActionSidebarView.class)) {
                    return new ActionSidebarView(stage, loop);
                }
                if (type.equals(ToolbarView.class)) {
                    return new ToolbarView(stage, loop);
                }
                if (type.equals(LibraryView.class)) {
                    return new LibraryView(stage, loop, persistence);
                }
                return new MainView(stage, loop, persistence);
            });
            var root = loader.<Parent>load();
            String title = "Preflop Charts Editor";
            if (owner != null) {
                title = owner.getTitle() + " - Preflop Charts Editor";
            }
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            return stage;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open Flopless window", e);
        }
    }
}

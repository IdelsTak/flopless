package com.github.idelstak.flopless.fx;

import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.view.*;
import java.io.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public final class FloplessApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        var persistence = new JsonPersistence("flopless.json");
        FloplessState initialState = FloplessState.initial();
        try {
            var savedState = persistence.load();
            System.out.println("[FLOPLESS APP] savedState = " + savedState);
            initialState = savedState;
        } catch (Exception e) {
            System.out.println("[FLOPLESS APP] " + e);
        }
        var loop = new FloplessLoop(initialState, new ReducedState());
        loader.setControllerFactory(type -> {
            System.out.println("[FLOPLESS APP] type = " + type.getSimpleName());
            if (type.equals(GridView.class)) {
                return new GridView(primaryStage, loop);
            }
            if (type.equals(SidebarView.class)) {
                return new SidebarView();
            }
            if (type.equals(ActionSidebarView.class)) {
                return new ActionSidebarView(primaryStage, loop);
            }
            if (type.equals(ToolbarView.class)) {
                return new ToolbarView(primaryStage, loop);
            }
            return new MainView(primaryStage, loop, persistence);
        });
        var root = loader.<Parent>load();
        var scene = new Scene(root);
        primaryStage.setTitle("Flopless");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

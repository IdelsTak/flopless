package com.github.idelstak.flopless.fx;

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
        loader.setControllerFactory(type -> {
            if (type.equals(GridView.class)) {
                return new GridView();
            }
            if (type.equals(SidebarView.class)) {
                return new SidebarView();
            }
            return new MainView();
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

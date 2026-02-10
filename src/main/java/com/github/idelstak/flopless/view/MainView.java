package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.fxml.*;
import javafx.stage.*;

public final class MainView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private final Persistence persistence;
    private DisposableObserver<History<FloplessState>> observe;
    private FloplessState currentState;

    public MainView(Stage stage, FloplessLoop loop, Persistence persistence) {
        this.stage = stage;
        this.loop = loop;
        this.persistence = persistence;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSubscription();
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
        stage.setOnHiding(_ -> {
            persistState();
            dispose();
        });
    }

    private void persistState() {
        try {
            persistence.save(currentState);
        } catch (Exception e) {
            System.out.println("[MAIN VIEW] " + e);
        }
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

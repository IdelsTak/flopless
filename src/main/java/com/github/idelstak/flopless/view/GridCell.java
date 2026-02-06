package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.*;
import io.reactivex.rxjava3.observers.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;

public final class GridCell extends StackPane {

    private final Stage stage;
    private final FloplessLoop loop;
    private final String notation;
    private final Coordinate coordinate;
    private DisposableObserver<FloplessState> observe;
    private boolean isSelected;

    GridCell(Stage stage, FloplessLoop loop, String notation, Coordinate coordinate) {
        this.stage = stage;
        this.loop = loop;
        this.notation = notation;
        this.coordinate = coordinate;

        setupCell();
        setupLabel();
        setupSubscription();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append('{');
        sb.append(notation);
        sb.append(" [").append(coordinate.column());
        sb.append(", ").append(coordinate.row()).append(']');
        sb.append('}');
        return sb.toString();
    }

    private void setupCell() {
        getStyleClass().add("cell");
    }

    private void setupLabel() {
        handLabel = new Label(notation);
        handLabel.getStyleClass().add("hand-label");
        getChildren().add(handLabel);
    }
    private Label handLabel;

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
        isSelected = state.selectedRange().coordinates().contains(coordinate);
        setBackground(Background.fill(Color.web("#151719")));
        handLabel.setTextFill(Color.web("#52525b"));
        if (isSelected) {
            setBackground(Background.fill(state.selectedAction().color()));
            handLabel.setTextFill(Color.WHITE);
        }
        var isPreviewed = state.previewRange().coordinates().contains(coordinate);
        if (isSelected && isPreviewed) {
            setBackground(Background.fill(Color.rgb(244, 67, 54, 0.5)));
            handLabel.setTextFill(Color.WHITE);
        } else if (!isSelected && isPreviewed) {
            setBackground(Background.fill(Color.rgb(76, 175, 80, 0.5)));
            handLabel.setTextFill(Color.WHITE);
        }
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

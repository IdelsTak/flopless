package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import javafx.application.*;
import javafx.css.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public final class GridCell extends StackPane {

    private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
    private static final PseudoClass PREVIEW_ADD_PSEUDO_CLASS = PseudoClass.getPseudoClass("preview-add");
    private static final PseudoClass PREVIEW_REMOVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("preview-remove");
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
        setupActions();
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
        var handLabel = new Label(notation);
        handLabel.getStyleClass().add("hand-label");
        getChildren().add(handLabel);
    }

    private void setupActions() {
        setOnMouseClicked(_ -> mouseClicked());
    }

    private void mouseClicked() {
        var action = isSelected
                   ? new Action.User.DeselectCell(coordinate)
                   : new Action.User.SelectCell(coordinate);
        loop.accept(action);
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

        isSelected = state.selectedRange().coordinates().contains(coordinate);
        boolean isPreviewed = state.previewRange().coordinates().contains(coordinate);

        pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, isSelected);
        getStyleClass().removeAll("preview-add", "preview-remove");

        if (isSelected && isPreviewed) {
            getStyleClass().add("preview-remove");
        } else if (!isSelected && isPreviewed) {
            getStyleClass().add("preview-add");
        } else if (isSelected) {
            // getStyleClass().add("selected");
            // pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, isSelected);
        }
//        isSelected = state.selectedRange().coordinates().contains(coordinate);
//        boolean isPreview = state.previewRange().coordinates().contains(coordinate);
//
//        pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, isSelected);
//
//        boolean isPreviewingAdd = isPreview && state.selectMode() instanceof SelectMode.Selecting;
//        boolean isPreviewingRemove = isPreview && state.selectMode() instanceof SelectMode.Erasing;
//
//        pseudoClassStateChanged(PREVIEW_ADD_PSEUDO_CLASS, isPreviewingAdd);
//        pseudoClassStateChanged(PREVIEW_REMOVE_PSEUDO_CLASS, isPreviewingRemove);
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

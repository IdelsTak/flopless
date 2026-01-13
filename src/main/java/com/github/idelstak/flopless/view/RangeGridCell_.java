package com.github.idelstak.flopless.view;

import javafx.scene.control.*;
import javafx.scene.layout.*;

final class RangeGridCell_ extends StackPane {

    private final int row;
    private final int col;
    private final String hand;
    private boolean isActive = false;
    private final Label handLabel;

    RangeGridCell_(int row, int col, String hand) {
        this.row = row;
        this.col = col;
        this.hand = hand;

        this.getStyleClass().add("cell");

        handLabel = new Label(hand);
        handLabel.getStyleClass().add("hand-label");
        this.getChildren().add(handLabel);
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    boolean isActive() {
        return isActive;
    }

    void setActive(boolean active) {
        this.isActive = active;
        updateState();
    }

    void toggleActive() {
        setActive(!isActive);
    }

    void setPreview(boolean isAdding) {
        getStyleClass().removeAll("preview-add", "preview-remove");
        getStyleClass().add(isAdding ? "preview-add" : "preview-remove");
    }

    void clearPreview() {
        getStyleClass().removeAll("preview-add", "preview-remove");
    }

    private void updateState() {
        if (isActive) {
            getStyleClass().add("active");
        } else {
            getStyleClass().remove("active");
        }
    }
}

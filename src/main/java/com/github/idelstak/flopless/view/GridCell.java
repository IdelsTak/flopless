package com.github.idelstak.flopless.view;

import javafx.scene.control.*;
import javafx.scene.layout.*;

final class GridCell extends StackPane {

    private final String notation;
    private final int column;
    private final int row;

    GridCell(String notation, int column, int row) {
        this.notation = notation;
        this.column = column;
        this.row = row;

        setupCell();
        setupLabel();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append('{');
        sb.append(notation);
        sb.append(" [").append(column);
        sb.append(", ").append(row).append(']');
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
}

package com.github.idelstak.flopless.view;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;

public class RangeGrid_ {

    @FXML
    private StackPane rootPane;
    @FXML
    private GridPane handGrid;
    @FXML
    private Pane interactionLayer;
    @FXML
    private Rectangle selectionBox;
    private static final String[] CARDS = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
    private static final int GRID_SIZE = 13;
    private static final long LONG_PRESS_DELAY = 220;
    private static final double MOUSE_MOVE_THRESHOLD = 8.0;
    private RangeGridCell_[][] cells = new RangeGridCell_[GRID_SIZE][GRID_SIZE];
    private boolean isDragging = false;
    private boolean isDeselecting = false;
    private Timer longPressTimer;
    private Point2D mouseDownPos;
    private int startRow, startCol;

    @FXML
    public void initialize() {
        initGrid();
        setupEventHandlers();
    }

    private void initGrid() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                String hand = (r == c) ? CARDS[r] + CARDS[c] : (r < c) ? CARDS[r] + CARDS[c] + "s"
                                                               : CARDS[c] + CARDS[r] + "o";
                RangeGridCell_ cell = new RangeGridCell_(r, c, hand);
                cells[r][c] = cell;
                handGrid.add(cell, c, r);
            }
        }
    }

    private void setupEventHandlers() {
        interactionLayer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        interactionLayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        interactionLayer.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent e) {
        mouseDownPos = new Point2D(e.getScreenX(), e.getScreenY());
        Point2D localPos = interactionLayer.screenToLocal(e.getScreenX(), e.getScreenY());
        startRow = (int) (localPos.getY() / (interactionLayer.getHeight() / GRID_SIZE));
        startCol = (int) (localPos.getX() / (interactionLayer.getWidth() / GRID_SIZE));

        longPressTimer = new Timer(true);
        longPressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> startAreaSelection());
            }
        }, LONG_PRESS_DELAY);
    }

    private void handleMouseDragged(MouseEvent e) {
        if (longPressTimer != null && mouseDownPos.distance(e.getScreenX(), e.getScreenY()) > MOUSE_MOVE_THRESHOLD) {
            longPressTimer.cancel();
            longPressTimer = null;
        }

        if (isDragging) {
            updateSelection(e);
        }
    }

    private void handleMouseReleased(MouseEvent e) {
        if (longPressTimer != null) {
            longPressTimer.cancel();
            longPressTimer = null;
            if (startRow >= 0 && startRow < GRID_SIZE && startCol >= 0 && startCol < GRID_SIZE) {
                cells[startRow][startCol].toggleActive();
            }
        }

        if (isDragging) {
            finalizeSelection();
        }
    }

    private void startAreaSelection() {
        if (longPressTimer != null) {
            longPressTimer.cancel();
            longPressTimer = null;
        }
        isDragging = true;
        interactionLayer.getStyleClass().add("dragging");
        if (startRow >= 0 && startRow < GRID_SIZE && startCol >= 0 && startCol < GRID_SIZE) {
            isDeselecting = cells[startRow][startCol].isActive();
        }
        selectionBox.setVisible(true);
    }

    private void updateSelection(MouseEvent e) {
        Point2D localPos = interactionLayer.screenToLocal(e.getScreenX(), e.getScreenY());
        int currentRow = (int) (localPos.getY() / (interactionLayer.getHeight() / GRID_SIZE));
        int currentCol = (int) (localPos.getX() / (interactionLayer.getWidth() / GRID_SIZE));

        int rStart = Math.min(startRow, currentRow);
        int rEnd = Math.max(startRow, currentRow);
        int cStart = Math.min(startCol, currentCol);
        int cEnd = Math.max(startCol, currentCol);

        rStart = Math.max(0, rStart);
        rEnd = Math.min(GRID_SIZE - 1, rEnd);
        cStart = Math.max(0, cStart);
        cEnd = Math.min(GRID_SIZE - 1, cEnd);

        double cellWidth = interactionLayer.getWidth() / GRID_SIZE;
        double cellHeight = interactionLayer.getHeight() / GRID_SIZE;

        selectionBox.setX(cStart * cellWidth);
        selectionBox.setY(rStart * cellHeight);
        selectionBox.setWidth((cEnd - cStart + 1) * cellWidth);
        selectionBox.setHeight((rEnd - rStart + 1) * cellHeight);

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                boolean isInside = (r >= rStart && r <= rEnd && c >= cStart && c <= cEnd);
                cells[r][c].clearPreview();
                if (isInside) {
                    cells[r][c].setPreview(!isDeselecting);
                }
            }
        }
    }

    private void finalizeSelection() {
        isDragging = false;
        interactionLayer.getStyleClass().remove("dragging");
        selectionBox.setVisible(false);

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (cells[r][c].getStyleClass().contains("preview-add")) {
                    cells[r][c].setActive(true);
                } else if (cells[r][c].getStyleClass().contains("preview-remove")) {
                    cells[r][c].setActive(false);
                }
                cells[r][c].clearPreview();
            }
        }
    }
}

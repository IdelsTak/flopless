package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

public final class GridView implements Initializable {

    private final Grid grid;
    @FXML
    private GridPane handGrid;
    @FXML
    private Pane interactionLayer;
    @FXML
    private Rectangle selectionBox;

    public GridView() {
        grid = new Grid();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupGrid();
    }

    private void setupGrid() {
        Platform.runLater(this::addCells);
    }

    private void addCells() {
        for (int column = 0; column < grid.cells().size(); column++) {
            var rows = grid.cells().get(column);
            for (int row = 0; row < rows.size(); row++) {
                var cell = rows.get(row);
                var gridCell = new GridCell(cell.cards().notation(), column, row);
                handGrid.add(gridCell, column, row);
            }
        }
    }
}

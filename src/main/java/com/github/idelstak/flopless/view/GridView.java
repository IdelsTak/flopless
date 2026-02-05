package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;

public final class GridView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private final Grid grid;
    @FXML
    private GridPane handGrid;
    @FXML
    private Pane interactionLayer;
    private DisposableObserver<FloplessState> observe;
    @FXML
    private Rectangle selectionBox;

    public GridView(Stage stage, FloplessLoop loop) {
        this.stage = stage;
        this.loop = loop;
        this.grid = new Grid();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGrid();
        initInteractionLayer();
        initSubscription();
    }

    private void initGrid() {
        for (int column = 0; column < grid.cells().size(); column++) {
            var rows = grid.cells().get(column);
            for (int row = 0; row < rows.size(); row++) {
                var cell = rows.get(row);
                var gridCell = new GridCell(stage, loop, cell.cards().notation(), new Coordinate(column, row));
                handGrid.add(gridCell, column, row);
            }
        }
    }

    private void initInteractionLayer() {
        interactionLayer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            loop.accept(new Action.User.StartDrag(coordinateFrom(event.getX(), event.getY())));
        });

        interactionLayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            loop.accept(new Action.User.UpdatePreview(coordinateFrom(event.getX(), event.getY())));
        });

        interactionLayer.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            loop.accept(new Action.User.CommitRange());
        });
    }

    private Coordinate coordinateFrom(double x, double y) {
        double cellWidth = handGrid.getWidth() / 13;
        double cellHeight = handGrid.getHeight() / 13;

        int column = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);

        // Clamp values between 0 and 12
        column = Math.max(0, Math.min(12, column));
        row = Math.max(0, Math.min(12, row));

        return new Coordinate(column, row);
    }

    private void initSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(FloplessState state) {
                // The GridView itself doesn't need to render, the cells do.
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

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

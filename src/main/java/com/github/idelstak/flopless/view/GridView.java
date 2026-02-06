package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.DisposableObserver;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public final class GridView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private final Grid grid;
    private DisposableObserver<FloplessState> observe;
    private boolean marqueeActive;
    private double dragStartX;
    private double dragStartY;
    @FXML
    private GridPane handGrid;
    @FXML
    private Pane interactionLayer;
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
        selectionBox.setVisible(false);

        interactionLayer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            dragStartX = event.getX();
            dragStartY = event.getY();
            marqueeActive = true;
            interactionLayer.getStyleClass().add("dragging");
            selectionBox.setVisible(true);
            selectionBox.setX(dragStartX);
            selectionBox.setY(dragStartY);
            selectionBox.setWidth(0);
            selectionBox.setHeight(0);
            loop.accept(new Action.User.StartDrag(coordinateFrom(dragStartX, dragStartY)));
        });

        interactionLayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (!marqueeActive) {
                return;
            }

            event.consume();

            double x = event.getX();
            double y = event.getY();

            selectionBox.setX(Math.min(dragStartX, x));
            selectionBox.setY(Math.min(dragStartY, y));
            selectionBox.setWidth(Math.abs(dragStartX - x));
            selectionBox.setHeight(Math.abs(dragStartY - y));

            loop.accept(new Action.User.UpdatePreview(coordinateFrom(x, y)));
        });

        interactionLayer.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (marqueeActive) {
                event.consume();
                interactionLayer.getStyleClass().remove("dragging");
                loop.accept(new Action.User.UpdatePreview(coordinateFrom(event.getX(), event.getY())));
                loop.accept(new Action.User.CommitRange());
                selectionBox.setVisible(false);
            }

            marqueeActive = false;
        });

    }

    private Coordinate coordinateFrom(double x, double y) {
        double cellWidth = handGrid.getWidth() / 13;
        double cellHeight = handGrid.getHeight() / 13;

        int column = Math.max(0, Math.min(12, (int) (x / cellWidth)));
        int row = Math.max(0, Math.min(12, (int) (y / cellHeight)));

        return new Coordinate(column, row);
    }

    private void initSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(FloplessState state) {
            }

            @Override
            public void onError(Throwable e) {
                throw new IllegalStateException(e);
            }

            @Override
            public void onComplete() {
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

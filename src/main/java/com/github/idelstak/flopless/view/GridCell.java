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
    private final Coordinate coordinate;
    private DisposableObserver<History<FloplessState>> observe;
    private boolean isSelected;
    private Label handLabel;
    private Label amountLabel;

    GridCell(Stage stage, FloplessLoop loop, Coordinate coordinate) {
        this.stage = stage;
        this.loop = loop;
        this.coordinate = coordinate;

        setupCell();
        setupLabel();
        setupSubscription();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append('{');
        sb.append(coordinate.hand());
        sb.append(" [").append(coordinate.column());
        sb.append(", ").append(coordinate.row()).append(']');
        sb.append('}');
        return sb.toString();
    }

    private void setupCell() {
        getStyleClass().add("cell");
    }

    private void setupLabel() {
        handLabel = new Label(coordinate.hand());
        handLabel.getStyleClass().add("hand-label");
        amountLabel = new Label();
        amountLabel.getStyleClass().add("raise-amount-label");
        // amountLabel.setManaged(false);
        amountLabel.setVisible(false);
        var content = new VBox(2, handLabel, amountLabel);
        content.setFillWidth(false);
        getChildren().add(content);
    }

    private void setupSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(History<FloplessState> history) {
                Platform.runLater(() -> render(history.present()));
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
            var gridAction = state.selectedRange().actionAt(coordinate);
            setBackground(Background.fill(gridAction.color()));
            handLabel.setTextFill(Color.WHITE);
            if (gridAction instanceof GridAction.Raise raise) {
                amountLabel.setText(formatAmount(raise.amount()) + "BB");
                // amountLabel.setManaged(true);
                amountLabel.setVisible(true);
            } else {
               //  amountLabel.setManaged(false);
                amountLabel.setVisible(false);
            }
        } else {
            // amountLabel.setManaged(false);
            amountLabel.setVisible(false);
        }
        var isPreviewed = state.startCoordinate().isPresent()
          && state.previewRange().coordinates().contains(coordinate);
        if (isSelected && isPreviewed) {
            setBackground(Background.fill(Color.rgb(244, 67, 54, 0.5)));
            handLabel.setTextFill(Color.WHITE);
        } else if (!isSelected && isPreviewed) {
            setBackground(Background.fill(Color.rgb(76, 175, 80, 0.5)));
            handLabel.setTextFill(Color.WHITE);
        }
    }

    private String formatAmount(java.math.BigDecimal value) {
        return value.doubleValue() % 1 == 0
                 ? String.format("%.0f", value.doubleValue())
                 : String.format("%.1f", value.doubleValue());
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

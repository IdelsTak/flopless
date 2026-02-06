package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.poker.action.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.math.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public final class ActionSidebarView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private DisposableObserver<FloplessState> observe;
    private BigDecimal raiseAmount;
    private BigDecimal perLimperAmount;
    @FXML
    private RadioButton foldRadio;
    @FXML
    private ToggleGroup actionGroup;
    @FXML
    private RadioButton checkRadio;
    @FXML
    private RadioButton callRadio;
    @FXML
    private RadioButton raiseRadio;
    @FXML
    private RadioButton allinRadio;
    @FXML
    private TextField limperAmountField;
    @FXML
    private Button decrementActionAmountButton;
    @FXML
    private TextField actionAmountField;
    @FXML
    private Button incrementActionAmountButton;
    @FXML
    private Button decrementLimperAmountButton;
    @FXML
    private Button incrementLimperAmountButton;
    @FXML
    private VBox raiseAmountEdit;
    @FXML
    private VBox limpersEdit;

    public ActionSidebarView(Stage stage, FloplessLoop loop) {
        this.stage = stage;
        this.loop = loop;

        raiseAmount = BigDecimal.ZERO;
        perLimperAmount = BigDecimal.ZERO;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupActions();
        setupSubscription();
    }

    private void setupActions() {
        foldRadio.setOnAction(_ -> {
            if (foldRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Fold()));
            }
        });
        checkRadio.setOnAction(_ -> {
            if (checkRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Check()));
            }
        });
        callRadio.setOnAction(_ -> {
            if (callRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Call()));
            }
        });
        allinRadio.setOnAction(_ -> {
            if (allinRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.AllIn()));
            }
        });
        raiseRadio.setOnAction(_ -> {
            if (raiseRadio.isSelected()) {
                loop.accept(new Action.Effect.GridActionSelected(new GridAction.Raise(raiseAmount)));
            }
        });
        actionAmountField.setOnAction(_ -> {
            var text = actionAmountField.getText();
            if (text == null || text.isBlank()) {
                return;
            }
            double raise = raiseAmount.doubleValue();
            try {
                raise = Double.parseDouble(text);
            } catch (NumberFormatException _) {
            }
            loop.accept(new Action.User.RaiseAmount(raise));
        });
        incrementActionAmountButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseRaiseAmount(0.5)));
        decrementActionAmountButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseRaiseAmount(-0.5)));
        limperAmountField.setOnAction(_ -> {
            var text = limperAmountField.getText();
            if (text == null || text.isBlank()) {
                return;
            }
            BigDecimal amount = perLimperAmount;
            try {
                amount = new BigDecimal(text);
            } catch (NumberFormatException _) {
            }
            loop.accept(new Action.User.LimperAmount(amount.doubleValue()));
        });
        incrementLimperAmountButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseLimperAmount(0.5)));
        decrementLimperAmountButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseLimperAmount(-0.5)));
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
        var action = state.selectedAction().action();
        var isRaise = action instanceof GameAction.Money.Raise;
        raiseAmountEdit.setDisable(!isRaise);
        limpersEdit.setDisable(!isRaise);

        raiseAmount = state.raiseAmount();
        actionAmountField.setText(raiseAmount.doubleValue() % 1 == 0
                                    ? String.format("%.0f", raiseAmount.doubleValue())
                                    : String.format("%.1f", raiseAmount.doubleValue()));

        perLimperAmount = state.perLimperAmount();
        limperAmountField.setText(perLimperAmount.doubleValue() % 1 == 0
                                    ? String.format("%.0f", perLimperAmount.doubleValue())
                                    : String.format("%.1f", perLimperAmount.doubleValue()));
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}

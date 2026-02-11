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
    private DisposableObserver<History<FloplessState>> observe;
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
    private Button decrementLimperCountButton;
    @FXML
    private Button incrementLimperCountButton;
    @FXML
    private TextField limperCountField;
    @FXML
    private TextField threeBetIpField;
    @FXML
    private Button decrementThreeBetIpButton;
    @FXML
    private Button incrementThreeBetIpButton;
    @FXML
    private TextField threeBetOopField;
    @FXML
    private Button decrementThreeBetOopButton;
    @FXML
    private Button incrementThreeBetOopButton;
    @FXML
    private TextField aaOverrideField;
    @FXML
    private TextField kkOverrideField;
    @FXML
    private TextField qqOverrideField;
    @FXML
    private TextField aksOverrideField;
    @FXML
    private TextField akoOverrideField;
    @FXML
    private VBox raiseAmountEdit;
    @FXML
    private VBox limpersEdit;
    @FXML
    private VBox threeBetEdit;
    @FXML
    private VBox premiumEdit;

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
            loop.accept(new Action.User.LimperAmount(readDouble(limperAmountField, perLimperAmount.doubleValue())));
        });
        incrementLimperAmountButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseLimperAmount(0.5)));
        decrementLimperAmountButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseLimperAmount(-0.5)));

        limperCountField.setOnAction(_ ->
          loop.accept(new Action.User.LimperCount(readInt(limperCountField, 0))));
        incrementLimperCountButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseLimperCount(1)));
        decrementLimperCountButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseLimperCount(1)));

        threeBetIpField.setOnAction(_ ->
          loop.accept(new Action.User.ThreeBetIpMultiplier(readDouble(threeBetIpField, 3.0))));
        incrementThreeBetIpButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseThreeBetIpMultiplier(0.5)));
        decrementThreeBetIpButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseThreeBetIpMultiplier(-0.5)));

        threeBetOopField.setOnAction(_ ->
          loop.accept(new Action.User.ThreeBetOopMultiplier(readDouble(threeBetOopField, 4.0))));
        incrementThreeBetOopButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseThreeBetOopMultiplier(0.5)));
        decrementThreeBetOopButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseThreeBetOopMultiplier(-0.5)));

        aaOverrideField.setOnAction(_ -> applyPremiumOverride("AA", aaOverrideField));
        kkOverrideField.setOnAction(_ -> applyPremiumOverride("KK", kkOverrideField));
        qqOverrideField.setOnAction(_ -> applyPremiumOverride("QQ", qqOverrideField));
        aksOverrideField.setOnAction(_ -> applyPremiumOverride("AKs", aksOverrideField));
        akoOverrideField.setOnAction(_ -> applyPremiumOverride("AKo", akoOverrideField));
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
        var action = state.selectedAction().gameAction();
        var isRaise = action instanceof GameAction.Money.Raise;
        raiseAmountEdit.setDisable(!isRaise);
        limpersEdit.setDisable(!(isRaise && state.squeezeLimpers()));
        threeBetEdit.setDisable(!(isRaise && state.facing() instanceof com.github.idelstak.flopless.poker.player.Facing.Raised));
        premiumEdit.setDisable(!isRaise);

        raiseAmount = state.raiseAmount();
        actionAmountField.setText(formatDecimal(raiseAmount));

        perLimperAmount = state.perLimperAmount();
        limperAmountField.setText(formatDecimal(perLimperAmount));
        limperCountField.setText(String.valueOf(state.limperCount()));
        threeBetIpField.setText(formatDecimal(state.threeBetIpMultiplier()));
        threeBetOopField.setText(formatDecimal(state.threeBetOopMultiplier()));

        aaOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AA", state.raiseAmount())));
        kkOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("KK", state.raiseAmount())));
        qqOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("QQ", state.raiseAmount())));
        aksOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AKs", state.raiseAmount())));
        akoOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AKo", state.raiseAmount())));

        var toSelect = actionGroup.getToggles()
          .stream().filter(t -> ((Labeled) t).getText().equalsIgnoreCase(action.displayLabel()))
          .findFirst()
          .orElseThrow();

        actionGroup.selectToggle(toSelect);
    }

    private void applyPremiumOverride(String hand, TextField source) {
        loop.accept(new Action.User.PremiumRaiseOverride(hand, readDouble(source, raiseAmount.doubleValue())));
    }

    private double readDouble(TextField field, double fallback) {
        var text = field.getText();
        if (text == null || text.isBlank()) {
            return fallback;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException _) {
            return fallback;
        }
    }

    private int readInt(TextField field, int fallback) {
        var text = field.getText();
        if (text == null || text.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException _) {
            return fallback;
        }
    }

    private String formatDecimal(BigDecimal value) {
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

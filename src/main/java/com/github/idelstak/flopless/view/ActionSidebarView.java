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
    private TextField reraisedIpField;
    @FXML
    private Button decrementReraisedIpButton;
    @FXML
    private Button incrementReraisedIpButton;
    @FXML
    private TextField reraisedOopField;
    @FXML
    private Button decrementReraisedOopButton;
    @FXML
    private Button incrementReraisedOopButton;
    @FXML
    private Button decrementAaOverrideButton;
    @FXML
    private Button incrementAaOverrideButton;
    @FXML
    private TextField aaOverrideField;
    @FXML
    private Button decrementKkOverrideButton;
    @FXML
    private Button incrementKkOverrideButton;
    @FXML
    private TextField kkOverrideField;
    @FXML
    private Button decrementQqOverrideButton;
    @FXML
    private Button incrementQqOverrideButton;
    @FXML
    private TextField qqOverrideField;
    @FXML
    private Button decrementJjOverrideButton;
    @FXML
    private Button incrementJjOverrideButton;
    @FXML
    private TextField jjOverrideField;
    @FXML
    private Button decrementAksOverrideButton;
    @FXML
    private Button incrementAksOverrideButton;
    @FXML
    private TextField aksOverrideField;
    @FXML
    private Button decrementAkoOverrideButton;
    @FXML
    private Button incrementAkoOverrideButton;
    @FXML
    private TextField akoOverrideField;
    @FXML
    private Button decrementAqsOverrideButton;
    @FXML
    private Button incrementAqsOverrideButton;
    @FXML
    private TextField aqsOverrideField;
    @FXML
    private VBox raiseAmountEdit;
    @FXML
    private VBox limpersEdit;
    @FXML
    private VBox reraisedEdit;
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

        reraisedIpField.setOnAction(_ ->
          loop.accept(new Action.User.ReraisedIpMultiplier(readDouble(reraisedIpField, 3.0))));
        incrementReraisedIpButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseReraisedIpMultiplier(0.5)));
        decrementReraisedIpButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseReraisedIpMultiplier(-0.5)));

        reraisedOopField.setOnAction(_ ->
          loop.accept(new Action.User.ReraisedOopMultiplier(readDouble(reraisedOopField, 4.0))));
        incrementReraisedOopButton.setOnAction(_ ->
          loop.accept(new Action.User.IncreaseReraisedOopMultiplier(0.5)));
        decrementReraisedOopButton.setOnAction(_ ->
          loop.accept(new Action.User.DecreaseReraisedOopMultiplier(-0.5)));
        configurePremiumField("AA", aaOverrideField, decrementAaOverrideButton, incrementAaOverrideButton);
        configurePremiumField("KK", kkOverrideField, decrementKkOverrideButton, incrementKkOverrideButton);
        configurePremiumField("QQ", qqOverrideField, decrementQqOverrideButton, incrementQqOverrideButton);
        configurePremiumField("JJ", jjOverrideField, decrementJjOverrideButton, incrementJjOverrideButton);
        configurePremiumField("AKs", aksOverrideField, decrementAksOverrideButton, incrementAksOverrideButton);
        configurePremiumField("AKo", akoOverrideField, decrementAkoOverrideButton, incrementAkoOverrideButton);
        configurePremiumField("AQs", aqsOverrideField, decrementAqsOverrideButton, incrementAqsOverrideButton);
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
        reraisedEdit.setDisable(!(isRaise && state.facing() instanceof com.github.idelstak.flopless.poker.player.Facing.ReRaised));
        premiumEdit.setDisable(!isRaise);

        raiseAmount = state.raiseAmount();
        actionAmountField.setText(formatDecimal(raiseAmount));

        perLimperAmount = state.perLimperAmount();
        limperAmountField.setText(formatDecimal(perLimperAmount));
        reraisedIpField.setText(formatDecimal(state.reraisedIpMultiplier()));
        reraisedOopField.setText(formatDecimal(state.reraisedOopMultiplier()));

        aaOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AA", state.raiseAmount())));
        kkOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("KK", state.raiseAmount())));
        qqOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("QQ", state.raiseAmount())));
        jjOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("JJ", state.raiseAmount())));
        aksOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AKs", state.raiseAmount())));
        akoOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AKo", state.raiseAmount())));
        aqsOverrideField.setText(formatDecimal(state.premiumRaiseOverridesBb().getOrDefault("AQs", state.raiseAmount())));

        var toSelect = actionGroup.getToggles()
          .stream().filter(t -> ((Labeled) t).getText().equalsIgnoreCase(action.displayLabel()))
          .findFirst()
          .orElseThrow();

        actionGroup.selectToggle(toSelect);
    }

    private void configurePremiumField(String hand, TextField field, Button decrement, Button increment) {
        field.setOnAction(_ -> loop.accept(new Action.User.PremiumRaiseOverride(hand, readDouble(field, raiseAmount.doubleValue()))));
        increment.setOnAction(_ -> nudgePremium(hand, field, 0.5));
        decrement.setOnAction(_ -> nudgePremium(hand, field, -0.5));
    }

    private void nudgePremium(String hand, TextField field, double delta) {
        var current = readDouble(field, raiseAmount.doubleValue());
        var next = Math.max(1.0, Math.min(30.0, current + delta));
        field.setText(formatDecimal(BigDecimal.valueOf(next)));
        loop.accept(new Action.User.PremiumRaiseOverride(hand, next));
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

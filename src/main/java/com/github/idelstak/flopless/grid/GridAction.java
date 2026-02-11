package com.github.idelstak.flopless.grid;

import com.github.idelstak.flopless.poker.action.*;
import java.math.*;
import javafx.scene.paint.*;

public sealed interface GridAction {

    GameAction gameAction();

    Color color();

    public static GridAction fromSimpleLabel(String value) {
        if (value.contains("bb")) {
            var amountText = value.replaceAll("bb", "");
            return new Raise(new BigDecimal(amountText));
        }
        return switch (value) {
            case "check" ->
                new Check();
            case "call" ->
                new Call();
            case "allin" ->
                new AllIn();
            case "fold" ->
                new Fold();
            default ->
                throw new IllegalArgumentException("Unknown action: " + value);
        };
    }

    record Raise(BigDecimal amount) implements GridAction {

        @Override
        public GameAction gameAction() {
            return new GameAction.Money.Raise(amount);
        }

        @Override
        public Color color() {
            return Color.web("#A16207");
        }
    }

    record Check() implements GridAction {

        @Override
        public GameAction gameAction() {
            return new GameAction.UknownMoney.Check();
        }

        @Override
        public Color color() {
            return Color.web("#3730A3");
        }
    }

    record Call() implements GridAction {

        @Override
        public GameAction gameAction() {
            return new GameAction.UknownMoney.Call();
        }

        @Override
        public Color color() {
            return Color.web("#0E7490");
        }
    }

    record AllIn() implements GridAction {

        @Override
        public GameAction gameAction() {
            return new GameAction.UknownMoney.AllIn();
        }

        @Override
        public Color color() {
            return Color.web("#6A0DAD");
        }
    }

    record Fold() implements GridAction {

        @Override
        public GameAction gameAction() {
            return new GameAction.UknownMoney.Fold();
        }

        @Override
        public Color color() {
            return Color.web("#616161");
        }
    }
}

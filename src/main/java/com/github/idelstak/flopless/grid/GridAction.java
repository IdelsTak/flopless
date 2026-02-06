package com.github.idelstak.flopless.grid;

import com.github.idelstak.flopless.poker.action.*;
import javafx.scene.paint.*;

public sealed interface GridAction {

    GameAction action();

    Color color();

    record Raise(GameAction action) implements GridAction {

        @Override
        public Color color() {
            return Color.web("#ff8c00");
        }
    }

    record Check() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.Check();
        }

        @Override
        public Color color() {
            return Color.web("#ffd700");
        }
    }

    record Call() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.Call();
        }

        @Override
        public Color color() {
            return Color.web("#00bfff");
        }
    }

    record AllIn() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.AllIn();
        }

        @Override
        public Color color() {
            return Color.web("#9400d3");
        }
    }

    record Fold() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.Fold();
        }

        @Override
        public Color color() {
            return Color.web("#9e9e9e");
        }
    }
}

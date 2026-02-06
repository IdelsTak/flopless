package com.github.idelstak.flopless.grid;

import com.github.idelstak.flopless.poker.action.*;
import javafx.scene.paint.*;

public sealed interface GridAction {

    GameAction action();

    Color color();

    record Raise(GameAction action) implements GridAction {

        @Override
        public Color color() {
            return Color.web("#CC6600");
        }
    }

    record Check() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.Check();
        }

        @Override
        public Color color() {
            return Color.web("#B8860B");
        }
    }

    record Call() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.Call();
        }

        @Override
        public Color color() {
            return Color.web("#0073B3");
        }
    }

    record AllIn() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.AllIn();
        }

        @Override
        public Color color() {
            return Color.web("#6A0DAD");
        }
    }

    record Fold() implements GridAction {

        @Override
        public GameAction action() {
            return new GameAction.UknownMoney.Fold();
        }

        @Override
        public Color color() {
            return Color.web("#616161");
        }
    }
}

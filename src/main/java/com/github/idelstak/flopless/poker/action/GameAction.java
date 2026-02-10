package com.github.idelstak.flopless.poker.action;

import java.math.*;
import java.util.*;

public sealed interface GameAction {

    String simpleLabel();

    String displayLabel();

    sealed interface Money extends GameAction {

        BigDecimal amount();

        record Raise(BigDecimal amount) implements Money {

            @Override
            public String simpleLabel() {
                return displayLabel().toLowerCase(Locale.ROOT);
            }

            @Override
            public String displayLabel() {
                return "Raise";
            }
        }
    }

    sealed interface UknownMoney extends GameAction {

        record Check() implements UknownMoney {

            @Override
            public String simpleLabel() {
                return displayLabel().toLowerCase(Locale.ROOT);
            }

            @Override
            public String displayLabel() {
                return "Check";
            }
        }

        record Call() implements UknownMoney {

            @Override
            public String simpleLabel() {
                return displayLabel().toLowerCase(Locale.ROOT);
            }

            @Override
            public String displayLabel() {
                return "Call";
            }
        }

        record AllIn() implements UknownMoney {

            @Override
            public String simpleLabel() {
                return displayLabel().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
            }

            @Override
            public String displayLabel() {
                return "All In";
            }
        }

        record Fold() implements UknownMoney {

            @Override
            public String simpleLabel() {
                return displayLabel().toLowerCase(Locale.ROOT);
            }

            @Override
            public String displayLabel() {
                return "Fold";
            }
        }
    }
}

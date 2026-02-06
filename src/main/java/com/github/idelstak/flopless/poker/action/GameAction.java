package com.github.idelstak.flopless.poker.action;

import java.math.*;

public sealed interface GameAction {

    String label();

    sealed interface Money extends GameAction {

        BigDecimal amount();

        record Raise(BigDecimal amount) implements Money {

            @Override
            public String label() {
                return "Raise";
            }
        }
    }

    sealed interface UknownMoney extends GameAction {

        record Check() implements UknownMoney {

            @Override
            public String label() {
                return "Check";
            }
        }

        record Call() implements UknownMoney {

            @Override
            public String label() {
                return "Call";
            }
        }

        record AllIn() implements UknownMoney {

            @Override
            public String label() {
                return "All In";
            }
        }

        record Fold() implements UknownMoney {

            @Override
            public String label() {
                return "Fold";
            }
        }
    }
}

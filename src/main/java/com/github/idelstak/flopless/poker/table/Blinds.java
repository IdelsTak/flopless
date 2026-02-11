package com.github.idelstak.flopless.poker.table;

public sealed interface Blinds {

    String label();

    record OneSbTwoBB() implements Blinds {

        @Override
        public String label() {
            return "1sb2bb";
        }
    }

    record TwoSbFourBB() implements Blinds {

        @Override
        public String label() {
            return "2sb4bb";
        }
    }
}

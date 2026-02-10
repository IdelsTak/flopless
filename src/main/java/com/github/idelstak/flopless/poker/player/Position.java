package com.github.idelstak.flopless.poker.player;

public sealed interface Position {

    int index();

    record Utg() implements Position {

        @Override public int index() {
            return 0;
        }
    }

    record Utg1() implements Position {

        @Override public int index() {
            return 1;
        }
    }

    record Utg2() implements Position {

        @Override public int index() {
            return 2;
        }
    }

    record Lj() implements Position {

        @Override public int index() {
            return 3;
        }
    }

    record Hj() implements Position {

        @Override public int index() {
            return 4;
        }
    }

    record Co() implements Position {

        @Override public int index() {
            return 5;
        }
    }

    record Btn() implements Position {

        @Override public int index() {
            return 6;
        }
    }

    record Sb() implements Position {

        @Override public int index() {
            return 7;
        }
    }

    record Bb() implements Position {

        @Override public int index() {
            return 8;
        }
    }
}

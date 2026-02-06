package com.github.idelstak.flopless.poker.hand;

public sealed interface Rank {

    int order();

    String text();

    record Two(int order, String text) implements Rank {

        public Two() {
            this(2, "2");
        }
    }

    record Three(int order, String text) implements Rank {

        public Three() {
            this(3, "3");
        }
    }

    record Four(int order, String text) implements Rank {

        public Four() {
            this(4, "4");
        }
    }

    record Five(int order, String text) implements Rank {

        public Five() {
            this(5, "5");
        }
    }

    record Six(int order, String text) implements Rank {

        public Six() {
            this(6, "6");
        }
    }

    record Seven(int order, String text) implements Rank {

        public Seven() {
            this(7, "7");
        }
    }

    record Eight(int order, String text) implements Rank {

        public Eight() {
            this(8, "8");
        }
    }

    record Nine(int order, String text) implements Rank {

        public Nine() {
            this(9, "9");
        }
    }

    record Ten(int order, String text) implements Rank {

        public Ten() {
            this(10, "T");
        }
    }

    record Jack(int order, String text) implements Rank {

        public Jack() {
            this(11, "J");
        }
    }

    record Queen(int order, String text) implements Rank {

        public Queen() {
            this(12, "Q");
        }
    }

    record King(int order, String text) implements Rank {

        public King() {
            this(13, "K");
        }
    }

    record Ace(int order, String text) implements Rank {

        public Ace() {
            this(14, "A");
        }
    }
}

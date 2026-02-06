package com.github.idelstak.flopless.poker.hand;

public sealed interface Suit {

    String text();

    record Spades(String text) implements Suit {

        public Spades() {
            this("s");
        }
    }

    record Hearts(String text) implements Suit {

        public Hearts() {
            this("h");
        }
    }

    record Diamonds(String text) implements Suit {

        public Diamonds() {
            this("d");
        }
    }

    record Clubs(String text) implements Suit {

        public Clubs() {
            this("c");
        }
    }
}

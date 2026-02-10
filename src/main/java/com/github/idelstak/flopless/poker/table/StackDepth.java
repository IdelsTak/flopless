package com.github.idelstak.flopless.poker.table;

public sealed interface StackDepth {

    String label();

    record Bb100() implements StackDepth {

        @Override
        public String label() {
            return "100bb";
        }
    }

    record Bb200() implements StackDepth {

        @Override
        public String label() {
            return "200bb";
        }
    }
}

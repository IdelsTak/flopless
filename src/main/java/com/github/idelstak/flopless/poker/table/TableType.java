package com.github.idelstak.flopless.poker.table;

public sealed interface TableType {

    StackDepth stack();

    Blinds blinds();

    String label();

    record SixMax(StackDepth stack, Blinds blinds) implements TableType {

        @Override
        public String label() {
            return "6max";
        }
    }

    record NineMax(StackDepth stack, Blinds blinds) implements TableType {

        @Override
        public String label() {
            return "9max";
        }
    }
}

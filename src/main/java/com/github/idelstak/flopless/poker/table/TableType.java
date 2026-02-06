package com.github.idelstak.flopless.poker.table;

public sealed interface TableType {

    record SixMax(StackDepth stacks) implements TableType {

    }

    record NineMax(StackDepth stacks) implements TableType {

    }
}

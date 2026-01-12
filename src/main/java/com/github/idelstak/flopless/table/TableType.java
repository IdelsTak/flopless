package com.github.idelstak.flopless.table;

public sealed interface TableType {

    record SixMax(StackDepth stacks) implements TableType {

    }

    record NineMax(StackDepth stacks) implements TableType {

    }
}

package com.github.idelstak.flopless.table;

public sealed interface TableType {

    record SixMax(Stacks stacks) implements TableType {

    }

    record NineMax(Stacks stacks) implements TableType {

    }
}

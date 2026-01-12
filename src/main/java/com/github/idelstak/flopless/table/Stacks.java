package com.github.idelstak.flopless.table;

public sealed interface Stacks {

    record Bb100() implements Stacks {
    }

    record Bb200() implements Stacks {
    }
}

package com.github.idelstak.flopless.poker.table;

public sealed interface StackDepth {

    record Bb100() implements StackDepth {
    }

    record Bb200() implements StackDepth {
    }
}

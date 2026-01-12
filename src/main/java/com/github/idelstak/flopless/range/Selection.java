package com.github.idelstak.flopless.range;

public sealed interface Selection {

    record Idle() implements Selection {
    }

    record Selecting() implements Selection {
    }

    record Erasing() implements Selection {
    }
}

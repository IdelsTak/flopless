package com.github.idelstak.flopless.range;

public sealed interface SelectMode {

    record Idle() implements SelectMode {
    }

    record Selecting() implements SelectMode {
    }

    record Erasing() implements SelectMode {
    }
}

package com.github.idelstak.flopless.poker.player;

public sealed interface Position {

    record Utg() implements Position {
    }

    record Utg1() implements Position {
    }

    record Utg2() implements Position {
    }

    record Lj() implements Position {
    }

    record Hj() implements Position {
    }

    record Co() implements Position {
    }

    record Btn() implements Position {
    }

    record Sb() implements Position {
    }

    record Bb() implements Position {
    }
}
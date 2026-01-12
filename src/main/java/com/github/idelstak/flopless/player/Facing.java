package com.github.idelstak.flopless.player;

public sealed interface Facing {

    record Open() implements Facing {
    }

    record VsUtg() implements Facing {
    }

    record VsUtg1() implements Facing {
    }

    record VsUtg2() implements Facing {
    }

    record VsLj() implements Facing {
    }

    record VsHj() implements Facing {
    }

    record VsCo() implements Facing {
    }

    record VsBtn() implements Facing {
    }

    record VsSb() implements Facing {
    }

    record Vs3Bet() implements Facing {
    }

    record Vs4Bet() implements Facing {
    }

    record Vs5Bet() implements Facing {
    }

    record VsAllIn() implements Facing {
    }
}

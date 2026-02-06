package com.github.idelstak.flopless.grid;

import com.github.idelstak.flopless.poker.hand.*;

public record Cell(HoleCards cards, GridAction action) {

    @Override
    public String toString() {
        return cards.notation();
    }
}

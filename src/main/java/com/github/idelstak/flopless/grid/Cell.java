package com.github.idelstak.flopless.grid;

import com.github.idelstak.flopless.hand.*;

public record Cell(HoleCards cards) {

    @Override
    public String toString() {
        return cards.notation();
    }
}

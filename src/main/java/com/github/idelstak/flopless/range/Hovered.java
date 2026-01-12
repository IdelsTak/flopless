package com.github.idelstak.flopless.range;

import com.github.idelstak.flopless.grid.*;
import java.util.*;

public record Hovered(Optional<Cell> maybeCell) {

    public static Hovered none() {
        return new Hovered(Optional.empty());
    }
}

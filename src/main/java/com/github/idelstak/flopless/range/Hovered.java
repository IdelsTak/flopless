package com.github.idelstak.flopless.range;

import com.github.idelstak.flopless.grid.*;
import java.util.*;

public record Hovered(Optional<Coordinate> maybeCoordinate) {

    public Hovered(Coordinate coordinate) {
        this(Optional.of(coordinate));
    }

    public static Hovered none() {
        return new Hovered(Optional.empty());
    }
}

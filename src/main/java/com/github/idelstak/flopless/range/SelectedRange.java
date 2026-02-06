package com.github.idelstak.flopless.range;

import com.github.idelstak.flopless.grid.*;
import java.util.*;

public record SelectedRange(Set<Coordinate> coordinates) {

    public SelectedRange {
        coordinates = Set.copyOf(coordinates);
    }

    public SelectedRange add(Coordinate coordinate) {
        var modifiable = new HashSet<>(coordinates);
        modifiable.add(coordinate);
        return new SelectedRange(modifiable);
    }

    public SelectedRange remove(Coordinate coordinate) {
        var modifiable = new HashSet<>(coordinates);
        modifiable.remove(coordinate);
        return new SelectedRange(modifiable);
    }

    public SelectedRange clear() {
        return new SelectedRange(Set.of());
    }

    public SelectedRange addRange(Coordinate start, Coordinate end) {
        var modifiable = new HashSet<>(coordinates);

        for (int col = Math.min(start.column(), end.column()); col <= Math.max(start.column(), end.column()); col++) {
            for (int row = Math.min(start.row(), end.row()); row <= Math.max(start.row(), end.row()); row++) {
                modifiable.add(new Coordinate(col, row));
            }
        }
        return new SelectedRange(modifiable);
    }

    public SelectedRange removeRange(Coordinate start, Coordinate end) {
        var modifiable = new HashSet<>(coordinates);

        for (int col = Math.min(start.column(), end.column()); col <= Math.max(start.column(), end.column()); col++) {
            for (int row = Math.min(start.row(), end.row()); row <= Math.max(start.row(), end.row()); row++) {
                modifiable.remove(new Coordinate(col, row));
            }
        }
        return new SelectedRange(modifiable);
    }

    public SelectedRange all() {
        var allCoordinates = new HashSet<Coordinate>();
        for (int col = 0; col < 13; col++) {
            for (int row = 0; row < 13; row++) {
                allCoordinates.add(new Coordinate(col, row));
            }
        }
        return new SelectedRange(allCoordinates);
    }

    public SelectedRange intersect(SelectedRange other) {
        var modifiable = new HashSet<>(coordinates);
        modifiable.retainAll(other.coordinates);
        return new SelectedRange(modifiable);
    }

    public SelectedRange union(SelectedRange other) {
        var modifiable = new HashSet<>(coordinates);
        modifiable.addAll(other.coordinates);
        return new SelectedRange(modifiable);
    }

    public SelectedRange difference(SelectedRange other) {
        var modifiable = new HashSet<>(coordinates);
        modifiable.removeAll(other.coordinates);
        return new SelectedRange(modifiable);
    }

    public static SelectedRange none() {
        return new SelectedRange(Set.of());
    }
}

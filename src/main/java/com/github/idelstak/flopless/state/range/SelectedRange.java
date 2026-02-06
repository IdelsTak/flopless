package com.github.idelstak.flopless.state.range;

import com.github.idelstak.flopless.grid.*;
import java.util.*;

public final class SelectedRange {

    private final Map<Coordinate, GridAction> map;

    private SelectedRange(Map<Coordinate, GridAction> map) {
        this.map = Map.copyOf(map);
    }

    public SelectedRange add(Coordinate coordinate, GridAction action) {
        var modifiable = new HashMap<>(map);
        modifiable.put(coordinate, action);
        return new SelectedRange(modifiable);
    }

    public SelectedRange remove(Coordinate coordinate) {
        var modifiable = new HashMap<>(map);
        modifiable.remove(coordinate);
        return new SelectedRange(modifiable);
    }

    public SelectedRange clear() {
        return new SelectedRange(Map.of());
    }

    public SelectedRange addRange(Coordinate start, Coordinate end, GridAction action) {
        var modifiable = new HashMap<>(map);
        for (int col = Math.min(start.column(), end.column()); col <= Math.max(start.column(), end.column()); col++) {
            for (int row = Math.min(start.row(), end.row()); row <= Math.max(start.row(), end.row()); row++) {
                modifiable.put(new Coordinate(col, row), action);
            }
        }
        return new SelectedRange(modifiable);
    }

    public SelectedRange removeRange(Coordinate start, Coordinate end) {
        var modifiable = new HashMap<>(map);
        for (int col = Math.min(start.column(), end.column()); col <= Math.max(start.column(), end.column()); col++) {
            for (int row = Math.min(start.row(), end.row()); row <= Math.max(start.row(), end.row()); row++) {
                modifiable.remove(new Coordinate(col, row));
            }
        }
        return new SelectedRange(modifiable);
    }

    public SelectedRange all(GridAction action) {
        var allCoordinates = new HashMap<Coordinate, GridAction>();
        for (int col = 0; col < 13; col++) {
            for (int row = 0; row < 13; row++) {
                allCoordinates.put(new Coordinate(col, row), action);
            }
        }
        return new SelectedRange(allCoordinates);
    }

    public SelectedRange intersect(SelectedRange other) {
        var modifiable = new HashMap<Coordinate, GridAction>();
        for (var entry : map.entrySet()) {
            if (other.map.containsKey(entry.getKey())) {
                modifiable.put(entry.getKey(), entry.getValue());
            }
        }
        return new SelectedRange(modifiable);
    }

    public SelectedRange union(SelectedRange other) {
        var modifiable = new HashMap<>(map);
        modifiable.putAll(other.map);
        return new SelectedRange(modifiable);
    }

    public SelectedRange difference(SelectedRange other) {
        var modifiable = new HashMap<>(map);
        for (var key : other.map.keySet()) {
            modifiable.remove(key);
        }
        return new SelectedRange(modifiable);
    }

    public GridAction actionAt(Coordinate coordinate) {
        return map.getOrDefault(coordinate, new GridAction.Fold());
    }

    public Set<Coordinate> coordinates() {
        return map.keySet();
    }

    public Map<Coordinate, GridAction> map() {
        return Map.copyOf(map);
    }

    public static SelectedRange none() {
        return new SelectedRange(Map.of());
    }
}

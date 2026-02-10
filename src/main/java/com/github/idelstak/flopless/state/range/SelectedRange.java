package com.github.idelstak.flopless.state.range;

import com.github.idelstak.flopless.io.SerializedRange;
import com.github.idelstak.flopless.io.DeserializedRange;
import com.fasterxml.jackson.databind.annotation.*;
import com.github.idelstak.flopless.grid.*;
import java.util.*;

@JsonSerialize(using = SerializedRange.class)
@JsonDeserialize(using = DeserializedRange.class)
public final class SelectedRange {

    private final Map<Coordinate, GridAction> map;

    public SelectedRange(Map<Coordinate, GridAction> map) {
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
        var grid = new Grid();
        for (int col = Math.min(start.column(), end.column()); col <= Math.max(start.column(), end.column()); col++) {
            for (int row = Math.min(start.row(), end.row()); row <= Math.max(start.row(), end.row()); row++) {
                var hand = grid.cell(col, row).cards().notation();
                modifiable.put(new Coordinate(hand, col, row), action);
            }
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

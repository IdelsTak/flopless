package com.github.idelstak.flopless.range;

import com.github.idelstak.flopless.grid.*;
import java.util.*;

public record SelectedRange(Set<Cell> cells) {

    public SelectedRange {
        cells = Set.copyOf(cells);
    }

    public SelectedRange add(Cell cell) {
        var newSet = Set.copyOf(cells);
        var modifiable = new java.util.HashSet<>(newSet);
        modifiable.add(cell);
        return new SelectedRange(modifiable);
    }

    public SelectedRange remove(Cell cell) {
        var newSet = Set.copyOf(cells);
        var modifiable = new java.util.HashSet<>(newSet);
        modifiable.remove(cell);
        return new SelectedRange(modifiable);
    }

    public SelectedRange clear() {
        return new SelectedRange(Set.of());
    }
}

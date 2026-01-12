package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.range.*;
import com.github.idelstak.flopless.table.*;

public record FloplessState(
  TableType tableType,
  Position position,
  Facing facing,
  SelectedRange range,
  Selection selection,
  Hovered hovered
  ) {

    public FloplessState switchTable(TableType tableType) {
        return new FloplessState(tableType, position, facing, range, selection, hovered);
    }

    public FloplessState focus(Position position) {
        return new FloplessState(tableType, position, facing, range, selection, hovered);
    }

    public FloplessState face(Facing facing) {
        return new FloplessState(tableType, position, facing, range, selection, hovered);
    }

    public FloplessState selectRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, range, selection, hovered);
    }

    public FloplessState setSelection(Selection selection) {
        return new FloplessState(tableType, position, facing, range, selection, hovered);
    }

    public FloplessState hoverCell(Hovered hovered) {
        return new FloplessState(tableType, position, facing, range, selection, hovered);
    }
}

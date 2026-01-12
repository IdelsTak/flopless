package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.range.*;
import com.github.idelstak.flopless.table.*;

public record FloplessState(
  TableType tableType,
  Position position,
  Facing facing,
  SelectedRange selectedRange,
  SelectMode selectMode,
  Hovered hovered
  ) {

    public FloplessState forTable(TableType tableType) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, hovered);
    }

    public FloplessState forPosition(Position position) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, hovered);
    }

    public FloplessState face(Facing facing) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, hovered);
    }

    public FloplessState selectRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, range, selectMode, hovered);
    }

    public FloplessState selectMode(SelectMode mode) {
        return new FloplessState(tableType, position, facing, selectedRange, mode, hovered);
    }

    public FloplessState hover(Hovered hovered) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, hovered);
    }
    
    public static FloplessState initial() {
        return new FloplessState(
          new TableType.SixMax(new StackDepth.Bb100()), 
          new Position.Utg(), 
          new Facing.Open(), 
          SelectedRange.none(), 
          new SelectMode.Idle(), 
          Hovered.none()
        );
    }
}

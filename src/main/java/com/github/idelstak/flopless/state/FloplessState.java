package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.poker.player.Position;
import com.github.idelstak.flopless.poker.player.Facing;
import com.github.idelstak.flopless.poker.table.StackDepth;
import com.github.idelstak.flopless.poker.table.TableType;
import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import java.util.*;

public record FloplessState(
  TableType tableType,
  Position position,
  Facing facing,
  SelectedRange selectedRange,
  SelectMode selectMode,
  Optional<Coordinate> startCoordinate,
  SelectedRange previewRange,
  GridAction selectedAction) implements State {

    FloplessState forTable(TableType tableType) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange, selectedAction);
    }

    FloplessState forPosition(Position position) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange, selectedAction);
    }

    FloplessState face(Facing facing) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange, selectedAction);
    }

    FloplessState selectRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, range, selectMode, startCoordinate, previewRange, selectedAction);
    }

    FloplessState selectMode(SelectMode mode) {
        return new FloplessState(tableType, position, facing, selectedRange, mode, startCoordinate, previewRange, selectedAction);
    }

    FloplessState beginDrag(Optional<Coordinate> maybeCoordinate) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, maybeCoordinate, previewRange, selectedAction);
    }

    FloplessState showPreview(SelectedRange range) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, range, selectedAction);
    }

    FloplessState selectAction(GridAction action) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange, action);
    }

    public static FloplessState initial() {
        return new FloplessState(
          new TableType.SixMax(new StackDepth.Bb100()),
          new Position.Utg(),
          new Facing.Open(),
          SelectedRange.none(),
          new SelectMode.Idle(),
          Optional.empty(),
          SelectedRange.none(),
          new GridAction.Fold()
        );
    }
}

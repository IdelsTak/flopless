package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import java.math.*;
import java.util.*;

public record FloplessState(
  TableType tableType,
  Position position,
  Facing facing,
  boolean squeezeLimpers,
  SelectedRange selectedRange,
  SelectMode selectMode,
  Optional<Coordinate> startCoordinate,
  SelectedRange previewRange,
  GridAction selectedAction,
  BigDecimal raiseAmount,
  BigDecimal minRaiseAmount,
  BigDecimal perLimperAmount,
  BigDecimal minPerLimperAmount) implements State {

    public FloplessState selectRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, range, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    public FloplessState forTable(TableType tableType) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    public FloplessState forPosition(Position position) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    public FloplessState face(Facing facing) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    public FloplessState toggleLimpersSqueeze(boolean squeeze) {
        return new FloplessState(tableType, position, facing, squeeze, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState selectMode(SelectMode mode) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, mode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState beginDrag(Optional<Coordinate> maybeCoordinate) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, maybeCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState showPreview(SelectedRange range) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, range, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState selectAction(GridAction action) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, action, raiseAmount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState raise(BigDecimal amount) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, amount, minRaiseAmount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState minRaiseAmount(BigDecimal amount) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, amount, perLimperAmount, minPerLimperAmount);
    }

    FloplessState perLimper(BigDecimal amount) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, amount, minPerLimperAmount);
    }

    FloplessState minPerLimperAmount(BigDecimal amount) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, raiseAmount, minRaiseAmount, perLimperAmount, amount);
    }

    FloplessState copy(FloplessState state) {
        return new FloplessState(
          state.tableType(),
          state.position(),
          state.facing(),
          state.squeezeLimpers(),
          state.selectedRange(),
          state.selectMode(),
          state.startCoordinate(),
          state.previewRange(),
          state.selectedAction(),
          state.raiseAmount(),
          state.minRaiseAmount(),
          state.perLimperAmount(),
          state.minPerLimperAmount()
        );
    }

    public static FloplessState initial() {
        var minRaise = BigDecimal.valueOf(2.5);
        var minPerLimper = BigDecimal.valueOf(1);
        return new FloplessState(
          new TableType.SixMax(new StackDepth.Bb100(), new Blinds.OneSbTwoBB()),
          new Position.Utg(),
          new Facing.Open(),
          false,
          SelectedRange.none(),
          new SelectMode.Idle(),
          Optional.empty(),
          SelectedRange.none(),
          new GridAction.Fold(),
          minRaise,
          minRaise,
          minPerLimper,
          minPerLimper
        );
    }
}

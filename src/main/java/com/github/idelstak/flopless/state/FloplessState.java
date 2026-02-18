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
  SizingConfig sizingConfig,
  String librarySearchTerm) implements State {

    public FloplessState selectRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, range, selectMode, startCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    public FloplessState forTable(TableType tableType) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    public FloplessState forPosition(Position position) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    public FloplessState face(Facing facing) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    public FloplessState toggleLimpersSqueeze(boolean squeeze) {
        return new FloplessState(tableType, position, facing, squeeze, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    public FloplessState withSizing(SizingConfig sizing) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, sizing, librarySearchTerm);
    }

    public FloplessState withLibrarySearchTerm(String term) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, selectedAction, sizingConfig, term == null ? "" : term);
    }

    public BigDecimal raiseAmount() {
        return sizingConfig.openSizeBb();
    }

    public BigDecimal minRaiseAmount() {
        return sizingConfig.minOpenSizeBb();
    }

    public BigDecimal perLimperAmount() {
        return sizingConfig.perLimperBb();
    }

    public BigDecimal minPerLimperAmount() {
        return sizingConfig.minPerLimperBb();
    }

    public BigDecimal reraisedIpMultiplier() {
        return sizingConfig.reraisedIpMultiplier();
    }

    public BigDecimal reraisedOopMultiplier() {
        return sizingConfig.reraisedOopMultiplier();
    }

    public Map<String, BigDecimal> premiumRaiseOverridesBb() {
        return sizingConfig.premiumRaiseOverridesBb();
    }

    FloplessState selectMode(SelectMode mode) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, mode, startCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    FloplessState beginDrag(Optional<Coordinate> maybeCoordinate) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, maybeCoordinate, previewRange, selectedAction, sizingConfig, librarySearchTerm);
    }

    FloplessState showPreview(SelectedRange range) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, range, selectedAction, sizingConfig, librarySearchTerm);
    }

    FloplessState selectAction(GridAction action) {
        return new FloplessState(tableType, position, facing, squeezeLimpers, selectedRange, selectMode, startCoordinate, previewRange, action, sizingConfig, librarySearchTerm);
    }

    FloplessState raise(BigDecimal amount) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withOpenSize(amount.doubleValue()),
          librarySearchTerm
        );
    }

    FloplessState minRaiseAmount(BigDecimal amount) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withMinOpenSize(amount.doubleValue()),
          librarySearchTerm
        );
    }

    FloplessState perLimper(BigDecimal amount) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withPerLimper(amount.doubleValue()),
          librarySearchTerm
        );
    }

    FloplessState minPerLimperAmount(BigDecimal amount) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withMinPerLimper(amount.doubleValue()),
          librarySearchTerm
        );
    }

    FloplessState reraisedIpMultiplier(BigDecimal multiplier) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withReraisedIpMultiplier(multiplier.doubleValue()),
          librarySearchTerm
        );
    }

    FloplessState reraisedOopMultiplier(BigDecimal multiplier) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withReraisedOopMultiplier(multiplier.doubleValue()),
          librarySearchTerm
        );
    }

    FloplessState premiumOverride(String hand, BigDecimal amountBb) {
        return new FloplessState(
          tableType,
          position,
          facing,
          squeezeLimpers,
          selectedRange,
          selectMode,
          startCoordinate,
          previewRange,
          selectedAction,
          sizingConfig.withPremiumOverride(hand, amountBb.doubleValue()),
          librarySearchTerm
        );
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
          state.sizingConfig(),
          state.librarySearchTerm()
        );
    }

    public static FloplessState initial() {
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
          SizingConfig.defaults(),
          ""
        );
    }
}

package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import com.github.idelstak.flopless.state.spi.*;
import java.math.*;
import java.util.*;

public final class ReducedState implements Reduced<FloplessState, Action, FloplessState> {

    private final Persistence persistence;

    public ReducedState(Persistence persistence) {
        this.persistence = persistence;
    }

    @Override
    public FloplessState apply(FloplessState state, Action action) {
        return switch (action) {
            case Action.User.TableTypePick a ->
                pickTable(state, a);
            case Action.User.PositionPick a ->
                pickPosition(state, a);
            case Action.User.FacingPick a ->
                pickFacing(state, a);
            case Action.User.ToggleLimpersSqueeze _ ->
                toggleLimpersSqueeze(state);
            case Action.User.RangeClear _ ->
                clearRange(state);
            case Action.User.StartDrag a ->
                startDrag(state, a);
            case Action.User.UpdatePreview a ->
                updatePreview(state, a);
            case Action.User.CommitRange _ ->
                commitRange(state);
            case Action.User.SelectGridAction a ->
                selectAction(state, a);
            case Action.User.RaiseAmount a ->
                raiseAmount(state, a);
            case Action.User.IncreaseRaiseAmount a ->
                increaseRaise(state, a);
            case Action.User.DecreaseRaiseAmount a ->
                decreaseRaise(state, a);
            case Action.User.LimperAmount a ->
                limperAmount(state, a);
            case Action.User.IncreaseLimperAmount a ->
                increaseLimper(state, a);
            case Action.User.DecreaseLimperAmount a ->
                decreaseLimper(state, a);
            case Action.User.Save _ ->
                save(state);
            case Action.User.LoadState a ->
                load(a);

            default ->
                state;
        };
    }

    private FloplessState pickTable(FloplessState state, Action.User.TableTypePick action) {
        return state.forTable(action.tableType());
    }

    private FloplessState pickPosition(FloplessState state, Action.User.PositionPick action) {
        return state.forPosition(action.position());
    }

    private FloplessState pickFacing(FloplessState state, Action.User.FacingPick action) {
        return state.face(action.facing());
    }

    private FloplessState toggleLimpersSqueeze(FloplessState state) {
        return state.toggleLimpersSqueeze(!state.squeezeLimpers());
    }

    private FloplessState clearRange(FloplessState state) {
        return state
          .selectMode(new SelectMode.Erasing())
          .selectRange(state.selectedRange().clear());
    }

    private FloplessState startDrag(FloplessState state, Action.User.StartDrag action) {
        var isSelected = state.selectedRange().coordinates().contains(action.coordinate());
        var mode = isSelected ? new SelectMode.Erasing() : new SelectMode.Selecting();
        return state
          .selectMode(mode)
          .beginDrag(Optional.of(action.coordinate()))
          .showPreview(SelectedRange.none());
    }

    private FloplessState updatePreview(FloplessState state, Action.User.UpdatePreview action) {
        var start = state.startCoordinate()
          .orElseThrow(() -> new IllegalStateException("Preview update without drag start"));
        var end = action.coordinate();
        int minX = Math.max(0, Math.min(start.column(), end.column()));
        int maxX = Math.min(12, Math.max(start.column(), end.column()));
        int minY = Math.max(0, Math.min(start.row(), end.row()));
        int maxY = Math.min(12, Math.max(start.row(), end.row()));
        var grid = new Grid();
        var preview = SelectedRange.none()
          .addRange(
            new Coordinate(grid.cell(minX, minY).cards().notation(), minX, minY),
            new Coordinate(grid.cell(maxX, maxY).cards().notation(), maxX, maxY),
            state.selectedAction()
          );
        return state.showPreview(preview);
    }

    private FloplessState commitRange(FloplessState state) {
        if (state.startCoordinate().isEmpty()) {
            return state.selectMode(new SelectMode.Idle());
        }
        var committed = SelectedRange.none();
        for (var c : state.selectedRange().coordinates()) {
            var actionAtCell = state.selectedRange().actionAt(c);
            committed = committed.add(c, actionAtCell);
        }
        for (var c : state.previewRange().coordinates()) {
            var selected = state.selectedRange().coordinates().contains(c);
            var actionToApply = selected ? state.selectedRange().actionAt(c) : state.selectedAction();
            committed = selected ? committed.remove(c) : committed.add(c, actionToApply);
        }
        return state
          .selectRange(committed)
          .showPreview(SelectedRange.none())
          .beginDrag(Optional.empty())
          .selectMode(new SelectMode.Idle());
    }

    private FloplessState selectAction(FloplessState state, Action.User.SelectGridAction select) {
        return state.selectAction(select.action());
    }

    private FloplessState raiseAmount(FloplessState state, Action.User.RaiseAmount raise) {
        var min = state.minRaiseAmount().doubleValue();
        return state.raise(BigDecimal.valueOf(Math.max(min, raise.amount())));
    }

    private FloplessState increaseRaise(FloplessState state, Action.User.IncreaseRaiseAmount increase) {
        var newRaise = updateAmount(state.minRaiseAmount().doubleValue(), state.raiseAmount().doubleValue(), increase.step());
        return state.raise(BigDecimal.valueOf(newRaise));
    }

    private FloplessState decreaseRaise(FloplessState state, Action.User.DecreaseRaiseAmount decrease) {
        var newRaise = updateAmount(state.minRaiseAmount().doubleValue(), state.raiseAmount().doubleValue(), decrease.step());
        return state.raise(BigDecimal.valueOf(newRaise));
    }

    private FloplessState limperAmount(FloplessState state, Action.User.LimperAmount amount) {
        var min = state.minPerLimperAmount().doubleValue();
        return state.raise(BigDecimal.valueOf(Math.max(min, amount.amount())));
    }

    private FloplessState increaseLimper(FloplessState state, Action.User.IncreaseLimperAmount increase) {
        var min = state.minPerLimperAmount().doubleValue();
        var newAmount = updateAmount(min, state.perLimperAmount().doubleValue(), increase.step());
        return state.perLimper(BigDecimal.valueOf(newAmount));
    }

    private FloplessState decreaseLimper(FloplessState state, Action.User.DecreaseLimperAmount decrease) {
        var min = state.minPerLimperAmount().doubleValue();
        var newAmount = updateAmount(min, state.perLimperAmount().doubleValue(), decrease.step());
        return state.perLimper(BigDecimal.valueOf(newAmount));
    }

    private double updateAmount(double min, double current, double step) {
        return Math.max(min, current + step);
    }

    private FloplessState save(FloplessState state) {
        try {
            persistence.save(state);
        } catch (Exception e) {
            System.out.println("[REDUCED STATE] " + e);
        }

        return state;
    }

    private FloplessState load(Action.User.LoadState load) {
        return FloplessState.initial().copy(load.state());
    }
}

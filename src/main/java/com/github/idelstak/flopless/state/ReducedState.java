package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import com.github.idelstak.flopless.state.spi.*;
import java.util.*;

public final class ReducedState implements Reduced<FloplessState, Action, FloplessState> {

    @Override
    public FloplessState apply(FloplessState state, Action action) {
        return switch (action) {
            case Action.User.TableTypePick a ->
                pickTable(state, a);
            case Action.User.PositionPick a ->
                pickPosition(state, a);
            case Action.User.FacingPick a ->
                pickFacing(state, a);
            case Action.User.RangeClear _ ->
                clearRange(state);
            case Action.User.StartDrag a ->
                startDrag(state, a);
            case Action.User.UpdatePreview a ->
                updatePreview(state, a);
            case Action.User.CommitRange _ ->
                commitRange(state);
            case Action.User.Effect _ ->
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
        var preview = SelectedRange.none().addRange(new Coordinate(minX, minY), new Coordinate(maxX, maxY));
        return state.showPreview(preview);
    }

    private FloplessState commitRange(FloplessState state) {
        if (state.startCoordinate().isEmpty()) {
            return state.selectMode(new SelectMode.Idle());
        }
        var committed = SelectedRange.none();
        for (var c : state.selectedRange().coordinates()) {
            committed = committed.add(c);
        }
        for (var c : state.previewRange().coordinates()) {
            boolean selected = state.selectedRange().coordinates().contains(c);
            committed = selected ? committed.remove(c) : committed.add(c);
        }
        return state
          .selectRange(committed)
          .showPreview(SelectedRange.none())
          .beginDrag(Optional.empty())
          .selectMode(new SelectMode.Idle());
    }
}

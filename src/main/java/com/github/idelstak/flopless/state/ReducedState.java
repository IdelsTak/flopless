package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.Coordinate;
import com.github.idelstak.flopless.range.SelectMode;
import com.github.idelstak.flopless.range.SelectedRange;
import com.github.idelstak.flopless.state.api.Action;
import com.github.idelstak.flopless.state.spi.Reduced;

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
            case Action.User.SelectCell a ->
                selectCell(state, a);
            case Action.User.DeselectCell a ->
                deselectCell(state, a);
            case Action.User.SelectRange a ->
                selectRange(state, a);
            case Action.User.DeselectRange a ->
                deselectRange(state, a);
            case Action.User.SelectAll _ ->
                selectAll(state);
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

    private FloplessState selectCell(FloplessState state, Action.User.SelectCell action) {
        state.selectMode();
        return state
          .selectMode(new SelectMode.Selecting())
          .selectRange(state.selectedRange().add(action.coordinate()));
    }

    private FloplessState deselectCell(FloplessState state, Action.User.DeselectCell action) {
        return state
          .selectMode(new SelectMode.Erasing())
          .selectRange(state.selectedRange().remove(action.coordinate()));
    }

    private FloplessState selectRange(FloplessState state, Action.User.SelectRange action) {
        return state
          .selectMode(new SelectMode.Selecting())
          .selectRange(state.selectedRange().addRange(action.start(), action.end()));
    }

    private FloplessState deselectRange(FloplessState state, Action.User.DeselectRange action) {
        return state
          .selectMode(new SelectMode.Erasing())
          .selectRange(state.selectedRange().removeRange(action.start(), action.end()));
    }

    private FloplessState selectAll(FloplessState state) {
        return state
          .selectMode(new SelectMode.Selecting())
          .selectRange(state.selectedRange().all());
    }

    private FloplessState clearRange(FloplessState state) {
        return state
          .selectMode(new SelectMode.Erasing())
          .selectRange(state.selectedRange().clear());
    }

    private FloplessState startDrag(FloplessState state, Action.User.StartDrag action) {
        boolean isSelected = state.selectedRange().coordinates().contains(action.coordinate());
        SelectMode mode = isSelected ? new SelectMode.Erasing() : new SelectMode.Selecting();
        return state.selectMode(mode).withStartCoordinate(action.coordinate());
    }

    private FloplessState updatePreview(FloplessState state, Action.User.UpdatePreview action) {
        Coordinate start = state.startCoordinate();
        Coordinate end = action.coordinate();

        int startX = start.column();
        int startY = start.row();
        int endX = end.column();
        int endY = end.row();

        int minX = Math.max(0, Math.min(startX, endX));
        int maxX = Math.min(12, Math.max(startX, endX));
        int minY = Math.max(0, Math.min(startY, endY));
        int maxY = Math.min(12, Math.max(startY, endY));

        SelectedRange previewRange = SelectedRange.none().addRange(new Coordinate(minX, minY), new Coordinate(maxX, maxY));
        return state.withPreviewRange(previewRange);
    }

    private FloplessState commitRange(FloplessState state) {
        SelectedRange newRange = switch (state.selectMode()) {
            case SelectMode.Selecting _ ->
                state.selectedRange().union(state.previewRange());
            case SelectMode.Erasing _ ->
                state.selectedRange().difference(state.previewRange());
            default ->
                state.selectedRange();
        };

        return state
          .selectRange(newRange)
          .withPreviewRange(SelectedRange.none())
          .selectMode(new SelectMode.Idle());
    }
//    private FloplessState startDrag(FloplessState state, Action.User.StartDrag action) {
//        boolean isSelected = state.selectedRange().coordinates().contains(action.coordinate());
//        SelectMode mode = isSelected ? new SelectMode.Erasing() : new SelectMode.Selecting();
//        return state.selectMode(mode).withStartCoordinate(action.coordinate());
//    }
//
//    private FloplessState updatePreview(FloplessState state, Action.User.UpdatePreview action) {
//        Coordinate start = state.startCoordinate();
//        Coordinate end = action.coordinate();
//
//        int startX = Math.max(0, Math.min(12, start.column()));
//        int startY = Math.max(0, Math.min(12, start.row()));
//        int endX = Math.max(0, Math.min(12, end.column()));
//        int endY = Math.max(0, Math.min(12, end.row()));
//
//        SelectedRange previewRange = SelectedRange.none().addRange(new Coordinate(startX, startY), new Coordinate(endX, endY));
//        return state.withPreviewRange(previewRange);
//    }
//
//    private FloplessState commitRange(FloplessState state) {
//        SelectedRange newRange = switch (state.selectMode()) {
//            case SelectMode.Selecting _ ->
//                state.selectedRange().addRange(
//                        state.startCoordinate(),
//                        state.previewRange().coordinates().stream().reduce((first, last) -> last).orElse(state.startCoordinate())
//                );
//            case SelectMode.Erasing _ ->
//                state.selectedRange().removeRange(
//                        state.startCoordinate(),
//                        state.previewRange().coordinates().stream().reduce((first, last) -> last).orElse(state.startCoordinate())
//                );
//            default ->
//                state.selectedRange();
//        };
//
//        return state
//                .selectRange(newRange)
//                .withPreviewRange(SelectedRange.none())
//                .withStartCoordinate(null)
//                .selectMode(new SelectMode.Idle());
//    }
}

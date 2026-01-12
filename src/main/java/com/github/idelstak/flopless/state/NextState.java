package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.intent.*;
import com.github.idelstak.flopless.range.*;

public final class NextState {

    public FloplessState apply(FloplessState state, Intend intend) {
        return switch (intend) {
            case Intend.TableTypePick i ->
                pickTable(state, i);
            case Intend.PositionPick i ->
                pickPosition(state, i);
            case Intend.FacingPick i ->
                pickFacing(state, i);
            case Intend.SelectCell i ->
                selectCell(state, i);
            case Intend.DeselectCell i ->
                deselectCell(state, i);
            case Intend.SelectRange i ->
                selectRange(state, i);
            case Intend.DeselectRange i ->
                deselectRange(state, i);
            case Intend.SelectAll _ ->
                selectAll(state);
            case Intend.RangeClear _ ->
                clearRange(state);
//            case Intend.FlipSuited i ->
//                flipSuited(state, i);
//            case Intend.FlipOffsuited i ->
//                flipOffsuited(state, i);
//            case Intend.FlipPaired i ->
//                flipPaired(state, i);
            case Intend.Hover i ->
                hover(state, i);
        };
    }

    private FloplessState pickTable(FloplessState state, Intend.TableTypePick intend) {
        return state.forTable(intend.tableType());
    }

    private FloplessState pickPosition(FloplessState state, Intend.PositionPick intend) {
        return state.forPosition(intend.position());
    }

    private FloplessState pickFacing(FloplessState state, Intend.FacingPick intend) {
        return state.face(intend.facing());
    }

    private FloplessState selectCell(FloplessState state, Intend.SelectCell intend) {
        state.selectMode();
        return state
          .selectMode(new SelectMode.Selecting())
          .selectRange(state.selectedRange().add(intend.coordinate()));
    }

    private FloplessState deselectCell(FloplessState state, Intend.DeselectCell intend) {
        return state
          .selectMode(new SelectMode.Erasing())
          .selectRange(state.selectedRange().remove(intend.coordinate()));
    }

    private FloplessState selectRange(FloplessState state, Intend.SelectRange intend) {
        return state
          .selectMode(new SelectMode.Selecting())
          .selectRange(state.selectedRange().addRange(intend.start(), intend.end()));
    }

    private FloplessState deselectRange(FloplessState state, Intend.DeselectRange intend) {
        return state
          .selectMode(new SelectMode.Erasing())
          .selectRange(state.selectedRange().removeRange(intend.start(), intend.end()));
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

//    private FloplessState flipSuited(FloplessState state, Intend.FlipSuited intend) {
//        return state.flipSuited();
//    }
//
//    private FloplessState flipOffsuited(FloplessState state, Intend.FlipOffsuited intend) {
//        return state.flipOffsuited();
//    }
//
//    private FloplessState flipPaired(FloplessState state, Intend.FlipPaired intend) {
//        return state.flipPaired();
//    }
    private FloplessState hover(FloplessState state, Intend.Hover intend) {
        return state.hover(new Hovered(intend.coordinate()));
    }
}

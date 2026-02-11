package com.github.idelstak.flopless.state.api;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.*;

public sealed interface Action {

    sealed interface Effect extends Action {

        record GridActionSelected(GridAction action) implements Effect {

        }

        record DeleteStateRequested(FloplessState state) implements Effect {

        }
    }

    sealed interface User extends Action {

        record Undo() implements User {
        }

        record Redo() implements User {
        }

        record Save() implements User {
        }

        record DeleteState(FloplessState state) implements User {
        }

        record TableTypePick(TableType tableType) implements User {

        }

        record PositionPick(Position position) implements User {

        }

        record FacingPick(Facing facing) implements User {

        }

        record ToggleLimpersSqueeze() implements User {
        }

        record RangeClear() implements User {
        }

        record StartDrag(Coordinate coordinate) implements User {

        }

        record UpdatePreview(Coordinate coordinate) implements User {

        }

        record CommitRange() implements User {
        }

        record SelectGridAction(GridAction action) implements User {

        }

        record RaiseAmount(double amount) implements Action {

        }

        record IncreaseRaiseAmount(double step) implements Action {

        }

        record DecreaseRaiseAmount(double step) implements Action {

        }

        record LimperAmount(double amount) implements Action {

        }

        record IncreaseLimperAmount(double step) implements Action {

        }

        record DecreaseLimperAmount(double step) implements Action {

        }

        record LoadState(FloplessState state) implements Action {

        }
    }
}

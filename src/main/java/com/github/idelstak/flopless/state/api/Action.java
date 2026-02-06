package com.github.idelstak.flopless.state.api;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;

public sealed interface Action {

    sealed interface Effect extends Action {

        record GridActionSelected(GridAction action) implements Effect {

        }
    }

    sealed interface User extends Action {

        record TableTypePick(TableType tableType) implements User {

        }

        record PositionPick(Position position) implements User {

        }

        record FacingPick(Facing facing) implements User {

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
    }
}

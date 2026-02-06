package com.github.idelstak.flopless.state.api;

import com.github.idelstak.flopless.poker.player.Position;
import com.github.idelstak.flopless.poker.player.Facing;
import com.github.idelstak.flopless.poker.table.TableType;
import com.github.idelstak.flopless.grid.*;

public sealed interface Action {

    non-sealed interface Effect extends Action {
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
    }
}

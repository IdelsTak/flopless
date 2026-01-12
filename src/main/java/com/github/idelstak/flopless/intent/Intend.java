package com.github.idelstak.flopless.intent;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.table.*;

public sealed interface Intend {

    record TableTypePick(TableType tableType) implements Intend {

    }

    record PositionPick(Position position) implements Intend {

    }

    record FacingPick(Facing facing) implements Intend {

    }

    record SelectCell(Coordinate coordinate) implements Intend {

    }

    record DeselectCell(Coordinate coordinate) implements Intend {

    }

    record SelectRange(Coordinate start, Coordinate end) implements Intend {

    }

    record DeselectRange(Coordinate start, Coordinate end) implements Intend {

    }

    record SelectAll() implements Intend {
    }

    record RangeClear() implements Intend {
    }

//    record FlipSuited() implements Intend {
//    }
//
//    record FlipOffsuited() implements Intend {
//    }
//
//    record FlipPaired() implements Intend {
//    }
    record Hover(Coordinate coordinate) implements Intend {

    }
}

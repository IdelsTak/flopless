package com.github.idelstak.flopless.intent;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.range.*;
import com.github.idelstak.flopless.table.*;

public sealed interface Intend {

    record CellPick(Cell cell) implements Intend {

    }

    record RangeClear() implements Intend {
    }

    record PositionPick(Position position) implements Intend {

    }

    record FacingPick(Facing facing) implements Intend {

    }

    record SelectionModePick(Selection selection) implements Intend {

    }

    record TableTypePick(TableType tableType) implements Intend {

    }

    record HoverCell(Cell cell) implements Intend {

    }
}

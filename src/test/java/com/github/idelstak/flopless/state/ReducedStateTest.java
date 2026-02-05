package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.range.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.table.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class ReducedStateTest {

    @Test
    void appliesTableTypePickAndChangesTableType() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var table = new TableType.NineMax(new StackDepth.Bb200());
        var after = reduced.apply(before, new Action.User.TableTypePick(table));
        assertThat("state did not change table type as expected", after.tableType(), is(table));
    }

    @Test
    void appliesPositionPickAndChangesPosition() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var position = new Position.Btn();
        var after = reduced.apply(before, new Action.User.PositionPick(position));
        assertThat("state did not change position as expected", after.position(), is(position));
    }

    @Test
    void appliesFacingPickAndChangesFacing() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var facing = new Facing.Vs3Bet();
        var after = reduced.apply(before, new Action.User.FacingPick(facing));
        assertThat("state did not change facing as expected", after.facing(), is(facing));
    }

    @Test
    void appliesSelectCellAndAddsCoordinateToRange() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var cell = new Coordinate(3, 7);
        var after = reduced.apply(before, new Action.User.SelectCell(cell));
        assertThat("state did not contain selected coordinate", after.selectedRange().coordinates(), hasItem(cell));
    }

    @Test
    void appliesDeselectCellAndRemovesCoordinateFromRange() {
        var reduced = new ReducedState();
        var cell = new Coordinate(2, 11);
        var before = FloplessState.initial().selectRange(SelectedRange.none().add(cell));
        var after = reduced.apply(before, new Action.User.DeselectCell(cell));
        assertThat("state still contained deselected coordinate", after.selectedRange().coordinates(), not(hasItem(cell)));
    }

    @Test
    void appliesSelectRangeAndAddsMultipleCoordinates() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var start = new Coordinate(1, 1);
        var end = new Coordinate(2, 2);
        var after = reduced.apply(before, new Action.User.SelectRange(start, end));
        assertThat("state did not include expected range size", after.selectedRange().coordinates().size(), is(4));
    }

    @Test
    void appliesDeselectRangeAndRemovesMultipleCoordinates() {
        var reduced = new ReducedState();
        var start = new Coordinate(4, 4);
        var end = new Coordinate(5, 5);
        var before = FloplessState.initial().selectRange(SelectedRange.none().addRange(start, end));
        var after = reduced.apply(before, new Action.User.DeselectRange(start, end));
        assertThat("state still contained deselected range", after.selectedRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void appliesSelectAllAndFillsEntireGrid() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var after = reduced.apply(before, new Action.User.SelectAll());
        assertThat("state did not select all grid cells", after.selectedRange().coordinates().size(), is(169));
    }

    @Test
    void appliesRangeClearAndRemovesAllSelectedCells() {
        var reduced = new ReducedState();
        var cell = new Coordinate(9, 9);
        var before = FloplessState.initial().selectRange(SelectedRange.none().add(cell));
        var after = reduced.apply(before, new Action.User.RangeClear());
        assertThat("state range was not cleared", after.selectedRange().coordinates().isEmpty(), is(true));
    }
}

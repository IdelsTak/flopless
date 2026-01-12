package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.intent.*;
import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.range.*;
import com.github.idelstak.flopless.table.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class NextStateTest {

    @Test
    void appliesTableTypePickAndChangesTableType() {
        var next = new NextState();
        var before = FloplessState.initial();
        var table = new TableType.NineMax(new StackDepth.Bb200());
        var after = next.apply(before, new Intend.TableTypePick(table));
        assertThat("state did not change table type as expected", after.tableType(), is(table));
    }

    @Test
    void appliesPositionPickAndChangesPosition() {
        var next = new NextState();
        var before = FloplessState.initial();
        var position = new Position.Btn();
        var after = next.apply(before, new Intend.PositionPick(position));
        assertThat("state did not change position as expected", after.position(), is(position));
    }

    @Test
    void appliesFacingPickAndChangesFacing() {
        var next = new NextState();
        var before = FloplessState.initial();
        var facing = new Facing.Vs3Bet();
        var after = next.apply(before, new Intend.FacingPick(facing));
        assertThat("state did not change facing as expected", after.facing(), is(facing));
    }

    @Test
    void appliesSelectCellAndAddsCoordinateToRange() {
        var next = new NextState();
        var before = FloplessState.initial();
        var cell = new Coordinate(3, 7);
        var after = next.apply(before, new Intend.SelectCell(cell));
        assertThat("state did not contain selected coordinate", after.selectedRange().coordinates(), hasItem(cell));
    }

    @Test
    void appliesDeselectCellAndRemovesCoordinateFromRange() {
        var next = new NextState();
        var cell = new Coordinate(2, 11);
        var before = FloplessState.initial().selectRange(SelectedRange.none().add(cell));
        var after = next.apply(before, new Intend.DeselectCell(cell));
        assertThat("state still contained deselected coordinate", after.selectedRange().coordinates(), not(hasItem(cell)));
    }

    @Test
    void appliesSelectRangeAndAddsMultipleCoordinates() {
        var next = new NextState();
        var before = FloplessState.initial();
        var start = new Coordinate(1, 1);
        var end = new Coordinate(2, 2);
        var after = next.apply(before, new Intend.SelectRange(start, end));
        assertThat("state did not include expected range size", after.selectedRange().coordinates().size(), is(4));
    }

    @Test
    void appliesDeselectRangeAndRemovesMultipleCoordinates() {
        var next = new NextState();
        var start = new Coordinate(4, 4);
        var end = new Coordinate(5, 5);
        var before = FloplessState.initial().selectRange(SelectedRange.none().addRange(start, end));
        var after = next.apply(before, new Intend.DeselectRange(start, end));
        assertThat("state still contained deselected range", after.selectedRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void appliesSelectAllAndFillsEntireGrid() {
        var next = new NextState();
        var before = FloplessState.initial();
        var after = next.apply(before, new Intend.SelectAll());
        assertThat("state did not select all grid cells", after.selectedRange().coordinates().size(), is(169));
    }

    @Test
    void appliesRangeClearAndRemovesAllSelectedCells() {
        var next = new NextState();
        var cell = new Coordinate(9, 9);
        var before = FloplessState.initial().selectRange(SelectedRange.none().add(cell));
        var after = next.apply(before, new Intend.RangeClear());
        assertThat("state range was not cleared", after.selectedRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void appliesHoverAndSetsHoveredCoordinate() {
        var next = new NextState();
        var before = FloplessState.initial();
        var cell = new Coordinate(6, 8);
        var after = next.apply(before, new Intend.Hover(cell));
        assertThat("state did not update hovered coordinate", after.hovered(), is(new Hovered(cell)));
    }
}

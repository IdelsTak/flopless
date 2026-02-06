package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class ReducedStateTest {

    @Test
    void tableTypePickChangesTableType() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var table = new TableType.NineMax(new StackDepth.Bb200());
        var after = reduced.apply(before, new Action.User.TableTypePick(table));
        assertThat(after.tableType(), is(table));
    }

    @Test
    void positionPickChangesPosition() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var position = new Position.Btn();
        var after = reduced.apply(before, new Action.User.PositionPick(position));
        assertThat(after.position(), is(position));
    }

    @Test
    void facingPickChangesFacing() {
        var reduced = new ReducedState();
        var before = FloplessState.initial();
        var facing = new Facing.Vs3Bet();
        var after = reduced.apply(before, new Action.User.FacingPick(facing));
        assertThat(after.facing(), is(facing));
    }

    @Test
    void rangeClearClearsSelectedRange() {
        var reduced = new ReducedState();
        var cell = new Coordinate(9, 9);
        var before = FloplessState.initial().selectRange(SelectedRange.none().add(cell));
        var after = reduced.apply(before, new Action.User.RangeClear());
        assertThat(after.selectedRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void startDragSetsStartCoordinate() {
        var reduced = new ReducedState();
        var cell = new Coordinate(5, 5);
        var before = FloplessState.initial();
        var after = reduced.apply(before, new Action.User.StartDrag(cell));
        assertThat(after.startCoordinate(), is(Optional.of(cell)));
    }

    @Test
    void startDragSetsModeErasingForSelectedCell() {
        var reduced = new ReducedState();
        var cell = new Coordinate(5, 5);
        var before = FloplessState.initial().selectRange(SelectedRange.none().add(cell));
        var after = reduced.apply(before, new Action.User.StartDrag(cell));
        assertThat(after.selectMode() instanceof SelectMode.Erasing, is(true));
    }

    @Test
    void startDragSetsModeSelectingForUnselectedCell() {
        var reduced = new ReducedState();
        var cell = new Coordinate(5, 5);
        var before = FloplessState.initial();
        var after = reduced.apply(before, new Action.User.StartDrag(cell));
        assertThat(after.selectMode() instanceof SelectMode.Selecting, is(true));
    }

    @Test
    void updatePreviewSetsCorrectPreviewCoordinates() {
        var reduced = new ReducedState();
        var start = new Coordinate(2, 2);
        var end = new Coordinate(4, 4);
        var before = FloplessState.initial().beginDrag(Optional.of(start));
        var after = reduced.apply(before, new Action.User.UpdatePreview(end));
        var previewCoords = after.previewRange().coordinates();
        for (int col = 2; col <= 4; col++) {
            for (int row = 2; row <= 4; row++) {
                assertThat(previewCoords, hasItem(new Coordinate(col, row)));
            }
        }
    }

    @Test
    void commitRangeAddsPreviewedNewCoordinates() {
        var reduced = new ReducedState();
        var previewAdd = SelectedRange.none().add(new Coordinate(2, 2));
        var before = FloplessState.initial()
          .beginDrag(Optional.of(new Coordinate(0, 0)))
          .showPreview(previewAdd);
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.selectedRange().coordinates(), hasItem(new Coordinate(2, 2)));
    }

    @Test
    void commitRangeRemovesPreviewedExistingCoordinates() {
        var reduced = new ReducedState();
        var selected = SelectedRange.none().add(new Coordinate(1, 1));
        var previewRemove = SelectedRange.none().add(new Coordinate(1, 1));
        var before = FloplessState.initial()
          .selectRange(selected)
          .beginDrag(Optional.of(new Coordinate(0, 0)))
          .showPreview(previewRemove);
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.selectedRange().coordinates(), not(hasItem(new Coordinate(1, 1))));
    }

    @Test
    void commitRangeClearsPreviewRange() {
        var reduced = new ReducedState();
        var preview = SelectedRange.none().add(new Coordinate(2, 2));
        var before = FloplessState.initial().beginDrag(Optional.of(new Coordinate(0, 0))).showPreview(preview);
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.previewRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void commitRangeClearsStartCoordinate() {
        var reduced = new ReducedState();
        var before = FloplessState.initial().beginDrag(Optional.of(new Coordinate(0, 0)));
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.startCoordinate().isEmpty(), is(true));
    }

    @Test
    void commitRangeSetsModeIdle() {
        var reduced = new ReducedState();
        var before = FloplessState.initial().beginDrag(Optional.of(new Coordinate(0, 0)));
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.selectMode() instanceof SelectMode.Idle, is(true));
    }
}

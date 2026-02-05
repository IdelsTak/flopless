package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.player.*;
import com.github.idelstak.flopless.range.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.table.*;

public record FloplessState(
        TableType tableType,
        Position position,
        Facing facing,
        SelectedRange selectedRange,
        SelectMode selectMode,
        Coordinate startCoordinate,
        SelectedRange previewRange) implements State {

    public FloplessState forTable(TableType tableType) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange);
    }

    public FloplessState forPosition(Position position) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange);
    }

    public FloplessState face(Facing facing) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, previewRange);
    }

    public FloplessState selectRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, range, selectMode, startCoordinate, previewRange);
    }

    public FloplessState selectMode(SelectMode mode) {
        return new FloplessState(tableType, position, facing, selectedRange, mode, startCoordinate, previewRange);
    }

    public FloplessState withStartCoordinate(Coordinate coordinate) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, coordinate, previewRange);
    }

    public FloplessState withPreviewRange(SelectedRange range) {
        return new FloplessState(tableType, position, facing, selectedRange, selectMode, startCoordinate, range);
    }

    public static FloplessState initial() {
        return new FloplessState(
                new TableType.SixMax(new StackDepth.Bb100()),
                new Position.Utg(),
                new Facing.Open(),
                SelectedRange.none(),
                new SelectMode.Idle(),
                new Coordinate(0, 0),
                SelectedRange.none()
        );
    }
}

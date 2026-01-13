package com.github.idelstak.flopless.grid;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class GridTest {

    @Test
    void gridContains13Columns() {
        var grid = new Grid();
        assertThat("grid does not have 13 rows", grid.cells().size(), is(13));
    }

    @Test
    void eachRowContains13Cells() {
        var grid = new Grid();
        for (var row : grid.cells()) {
            assertThat("row does not have 13 cells", row.size(), is(13));
        }
    }

    @Test
    void topLeftCellIsAcePair() {
        var grid = new Grid();
        var cell = grid.cell(0, 0);
        assertThat("top-left cell is not AA", cell.toString(), is("AA"));
    }

    @Test
    void bottomRightCellIsTwoPair() {
        var grid = new Grid();
        var cell = grid.cell(12, 12);
        assertThat("bottom-right cell is not 22", cell.toString(), is("22"));
    }

    @Test
    void diagonalCellsArePairs() {
        var grid = new Grid();
        for (int i = 0; i < 13; i++) {
            var cell = grid.cell(i, i);
            assertThat("cell on diagonal is not a pair", cell.cards().first().rank(),
              is(cell.cards().second().rank()));
        }
    }

    @Test
    void upperTriangleCellsAreSuited() {
        var grid = new Grid();
        for (int row = 0; row < 13; row++) {
            for (int col = row + 1; col < 13; col++) {
                var cell = grid.cell(row, col);
                assertThat("cell above diagonal is not suited", cell.cards().first().suit(),
                  is(cell.cards().second().suit()));
            }
        }
    }

    @Test
    void lowerTriangleCellsAreOffsuit() {
        var grid = new Grid();
        for (int row = 1; row < 13; row++) {
            for (int col = 0; col < row; col++) {
                var cell = grid.cell(row, col);
                assertThat("cell below diagonal is not offsuit",
                  cell.cards().first().suit().equals(cell.cards().second().suit()), is(false));
            }
        }
    }
}

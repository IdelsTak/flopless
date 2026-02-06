package com.github.idelstak.flopless.grid;

import com.github.idelstak.flopless.poker.hand.*;
import java.util.*;

public final class Grid {

    private final List<List<Cell>> cells;
    private final List<Rank> ranks;

    public Grid() {
        ranks = List.of(
          new Rank.Ace(), new Rank.King(), new Rank.Queen(), new Rank.Jack(),
          new Rank.Ten(), new Rank.Nine(), new Rank.Eight(), new Rank.Seven(),
          new Rank.Six(), new Rank.Five(), new Rank.Four(), new Rank.Three(),
          new Rank.Two()
        );
        List<Suit> suits = List.of(
          new Suit.Clubs(),
          new Suit.Diamonds(),
          new Suit.Hearts(),
          new Suit.Spades()
        );
        var grid = new ArrayList<List<Cell>>();
        for (var row : ranks) {
            var rowCells = new ArrayList<Cell>();
            for (var col : ranks) {
                rowCells.add(new Cell(build(row, col, suits), new GridAction.Fold()));
            }
            grid.add(List.copyOf(rowCells));
        }
        cells = List.copyOf(grid);
    }

    public String render() {
        var builder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < ranks.size(); rowIndex++) {
            if (ranks.get(rowIndex).text().length() == 1) {
                builder.append(" ");
            }
            for (int colIndex = 0; colIndex < ranks.size(); colIndex++) {
                var cell = cells.get(rowIndex).get(colIndex);
                String notation = cell.cards().notation();
                builder.append(String.format("%-4s", notation));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public List<List<Cell>> cells() {
        return List.copyOf(cells);
    }

    Cell cell(int column, int row) {
        return cells.get(column).get(row);
    }

    private HoleCards build(Rank row, Rank col, List<Suit> suits) {
        if (row.equals(col)) {
            return buildPair(row, suits);
        }
        if (row.order() > col.order()) {
            return buildSuited(row, col, suits.get(0));
        }
        return buildOffSuit(col, row, suits);
    }

    private HoleCards buildPair(Rank rank, List<Suit> suits) {
        return new HoleCards(new Card(rank, suits.get(0)), new Card(rank, suits.get(1)));
    }

    private HoleCards buildSuited(Rank high, Rank low, Suit suit) {
        return new HoleCards(new Card(high, suit), new Card(low, suit));
    }

    private HoleCards buildOffSuit(Rank high, Rank low, List<Suit> suits) {
        return new HoleCards(new Card(high, suits.get(0)), new Card(low, suits.get(1)));
    }
}

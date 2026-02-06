package com.github.idelstak.flopless.poker.hand;

import java.util.*;

public record HoleCards(Card first, Card second) implements Comparable<HoleCards> {

    public boolean suited() {
        return first.suit().equals(second.suit());
    }

    public Rank highRank() {
        return first.rank().order() >= second.rank().order() ? first.rank() : second.rank();
    }

    public Rank lowRank() {
        return first.rank().order() >= second.rank().order() ? second.rank() : first.rank();
    }

    public String notation() {
        if (first.rank().equals(second.rank())) {
            return first.rank().text() + second.rank().text(); // e.g. "AA"
        }
        return highRank().text() + lowRank().text() + (suited() ? "s" : "o"); // e.g. "AKs"
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.notation());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        var other = (HoleCards) obj;
        return Objects.equals(this.notation(), other.notation());
    }

    @Override
    public int compareTo(HoleCards other) {
        int cmp = Integer.compare(other.highRank().order(), highRank().order());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(other.lowRank().order(), lowRank().order());
        if (cmp != 0) {
            return cmp;
        }
        return Boolean.compare(other.suited(), suited());
    }

    @Override
    public String toString() {
        return notation();
    }
}

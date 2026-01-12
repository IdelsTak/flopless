package com.github.idelstak.flopless.hand;

public record Card(Rank rank, Suit suit) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append(rank.text());
        sb.append(suit.text());
        sb.append('}');
        return sb.toString();
    }

}

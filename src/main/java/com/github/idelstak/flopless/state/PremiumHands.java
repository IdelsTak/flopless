package com.github.idelstak.flopless.state;

import java.util.*;

final class PremiumHands {

    private final Set<String> hands;

    PremiumHands() {
        hands = Set.of("AA", "KK", "QQ", "JJ", "AKs", "AKo", "AQs");
    }

    boolean contains(String hand) {
        return hands.contains(hand);
    }
}

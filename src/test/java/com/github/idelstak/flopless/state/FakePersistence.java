package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.io.*;
import java.util.*;

final class FakePersistence implements Persistence {

    private final List<FloplessState> states;
    private final Strategy strategy;

    FakePersistence() {
        this(List.of());
    }

    FakePersistence(List<FloplessState> initialStates) {
        this.states = new ArrayList<>(initialStates);
        this.strategy = new Strategy();
    }

    @Override
    public void save(FloplessState state) {
        var name = strategy.name(state);
        var existing = -1;
        for (int i = 0; i < states.size(); i++) {
            if (strategy.name(states.get(i)).equals(name)) {
                existing = i;
                break;
            }
        }
        if (existing >= 0) {
            states.set(existing, state);
            return;
        }
        states.add(state);
    }

    @Override
    public void delete(FloplessState state) {
        var name = strategy.name(state);
        states.removeIf(saved -> strategy.name(saved).equals(name));
    }

    @Override
    public List<FloplessState> loadAll() {
        return List.copyOf(states);
    }
}

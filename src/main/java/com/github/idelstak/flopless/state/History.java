package com.github.idelstak.flopless.state;

import java.util.*;

public final class History<S> {

    private final List<S> past;
    private final S present;
    private final List<S> future;

    public History(List<S> past, S present, List<S> future) {
        this.past = past;
        this.present = present;
        this.future = future;
    }

    public History<S> advance(S next) {
        var nextPast = new ArrayList<S>(past);
        nextPast.add(present);
        return new History<>(List.copyOf(nextPast), next, List.of());
    }

    public History<S> undo() {
        if (past.isEmpty()) {
            throw new IllegalStateException("Undo requested with empty history");
        }
        var nextFuture = new ArrayList<S>(future);
        nextFuture.add(0, present);
        var last = past.get(past.size() - 1);
        return new History<>(past.subList(0, past.size() - 1), last, List.copyOf(nextFuture));
    }

    public History<S> redo() {
        if (future.isEmpty()) {
            throw new IllegalStateException("Redo requested with empty future");
        }
        var nextPast = new ArrayList<S>(past);
        nextPast.add(present);
        return new History<>(List.copyOf(nextPast), future.get(0), future.subList(1, future.size()));
    }

    public S present() {
        return present;
    }

    public List<S> past() {
        return List.copyOf(past);
    }

    public List<S> future() {
        return List.copyOf(future);
    }

    public boolean canUndo() {
        return !past.isEmpty();
    }

    public boolean canRedo() {
        return !future.isEmpty();
    }

    public static <S> History<S> initial(S state) {
        return new History<>(List.of(), state, List.of());
    }
}

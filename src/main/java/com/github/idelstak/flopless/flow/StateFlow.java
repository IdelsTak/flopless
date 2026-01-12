package com.github.idelstak.flopless.flow;

import com.github.idelstak.flopless.intent.*;
import com.github.idelstak.flopless.state.*;

public final class StateFlow implements IntentSink {

    private final NextState nextState;
    private FloplessState state;

    public StateFlow() {
        nextState = new NextState();
        state = null;
    }

    @Override
    public void accept(Intend intend) {
        state = nextState.apply(state, intend);
    }

    @Override
    public FloplessState latestState() {
        return state;
    }
}

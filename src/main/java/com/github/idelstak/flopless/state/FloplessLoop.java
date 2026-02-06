package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.spi.*;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.subjects.*;

public final class FloplessLoop implements Source<FloplessState>, Sink<Action> {

    private final Subject<Action> actions;
    private final Observable<FloplessState> states;

    public FloplessLoop(ReducedState reduced) {
        actions = PublishSubject.<Action>create().toSerialized();
        states = actions
          .scan(FloplessState.initial(), reduced::apply)
          .replay(1)
          .autoConnect();
    }

    @Override
    public void subscribe(Observer<? super FloplessState> observer) {
        states.subscribe(observer);
    }

    @Override
    public void accept(Action action) {
        switch (action) {
            case Action.User userAction ->
                actions.onNext(userAction);
            case Action.Effect sideEffect -> {
                switch (sideEffect) {
                    case Action.Effect.GridActionSelected selected -> {
                        actions.onNext(new Action.User.SelectGridAction(selected.action()));
                    }
                }
            }
        }
    }
}

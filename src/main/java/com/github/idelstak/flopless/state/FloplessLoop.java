package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import com.github.idelstak.flopless.state.spi.*;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.subjects.*;

public final class FloplessLoop implements Source<History<FloplessState>>, Sink<Action> {

    private final Subject<Action> actions;
    private final Observable<History<FloplessState>> states;
    private final Reduced<FloplessState, Action, FloplessState> reduced;

    public FloplessLoop(FloplessState initial, Reduced<FloplessState, Action, FloplessState> reduced) {
        this.reduced = reduced;
        this.actions = PublishSubject.<Action>create().toSerialized();
        this.states = actions
          .scan(History.initial(initial), this::reduce)
          .replay(1)
          .autoConnect();
    }

    @Override
    public void subscribe(Observer<? super History<FloplessState>> observer) {
        states.subscribe(observer);
    }

    @Override
    public void accept(Action action) {
        switch (action) {
            case Action.Effect effect ->
                handle(effect);
            default ->
                actions.onNext(action);
        }
    }

    private void handle(Action.Effect effect) {
        switch (effect) {
            case Action.Effect.GridActionSelected selected ->
                actions.onNext(new Action.User.SelectGridAction(selected.action()));
        }
    }

    private History<FloplessState> reduce(History<FloplessState> history, Action action) {
        return switch (action) {
            case Action.User.Undo _ ->
                clearPreview(history.undo());
            case Action.User.Redo _ ->
                clearPreview(history.redo());
            default ->
                apply(history, action);
        };
    }

    private History<FloplessState> clearPreview(History<FloplessState> history) {
        var past = history.past();
        var present = history.present();
        var future = history.future();
        return new History<>(past, present.showPreview(SelectedRange.none()), future);
    }

    private History<FloplessState> apply(History<FloplessState> history, Action action) {
        var current = history.present();
        var next = reduced.apply(current, action);
        return transientAction(action)
                 ? new History<>(history.past(), next, history.future())
                 : history.advance(next);
    }

    private boolean transientAction(Action action) {
        return action instanceof Action.User.StartDrag
          || action instanceof Action.User.UpdatePreview
          || action instanceof Action.Effect;
    }
}

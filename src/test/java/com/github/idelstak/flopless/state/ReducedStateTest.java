package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.api.*;
import com.github.idelstak.flopless.state.range.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class ReducedStateTest {

    @Test
    void tableTypePickChangesTableType() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial();
        var table = new TableType.NineMax(new StackDepth.Bb200(), new Blinds.OneSbTwoBB());
        var after = reduced.apply(before, new Action.User.TableTypePick(table));
        assertThat(after.tableType(), is(table));
    }

    @Test
    void positionPickChangesPosition() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial();
        var position = new Position.Btn();
        var after = reduced.apply(before, new Action.User.PositionPick(position));
        assertThat(after.position(), is(position));
    }

    @Test
    void facingPickChangesFacing() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial();
        var facing = new Facing.ReRaised.Vs3Bet();
        var after = reduced.apply(before, new Action.User.FacingPick(facing));
        assertThat(after.facing(), is(facing));
    }

    @Test
    void rangeClearClearsSelectedRange() {
        var reduced = new ReducedState(new FakePersistence());
        var hand = new Grid().cell(9, 9).cards().notation();
        var cell = new Coordinate(hand, 9, 9);
        var before = FloplessState.initial()
          .selectRange(SelectedRange.none().add(cell, new GridAction.Fold()));
        var after = reduced.apply(before, new Action.User.RangeClear());
        assertThat(after.selectedRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void startDragSetsStartCoordinate() {
        var reduced = new ReducedState(new FakePersistence());
        var hand = new Grid().cell(5, 5).cards().notation();
        var cell = new Coordinate(hand, 5, 5);
        var before = FloplessState.initial();
        var after = reduced.apply(before, new Action.User.StartDrag(cell));
        assertThat(after.startCoordinate(), is(Optional.of(cell)));
    }

    @Test
    void startDragSetsModeErasingForSelectedCell() {
        var reduced = new ReducedState(new FakePersistence());
        var hand = new Grid().cell(5, 5).cards().notation();
        var cell = new Coordinate(hand, 5, 5);
        var before = FloplessState.initial()
          .selectRange(SelectedRange.none().add(cell, new GridAction.Fold()));
        var after = reduced.apply(before, new Action.User.StartDrag(cell));
        assertThat(after.selectMode() instanceof SelectMode.Erasing, is(true));
    }

    @Test
    void startDragSetsModeSelectingForUnselectedCell() {
        var reduced = new ReducedState(new FakePersistence());
        var hand = new Grid().cell(5, 5).cards().notation();
        var cell = new Coordinate(hand, 5, 5);
        var before = FloplessState.initial();
        var after = reduced.apply(before, new Action.User.StartDrag(cell));
        assertThat(after.selectMode() instanceof SelectMode.Selecting, is(true));
    }

    @Test
    void updatePreviewSetsPreviewCoordinate() {
        var reduced = new ReducedState(new FakePersistence());
        var startHand = new Grid().cell(2, 2).cards().notation();
        var start = new Coordinate(startHand, 2, 2);
        var endHand = new Grid().cell(4, 4).cards().notation();
        var end = new Coordinate(endHand, 4, 4);
        var action = new GridAction.Fold();
        var before = FloplessState.initial()
          .beginDrag(Optional.of(start))
          .selectAction(action);
        var after = reduced.apply(before, new Action.User.UpdatePreview(end));
        var previewHand = new Grid().cell(2, 2).cards().notation();
        assertThat(after.previewRange().coordinates().contains(new Coordinate(previewHand, 2, 2)), is(true));
    }

    @Test
    void commitRangeAddsPreviewedCoordinate() {
        var reduced = new ReducedState(new FakePersistence());
        var startHand = new Grid().cell(0, 0).cards().notation();
        var start = new Coordinate(startHand, 0, 0);
        var previewHand = new Grid().cell(2, 2).cards().notation();
        var previewAdd = SelectedRange.none().add(new Coordinate(previewHand, 2, 2), new GridAction.Fold());
        var before = FloplessState.initial()
          .beginDrag(Optional.of(start))
          .selectAction(new GridAction.Fold())
          .showPreview(previewAdd);
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.selectedRange().coordinates().contains(new Coordinate(previewHand, 2, 2)), is(true));
    }

    @Test
    void commitRangeRemovesPreviewedCoordinate() {
        var reduced = new ReducedState(new FakePersistence());
        var selectedHand = new Grid().cell(1, 1).cards().notation();
        var selected = SelectedRange.none().add(new Coordinate(selectedHand, 1, 1), new GridAction.Fold());
        var previewRemove = SelectedRange.none().add(new Coordinate(selectedHand, 1, 1), new GridAction.Fold());
        var startHand = new Grid().cell(0, 0).cards().notation();
        var before = FloplessState.initial()
          .selectRange(selected)
          .beginDrag(Optional.of(new Coordinate(startHand, 0, 0)))
          .showPreview(previewRemove);
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.selectedRange().coordinates().contains(new Coordinate(selectedHand, 1, 1)), is(false));
    }

    @Test
    void commitRangeClearsPreviewRange() {
        var reduced = new ReducedState(new FakePersistence());
        var previewHand = new Grid().cell(2, 2).cards().notation();
        var preview = SelectedRange.none().add(new Coordinate(previewHand, 2, 2), new GridAction.Fold());
        var startHand = new Grid().cell(0, 0).cards().notation();
        var before = FloplessState.initial()
          .beginDrag(Optional.of(new Coordinate(startHand, 0, 0)))
          .showPreview(preview);
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.previewRange().coordinates().isEmpty(), is(true));
    }

    @Test
    void commitRangeClearsStartCoordinate() {
        var reduced = new ReducedState(new FakePersistence());
        var startHand = new Grid().cell(0, 0).cards().notation();
        var before = FloplessState.initial().beginDrag(Optional.of(new Coordinate(startHand, 0, 0)));
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.startCoordinate().isEmpty(), is(true));
    }

    @Test
    void commitRangeSetsModeIdle() {
        var reduced = new ReducedState(new FakePersistence());
        var startHand = new Grid().cell(0, 0).cards().notation();
        var before = FloplessState.initial().beginDrag(Optional.of(new Coordinate(startHand, 0, 0)));
        var after = reduced.apply(before, new Action.User.CommitRange());
        assertThat(after.selectMode() instanceof SelectMode.Idle, is(true));
    }

    @Test
    void raiseAmountSetsRaiseToMaxOfMinAndValue() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial().minRaiseAmount(BigDecimal.valueOf(5));
        var after = reduced.apply(before, new Action.User.RaiseAmount(3));
        assertThat(after.raiseAmount().doubleValue(), is(5.0));
    }

    @Test
    void increaseRaiseIncrementsRaiseByStep() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial().minRaiseAmount(BigDecimal.valueOf(1)).raise(BigDecimal.valueOf(2));
        var after = reduced.apply(before, new Action.User.IncreaseRaiseAmount(0.5));
        assertThat(after.raiseAmount().doubleValue(), is(2.5));
    }

    @Test
    void decreaseRaiseDecrementsRaiseButNotBelowMin() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial().minRaiseAmount(BigDecimal.valueOf(2)).raise(BigDecimal.valueOf(3));
        var after = reduced.apply(before, new Action.User.DecreaseRaiseAmount(-2));
        assertThat(after.raiseAmount().doubleValue(), is(2.0));
    }

    @Test
    void limperAmountSetsPerLimperToMaxOfMinAndValue() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial().minPerLimperAmount(BigDecimal.valueOf(1));
        var after = reduced.apply(before, new Action.User.LimperAmount(0.5));
        assertThat(after.perLimperAmount().doubleValue(), is(1.0));
    }

    @Test
    void increaseLimperIncrementsPerLimperByStep() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial().minPerLimperAmount(BigDecimal.valueOf(1)).perLimper(BigDecimal.valueOf(2));
        var after = reduced.apply(before, new Action.User.IncreaseLimperAmount(0.25));
        assertThat(after.perLimperAmount().doubleValue(), is(2.25));
    }

    @Test
    void decreaseLimperDecrementsPerLimperButNotBelowMin() {
        var reduced = new ReducedState(new FakePersistence());
        var before = FloplessState.initial().minPerLimperAmount(BigDecimal.valueOf(1.5)).perLimper(BigDecimal.valueOf(2));
        var after = reduced.apply(before, new Action.User.DecreaseLimperAmount(-1));
        assertThat(after.perLimperAmount().doubleValue(), is(1.5));
    }

    @Test
    void deleteStateRemovesSavedChartAndKeepsCurrentWhenDeletingAnotherChart() {
        var utg = FloplessState.initial().forPosition(new Position.Utg());
        var co = FloplessState.initial().forPosition(new Position.Co());
        var btn = FloplessState.initial().forPosition(new Position.Btn());
        var persistence = new FakePersistence(List.of(utg, co, btn));
        var reduced = new ReducedState(persistence);
        var after = reduced.apply(utg, new Action.User.DeleteState(btn));
        var remaining = persistence.loadAll();
        var strategy = new com.github.idelstak.flopless.io.Strategy();

        assertThat(strategy.name(after), is(strategy.name(utg)));
        assertThat(remaining.size(), is(2));
        assertThat(remaining.stream().map(strategy::name).toList(), not(hasItem(strategy.name(btn))));
    }

    @Test
    void deleteStateFallsForwardWhenDeletingActiveChart() {
        var utg = FloplessState.initial().forPosition(new Position.Utg());
        var co = FloplessState.initial().forPosition(new Position.Co());
        var btn = FloplessState.initial().forPosition(new Position.Btn());
        var persistence = new FakePersistence(List.of(utg, co, btn));
        var reduced = new ReducedState(persistence);
        var after = reduced.apply(co, new Action.User.DeleteState(co));
        var strategy = new com.github.idelstak.flopless.io.Strategy();

        assertThat(strategy.name(after), is(strategy.name(btn)));
    }

    @Test
    void deleteStateFallsBackWhenDeletingLastActiveChart() {
        var utg = FloplessState.initial().forPosition(new Position.Utg());
        var co = FloplessState.initial().forPosition(new Position.Co());
        var btn = FloplessState.initial().forPosition(new Position.Btn());
        var persistence = new FakePersistence(List.of(utg, co, btn));
        var reduced = new ReducedState(persistence);
        var after = reduced.apply(btn, new Action.User.DeleteState(btn));
        var strategy = new com.github.idelstak.flopless.io.Strategy();

        assertThat(strategy.name(after), is(strategy.name(co)));
    }

    @Test
    void deleteStateLoadsInitialWhenLastSavedChartIsDeleted() {
        var utg = FloplessState.initial().forPosition(new Position.Utg());
        var persistence = new FakePersistence(List.of(utg));
        var reduced = new ReducedState(persistence);
        var after = reduced.apply(utg, new Action.User.DeleteState(utg));
        var strategy = new com.github.idelstak.flopless.io.Strategy();

        assertThat(strategy.name(after), is(strategy.name(FloplessState.initial())));
    }

    @Test
    void commitRangeUsesPremiumOverrideRaiseForPremiumHand() {
        var reduced = new ReducedState(new FakePersistence());
        var aa = new Grid().coordinate("AA").orElseThrow();
        var before = FloplessState.initial()
          .raise(BigDecimal.valueOf(3))
          .premiumOverride("AA", BigDecimal.valueOf(11))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(aa))
          .showPreview(SelectedRange.none().add(aa, new GridAction.Raise(BigDecimal.valueOf(3))));
        var after = reduced.apply(before, new Action.User.CommitRange());
        var raise = (GridAction.Raise) after.selectedRange().actionAt(aa);
        assertThat(raise.amount().doubleValue(), is(11.0));
    }

    @Test
    void commitRangeUsesOpenSizeForNonPremiumHands() {
        var reduced = new ReducedState(new FakePersistence());
        var kqo = new Grid().coordinate("KQo").orElseThrow();
        var before = FloplessState.initial()
          .raise(BigDecimal.valueOf(3))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(kqo))
          .showPreview(SelectedRange.none().add(kqo, new GridAction.Raise(BigDecimal.valueOf(3))));
        var after = reduced.apply(before, new Action.User.CommitRange());
        var raise = (GridAction.Raise) after.selectedRange().actionAt(kqo);
        assertThat(raise.amount().doubleValue(), is(3.0));
    }

    @Test
    void commitRangeUsesReraisedMultiplierByPositionAndDepth() {
        var reduced = new ReducedState(new FakePersistence());
        var kqo = new Grid().coordinate("KQo").orElseThrow();
        var ipBefore = FloplessState.initial()
          .forPosition(new Position.Btn())
          .face(new Facing.ReRaised.Vs3Bet())
          .raise(BigDecimal.valueOf(3))
          .reraisedIpMultiplier(BigDecimal.valueOf(3))
          .reraisedOopMultiplier(BigDecimal.valueOf(4))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(kqo))
          .showPreview(SelectedRange.none().add(kqo, new GridAction.Raise(BigDecimal.valueOf(3))));
        var ipAfter = reduced.apply(ipBefore, new Action.User.CommitRange());
        var ipRaise = (GridAction.Raise) ipAfter.selectedRange().actionAt(kqo);
        assertThat(ipRaise.amount().doubleValue(), is(9.0));

        var oopBefore = FloplessState.initial()
          .forPosition(new Position.Utg())
          .face(new Facing.ReRaised.Vs3Bet())
          .raise(BigDecimal.valueOf(3))
          .reraisedIpMultiplier(BigDecimal.valueOf(3))
          .reraisedOopMultiplier(BigDecimal.valueOf(4))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(kqo))
          .showPreview(SelectedRange.none().add(kqo, new GridAction.Raise(BigDecimal.valueOf(3))));
        var oopAfter = reduced.apply(oopBefore, new Action.User.CommitRange());
        var oopRaise = (GridAction.Raise) oopAfter.selectedRange().actionAt(kqo);
        assertThat(oopRaise.amount().doubleValue(), is(12.0));

        var fourBetBefore = FloplessState.initial()
          .forPosition(new Position.Btn())
          .face(new Facing.ReRaised.Vs4Bet())
          .raise(BigDecimal.valueOf(3))
          .reraisedIpMultiplier(BigDecimal.valueOf(3))
          .reraisedOopMultiplier(BigDecimal.valueOf(4))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(kqo))
          .showPreview(SelectedRange.none().add(kqo, new GridAction.Raise(BigDecimal.valueOf(3))));
        var fourBetAfter = reduced.apply(fourBetBefore, new Action.User.CommitRange());
        var fourBetRaise = (GridAction.Raise) fourBetAfter.selectedRange().actionAt(kqo);
        assertThat(fourBetRaise.amount().doubleValue(), is(12.0));

        var fiveBetBefore = FloplessState.initial()
          .forPosition(new Position.Utg())
          .face(new Facing.ReRaised.Vs5Bet())
          .raise(BigDecimal.valueOf(3))
          .reraisedIpMultiplier(BigDecimal.valueOf(3))
          .reraisedOopMultiplier(BigDecimal.valueOf(4))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(kqo))
          .showPreview(SelectedRange.none().add(kqo, new GridAction.Raise(BigDecimal.valueOf(3))));
        var fiveBetAfter = reduced.apply(fiveBetBefore, new Action.User.CommitRange());
        var fiveBetRaise = (GridAction.Raise) fiveBetAfter.selectedRange().actionAt(kqo);
        assertThat(fiveBetRaise.amount().doubleValue(), is(18.0));
    }

    @Test
    void commitRangeUsesOpenSizeWhenFacingSingleRaise() {
        var reduced = new ReducedState(new FakePersistence());
        var kqo = new Grid().coordinate("KQo").orElseThrow();
        var before = FloplessState.initial()
          .forPosition(new Position.Btn())
          .face(new Facing.Raised.VsUtg())
          .raise(BigDecimal.valueOf(3))
          .reraisedIpMultiplier(BigDecimal.valueOf(3))
          .reraisedOopMultiplier(BigDecimal.valueOf(4))
          .selectAction(new GridAction.Raise(BigDecimal.valueOf(3)))
          .beginDrag(Optional.of(kqo))
          .showPreview(SelectedRange.none().add(kqo, new GridAction.Raise(BigDecimal.valueOf(3))));
        var after = reduced.apply(before, new Action.User.CommitRange());
        var raise = (GridAction.Raise) after.selectedRange().actionAt(kqo);
        assertThat(raise.amount().doubleValue(), is(3.0));
    }
}

package com.github.idelstak.flopless.io;

import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.api.Assertions.*;

final class StrategyTest {

    @ParameterizedTest
    @MethodSource("states")
    void generatesStrategyName(FloplessState state) {
        var strategy = new Strategy();
        var name = strategy.name(state);
        assertNotNull(name);
        System.out.println(name);
    }

    @ParameterizedTest
    @MethodSource("states")
    void parseRestoresState(FloplessState original) {
        var strategy = new Strategy();
        var name = strategy.name(original);
        var parsed = strategy.parse(name);

        assertEquals(
          original.tableType().label(),
          parsed.tableType().label(),
          () -> "tableType mismatch for strategy name: " + name
        );
        assertEquals(
          original.tableType().stack().label(),
          parsed.tableType().stack().label(),
          () -> "stack label mismatch for strategy name: " + name
        );
        assertEquals(
          original.tableType().blinds().label(),
          parsed.tableType().blinds().label(),
          () -> "blinds label mismatch for strategy name: " + name
        );
        assertEquals(
          original.position().getClass(),
          parsed.position().getClass(),
          () -> "position mismatch for strategy name: " + name
        );
        assertEquals(
          original.squeezeLimpers(),
          parsed.squeezeLimpers(),
          () -> "squeezeLimpers mismatch for strategy name: " + name
        );
        assertEquals(
          original.facing().getClass(),
          parsed.facing().getClass(),
          () -> "facing mismatch for strategy name: " + name
        );
    }

    private static Stream<FloplessState> states() {
        return Stream.of(
          new TableType.SixMax(new StackDepth.Bb100(), new Blinds.OneSbTwoBB()),
          new TableType.SixMax(new StackDepth.Bb100(), new Blinds.TwoSbFourBB()),
          new TableType.SixMax(new StackDepth.Bb200(), new Blinds.OneSbTwoBB()),
          new TableType.SixMax(new StackDepth.Bb200(), new Blinds.TwoSbFourBB()),
          
          new TableType.NineMax(new StackDepth.Bb100(), new Blinds.OneSbTwoBB()),
          new TableType.NineMax(new StackDepth.Bb100(), new Blinds.TwoSbFourBB()),
          new TableType.NineMax(new StackDepth.Bb200(), new Blinds.OneSbTwoBB()),
          new TableType.NineMax(new StackDepth.Bb200(), new Blinds.TwoSbFourBB())
        ).flatMap(table ->
          Stream.of(
            new Position.Utg(), new Position.Utg1(), new Position.Utg2(),
            new Position.Lj(), new Position.Hj(), new Position.Co(),
            new Position.Btn(), new Position.Sb(), new Position.Bb()
          ).flatMap(pos ->
            Stream.of(
              new Facing.Open(),
              new Facing.Raised.VsUtg(), new Facing.Raised.VsUtg1(),
              new Facing.Raised.VsUtg2(), new Facing.Raised.VsLj(),
              new Facing.Raised.VsHj(), new Facing.Raised.VsCo(),
              new Facing.Raised.VsBtn(), new Facing.Raised.VsSb(),
              new Facing.ReRaised.Vs3Bet(), new Facing.ReRaised.Vs4Bet(),
              new Facing.ReRaised.Vs5Bet(), new Facing.ReRaised.VsAllIn()
            ).flatMap(facing ->
              Stream.of(true, false).map(squeeze ->
                FloplessState.initial()
                  .forTable(table)
                  .forPosition(pos)
                  .face(facing)
                  .toggleLimpersSqueeze(squeeze)
              )
            )
          )
        );
    }
}

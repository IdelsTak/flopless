package com.github.idelstak.flopless.io;

import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.*;

public final class Strategy {

    public String name(FloplessState state) {
        var table = state.tableType().label();
        var stack = state.tableType().stack().label();
        var blinds = state.tableType().blinds().label();
        var facingAction = formatFacing(state.facing());
        var squeezeLimpers = formatLimpersSqueeze(state.squeezeLimpers());
        var pos = posName(state.position());

        return String.format(
          "cash_%s_%s_%s_%s%s%s_strategy",
          table, stack, blinds, facingAction, squeezeLimpers, pos
        );
    }

    private String posName(Position position) {
        return position.getClass().getSimpleName().toLowerCase();
    }

    private String formatFacing(Facing facing) {
        return switch (facing) {
            case Facing.Open _ ->
                "";
            case Facing.Raised raised ->
                posName(raised.villain()) + "_raise_";
            case Facing.ReRaised reRaised ->
                reRaised.label() + "_";
        };
    }

    private String formatLimpersSqueeze(boolean squeezeLimpers) {
        if (squeezeLimpers) {
            return "limp_";
        }
        return "";
    }

    public FloplessState parse(String name) {
        if (!name.startsWith("cash_") || !name.endsWith("_strategy")) {
            throw new IllegalArgumentException("Invalid strategy name: " + name);
        }
        // strip prefix and suffix
        var core = name.substring(5, name.length() - 9);
        var parts = core.split("_");

        if (parts.length < 4) {
            throw new IllegalArgumentException("Incomplete strategy name: " + name);
        }

        // Table info
        TableType table = switch (parts[0]) {
            case "6max" ->
                new TableType.SixMax(stackFromLabel(parts[1]), blindsFromLabel(parts[2]));
            case "9max" ->
                new TableType.NineMax(stackFromLabel(parts[1]), blindsFromLabel(parts[2]));
            default ->
                throw new IllegalArgumentException("Unknown table: " + parts[0]);
        };

        // parts after table/stack/blinds
        int idx = 3;
        Facing facing = new Facing.Open();
        boolean squeeze = false;

        // Handle Raised (villain + "raise")
        if (idx + 1 < parts.length && "raise".equals(parts[idx + 1])) {
            String villainLabel = parts[idx];
            facing = switch (villainLabel) {
                case "utg" ->
                    new Facing.Raised.VsUtg();
                case "utg1" ->
                    new Facing.Raised.VsUtg1();
                case "utg2" ->
                    new Facing.Raised.VsUtg2();
                case "lj" ->
                    new Facing.Raised.VsLj();
                case "hj" ->
                    new Facing.Raised.VsHj();
                case "co" ->
                    new Facing.Raised.VsCo();
                case "btn" ->
                    new Facing.Raised.VsBtn();
                case "sb" ->
                    new Facing.Raised.VsSb();
                default ->
                    throw new IllegalArgumentException("Unknown villain: " + villainLabel);
            };
            idx += 2; // consume both villain and "raise"
        } // Handle ReRaised
        else if ("3bet".equals(parts[idx]) || "4bet".equals(parts[idx]) || "5bet".equals(parts[idx]) || "allin".equals(parts[idx])) {
            facing = switch (parts[idx]) {
                case "3bet" ->
                    new Facing.ReRaised.Vs3Bet();
                case "4bet" ->
                    new Facing.ReRaised.Vs4Bet();
                case "5bet" ->
                    new Facing.ReRaised.Vs5Bet();
                case "allin" ->
                    new Facing.ReRaised.VsAllIn();
                default ->
                    throw new IllegalArgumentException("Unknown reraised: " + parts[idx]);
            };
            idx++;
        }

        // Handle limpers
        if (idx < parts.length && "limp".equals(parts[idx])) {
            squeeze = true;
            idx++;
        }

        // Remaining part is player position
        if (idx >= parts.length) {
            throw new IllegalArgumentException("Missing position in strategy name: " + name);
        }
        Position pos = positionFromLabel(parts[idx]);

        return FloplessState.initial()
          .forTable(table)
          .face(facing)
          .toggleLimpersSqueeze(squeeze)
          .forPosition(pos);
    }

    private StackDepth stackFromLabel(String label) {
        return switch (label) {
            case "100bb" ->
                new StackDepth.Bb100();
            case "200bb" ->
                new StackDepth.Bb200();
            default ->
                throw new IllegalArgumentException("Unknown stack: " + label);
        };
    }

    private Blinds blindsFromLabel(String label) {
        return switch (label) {
            case "1bb-2bb" ->
                new Blinds.OneSbTwoBB();
            case "2bb-4bb" ->
                new Blinds.TwoSbFourBB();
            default ->
                throw new IllegalArgumentException("Unknown blinds: " + label);
        };
    }

    private Position positionFromLabel(String label) {
        return switch (label.toLowerCase()) {
            case "utg" ->
                new Position.Utg();
            case "utg1" ->
                new Position.Utg1();
            case "utg2" ->
                new Position.Utg2();
            case "lj" ->
                new Position.Lj();
            case "hj" ->
                new Position.Hj();
            case "co" ->
                new Position.Co();
            case "btn" ->
                new Position.Btn();
            case "sb" ->
                new Position.Sb();
            case "bb" ->
                new Position.Bb();
            default ->
                throw new IllegalArgumentException("Unknown position: " + label);
        };
    }
}

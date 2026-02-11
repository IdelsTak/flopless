package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.poker.player.*;
import java.math.*;
import java.util.*;

public record SizingConfig(
  BigDecimal openSizeBb,
  BigDecimal minOpenSizeBb,
  BigDecimal perLimperBb,
  BigDecimal minPerLimperBb,
  BigDecimal reraisedIpMultiplier,
  BigDecimal minReraisedIpMultiplier,
  BigDecimal reraisedOopMultiplier,
  BigDecimal minReraisedOopMultiplier,
  Map<String, BigDecimal> premiumRaiseOverridesBb) {

    public SizingConfig {
        premiumRaiseOverridesBb = Map.copyOf(premiumRaiseOverridesBb);
    }

    public SizingConfig withOpenSize(double amount) {
        var value = BigDecimal.valueOf(Math.max(minOpenSizeBb.doubleValue(), amount));
        return new SizingConfig(
          value,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          reraisedIpMultiplier,
          minReraisedIpMultiplier,
          reraisedOopMultiplier,
          minReraisedOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withMinOpenSize(double amount) {
        var min = BigDecimal.valueOf(Math.max(0, amount));
        var open = BigDecimal.valueOf(Math.max(min.doubleValue(), openSizeBb.doubleValue()));
        return new SizingConfig(
          open,
          min,
          perLimperBb,
          minPerLimperBb,
          reraisedIpMultiplier,
          minReraisedIpMultiplier,
          reraisedOopMultiplier,
          minReraisedOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withPerLimper(double amount) {
        var value = BigDecimal.valueOf(Math.max(minPerLimperBb.doubleValue(), amount));
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          value,
          minPerLimperBb,
          reraisedIpMultiplier,
          minReraisedIpMultiplier,
          reraisedOopMultiplier,
          minReraisedOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withMinPerLimper(double amount) {
        var min = BigDecimal.valueOf(Math.max(0, amount));
        var perLimper = BigDecimal.valueOf(Math.max(min.doubleValue(), perLimperBb.doubleValue()));
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimper,
          min,
          reraisedIpMultiplier,
          minReraisedIpMultiplier,
          reraisedOopMultiplier,
          minReraisedOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withReraisedIpMultiplier(double multiplier) {
        var value = BigDecimal.valueOf(Math.max(minReraisedIpMultiplier.doubleValue(), multiplier));
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          value,
          minReraisedIpMultiplier,
          reraisedOopMultiplier,
          minReraisedOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withReraisedOopMultiplier(double multiplier) {
        var value = BigDecimal.valueOf(Math.max(minReraisedOopMultiplier.doubleValue(), multiplier));
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          reraisedIpMultiplier,
          minReraisedIpMultiplier,
          value,
          minReraisedOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withPremiumOverride(String hand, double amountBb) {
        if (!new PremiumHands().contains(hand)) {
            return this;
        }
        var value = BigDecimal.valueOf(Math.max(minOpenSizeBb.doubleValue(), amountBb));
        var map = new HashMap<>(premiumRaiseOverridesBb);
        map.put(hand, value);
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          reraisedIpMultiplier,
          minReraisedIpMultiplier,
          reraisedOopMultiplier,
          minReraisedOopMultiplier,
          map
        );
    }

    public BigDecimal resolvedRaiseBb(String hand, Position hero, Facing facing, boolean squeezeLimpers) {
        var premium = premiumRaiseOverridesBb.get(hand);
        if (premium != null) {
            return premium;
        }

        if (facing instanceof Facing.ReRaised reRaised) {
            if (reRaised instanceof Facing.ReRaised.VsAllIn) {
                return openSizeBb;
            }
            var multiplier = reraisedMultiplierFor(hero, reRaised);
            return openSizeBb.multiply(multiplier);
        }

        return openSizeBb;
    }

    private BigDecimal reraisedMultiplierFor(Position hero, Facing.ReRaised reRaised) {
        var isInPosition = hero instanceof Position.Btn || hero instanceof Position.Co;
        var base = isInPosition ? reraisedIpMultiplier : reraisedOopMultiplier;
        var step = switch (reRaised) {
            case Facing.ReRaised.Vs3Bet _ ->
                BigDecimal.ZERO;
            case Facing.ReRaised.Vs4Bet _ ->
                BigDecimal.ONE;
            case Facing.ReRaised.Vs5Bet _ ->
                BigDecimal.valueOf(2);
            case Facing.ReRaised.VsAllIn _ ->
                throw new IllegalStateException("VsAllIn does not use reraised multiplier");
        };
        return base.add(step);
    }

    public static SizingConfig defaults() {
        var one = BigDecimal.ONE;
        return new SizingConfig(
          BigDecimal.valueOf(3.0),
          one,
          BigDecimal.valueOf(2.0),
          one,
          BigDecimal.valueOf(3.0),
          one,
          BigDecimal.valueOf(4.0),
          one,
          Map.of()
        );
    }
}

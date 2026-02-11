package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.poker.player.*;
import java.math.*;
import java.util.*;

public record SizingConfig(
  BigDecimal openSizeBb,
  BigDecimal minOpenSizeBb,
  BigDecimal perLimperBb,
  BigDecimal minPerLimperBb,
  int limperCount,
  BigDecimal threeBetIpMultiplier,
  BigDecimal minThreeBetIpMultiplier,
  BigDecimal threeBetOopMultiplier,
  BigDecimal minThreeBetOopMultiplier,
  Map<String, BigDecimal> premiumRaiseOverridesBb) {

    public static final Set<String> PREMIUM_HANDS = Set.of("AA", "KK", "QQ", "AKs", "AKo");

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
          limperCount,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
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
          limperCount,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
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
          limperCount,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
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
          limperCount,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withLimperCount(int count) {
        var value = Math.max(0, count);
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          value,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withThreeBetIpMultiplier(double multiplier) {
        var value = BigDecimal.valueOf(Math.max(minThreeBetIpMultiplier.doubleValue(), multiplier));
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          limperCount,
          value,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withThreeBetOopMultiplier(double multiplier) {
        var value = BigDecimal.valueOf(Math.max(minThreeBetOopMultiplier.doubleValue(), multiplier));
        return new SizingConfig(
          openSizeBb,
          minOpenSizeBb,
          perLimperBb,
          minPerLimperBb,
          limperCount,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          value,
          minThreeBetOopMultiplier,
          premiumRaiseOverridesBb
        );
    }

    public SizingConfig withPremiumOverride(String hand, double amountBb) {
        if (!PREMIUM_HANDS.contains(hand)) {
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
          limperCount,
          threeBetIpMultiplier,
          minThreeBetIpMultiplier,
          threeBetOopMultiplier,
          minThreeBetOopMultiplier,
          map
        );
    }

    public BigDecimal resolvedRaiseBb(String hand, Position hero, Facing facing, boolean squeezeLimpers) {
        var premium = premiumRaiseOverridesBb.get(hand);
        if (premium != null) {
            return premium;
        }

        if (facing instanceof Facing.Raised raised) {
            var isInPosition = hero.index() > raised.villain().index();
            var multiplier = isInPosition ? threeBetIpMultiplier : threeBetOopMultiplier;
            return openSizeBb.multiply(multiplier);
        }

        var base = openSizeBb;
        if (squeezeLimpers && limperCount > 0) {
            base = base.add(perLimperBb.multiply(BigDecimal.valueOf(limperCount)));
        }
        return base;
    }

    public static SizingConfig defaults() {
        var one = BigDecimal.ONE;
        return new SizingConfig(
          BigDecimal.valueOf(3.0),
          one,
          BigDecimal.valueOf(2.0),
          one,
          1,
          BigDecimal.valueOf(3.0),
          one,
          BigDecimal.valueOf(4.0),
          one,
          Map.of()
        );
    }
}

package com.github.idelstak.flopless.io;

import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.range.*;

public record PersistedChart(
  int chartVersion,
  SizingConfig sizing,
  SelectedRange range) {

    public static PersistedChart fromState(FloplessState state) {
        return new PersistedChart(2, state.sizingConfig(), state.selectedRange());
    }
}

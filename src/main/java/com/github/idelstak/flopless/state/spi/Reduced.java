package com.github.idelstak.flopless.state.spi;

import java.util.function.*;

public interface Reduced<T, U, R> extends BiFunction<T, U, R> {
}

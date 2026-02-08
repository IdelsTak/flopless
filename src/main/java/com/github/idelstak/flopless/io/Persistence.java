package com.github.idelstak.flopless.io;

import com.github.idelstak.flopless.state.*;

public interface Persistence {

    FloplessState load();

    void save(FloplessState state);
}

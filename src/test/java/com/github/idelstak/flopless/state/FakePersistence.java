package com.github.idelstak.flopless.state;

import com.github.idelstak.flopless.io.*;
import java.util.*;

final class FakePersistence implements Persistence {

    @Override
    public void save(FloplessState state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FloplessState> loadAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

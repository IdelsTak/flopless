package com.github.idelstak.flopless.io;

import com.github.idelstak.flopless.state.*;
import java.util.*;

public interface Persistence {

    void save(FloplessState state);

    void delete(FloplessState state);

    List<FloplessState> loadAll();
}

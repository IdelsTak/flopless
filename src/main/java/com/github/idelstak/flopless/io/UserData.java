package com.github.idelstak.flopless.io;

import java.nio.file.*;

final class UserData {

    Path directory() {
        var home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        Path base;

        if (os.contains("win")) {
            base = Paths.get(System.getenv("APPDATA"));
        } else if (os.contains("mac")) {
            base = Paths.get(home, "Library", "Application Support");
        } else { // Linux / Unix
            base = Paths.get(home, ".flopless");
        }

        return base.resolve("flopless");
    }
}

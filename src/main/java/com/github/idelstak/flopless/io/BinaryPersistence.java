package com.github.idelstak.flopless.io;

import com.github.idelstak.flopless.state.*;
import java.io.*;
import java.nio.file.*;

public final class BinaryPersistence implements Persistence {

    private final Path path;

    public BinaryPersistence(String fileName) {
        this.path = new UserData().directory().resolve(fileName);
    }

    @Override
    public void save(FloplessState state) {
        try {
            Files.createDirectories(path.getParent());
            try (var out = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                out.writeObject(state);
            }
        } catch (IOException e) {
            System.out.println("[BINARY PERSISTENCE] " + e);
            throw new IllegalStateException("Failed to save state to " + path, e);
        }
    }

    @Override
    public FloplessState load() {
        if (!Files.exists(path)) {
            return FloplessState.initial();
        }
        try (var in = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (FloplessState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[BINARY PERSISTENCE] " + e);
            throw new IllegalStateException("Failed to load state from " + path, e);
        }
    }
}

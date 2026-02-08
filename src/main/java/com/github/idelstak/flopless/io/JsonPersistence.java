package com.github.idelstak.flopless.io;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.*;
import com.github.idelstak.flopless.state.*;
import java.io.*;
import java.nio.file.*;

public final class JsonPersistence implements Persistence {

    private final Path path;
    private final ObjectMapper mapper;

    public JsonPersistence(String fileName) {
        this.path = new UserData().directory().resolve(fileName);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new Jdk8Module());
    }

    @Override
    public void save(FloplessState state) {
        try {
            Files.createDirectories(path.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), state);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save state to " + path, e);
        }
    }

    @Override
    public FloplessState load() {
        if (!Files.exists(path)) {
            return FloplessState.initial();
        }
        try {
            return mapper.readValue(path.toFile(), FloplessState.class);
        } catch (IOException e) {
            System.out.println("e = " + e);
            throw new IllegalStateException("Failed to load state from " + path, e);
        }
    }
}

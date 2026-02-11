package com.github.idelstak.flopless.io;

import com.fasterxml.jackson.databind.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.range.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class JsonPersistence implements Persistence {

    private final Path directory;
    private final ObjectMapper mapper;
    private final Strategy strategy;

    public JsonPersistence() {
        directory = new UserData().directory();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        strategy = new Strategy();
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create directory " + directory, e);
        }
    }

    @Override
    public List<FloplessState> loadAll() {
        try (var files = Files.list(directory)) {
            var states = new ArrayList<FloplessState>();
            files
              .filter(f -> f.toString().endsWith(".json"))
              .sorted(Comparator.comparing(path -> path.getFileName().toString()))
              .forEach(file -> {
                try {
                    // extract strategy name from file
                    var fileName = file.getFileName().toString();
                    var name = fileName.substring(0, fileName.length() - 5); // strip ".json"
                    var state = strategy.parse(name);
                    var node = mapper.readTree(file.toFile());
                    if (node.has("range")) {
                        var chart = mapper.treeToValue(node, PersistedChart.class);
                        states.add(state.selectRange(chart.range()).withSizing(chart.sizing()));
                    } else {
                        var range = mapper.treeToValue(node, SelectedRange.class);
                        states.add(state.selectRange(range));
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to load SelectedRange from " + file, e);
                }
              });
            return List.copyOf(states);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to list files in directory " + directory, e);
        }
    }

    @Override
    public void save(FloplessState state) {
        Path file = directory.resolve(strategy.name(state) + ".json");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), PersistedChart.fromState(state));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save SelectedRange to " + file, e);
        }
    }

    @Override
    public void delete(FloplessState state) {
        Path file = directory.resolve(strategy.name(state) + ".json");
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete SelectedRange at " + file, e);
        }
    }
}

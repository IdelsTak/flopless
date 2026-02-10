package com.github.idelstak.flopless.io;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.github.idelstak.flopless.grid.*;
import com.github.idelstak.flopless.state.range.*;
import java.io.*;
import java.util.*;

public final class DeserializedRange extends JsonDeserializer<SelectedRange> {

    @Override
    public SelectedRange deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var node = p.readValueAs(Map.class);
        if (node == null) {
            throw new IllegalStateException("Deserialization failed: JSON is null");
        }
        var map = new HashMap<Coordinate, GridAction>();
        var grid = new Grid();
        for (var rawEntry : node.entrySet()) {
            if (!(rawEntry instanceof Map.Entry<?, ?> entry)) {
                throw new IllegalStateException("Expected map entry but found " + rawEntry);
            }
            var hand = entry.getKey().toString();
            var actionLabel = entry.getValue().toString();
            var coord = grid.coordinate(hand).orElse(null);
            if (coord == null) {
                throw new IllegalStateException("Unknown hand '" + hand + "' in JSON");
            }
            var action = GridAction.fromSimpleLabel(actionLabel);
            if (action == null) {
                throw new IllegalStateException("Unknown GridAction label '" + actionLabel + "' in JSON");
            }
            map.put(coord, action);
        }
        return new SelectedRange(map);
    }
}

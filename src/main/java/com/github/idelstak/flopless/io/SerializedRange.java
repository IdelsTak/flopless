package com.github.idelstak.flopless.io;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.github.idelstak.flopless.poker.action.*;
import com.github.idelstak.flopless.state.range.*;
import java.io.*;

public final class SerializedRange extends JsonSerializer<SelectedRange> {

    @Override
    public void serialize(SelectedRange value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        for (var entry : value.map().entrySet()) {
            var action = entry.getValue();
            var gameAction = action.gameAction();
            var actionText = switch (gameAction) {
                case GameAction.Money m ->
                    "%sbb".formatted(m.amount().stripTrailingZeros().toPlainString());
                case GameAction.UknownMoney um ->
                    um.simpleLabel();
            };
            gen.writeStringField(entry.getKey().hand(), actionText);
        }
        gen.writeEndObject();
    }
}

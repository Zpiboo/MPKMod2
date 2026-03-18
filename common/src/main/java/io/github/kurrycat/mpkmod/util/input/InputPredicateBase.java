package io.github.kurrycat.mpkmod.util.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class InputPredicateBase {
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InputPredicateBase create(JsonNode node) {
        if (node.isTextual())
            return new InputPredicate(node.asText());
        if (node.isInt())
            return new InputPredicateReference(node.asInt());

        return null;
    }

    public abstract boolean matches(InputVector input);
}

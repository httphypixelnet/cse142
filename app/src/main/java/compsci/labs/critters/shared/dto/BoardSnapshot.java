package compsci.labs.critters.shared.dto;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/** Minimal payload describing the board for remote clients. */
public record BoardSnapshot(int width, int height, List<Cell> cells) {
    public record Cell(int x, int y, String appearance, String colorHex) {}

    public static BoardSnapshot empty(int width, int height) {
        return new BoardSnapshot(width, height, Collections.emptyList());
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BoardSnapshot", e);
        }
    }

    public static BoardSnapshot fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, BoardSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize BoardSnapshot", e);
        }
    }
}

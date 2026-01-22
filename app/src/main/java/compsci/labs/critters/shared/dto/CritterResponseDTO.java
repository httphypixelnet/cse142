package compsci.labs.critters.shared.dto;

import compsci.labs.critters.shared.Critter;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public record CritterResponseDTO(UUID critterId, Critter.Action action, String value) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CritterResponseDTO", e);
        }
    }

    public static CritterResponseDTO fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, CritterResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize CritterResponseDTO", e);
        }
    }
}

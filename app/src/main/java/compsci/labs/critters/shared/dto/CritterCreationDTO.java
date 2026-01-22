package compsci.labs.critters.shared.dto;

import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public record CritterCreationDTO(UUID id, String species, String colorHex, String appearance) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CritterCreationDTO", e);
        }
    }

    public static CritterCreationDTO fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, CritterCreationDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize CritterCreationDTO", e);
        }
    }
}

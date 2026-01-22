package compsci.labs.critters.shared.dto;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public record BatchCritterResponseDTO(List<CritterResponseDTO> responses) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BatchCritterResponseDTO", e);
        }
    }

    public static BatchCritterResponseDTO fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, BatchCritterResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize BatchCritterResponseDTO", e);
        }
    }
}

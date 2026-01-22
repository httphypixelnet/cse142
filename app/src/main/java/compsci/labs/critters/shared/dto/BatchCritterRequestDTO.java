package compsci.labs.critters.shared.dto;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public record BatchCritterRequestDTO(List<CritterRequestDTO> requests) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BatchCritterRequestDTO", e);
        }
    }

    public static BatchCritterRequestDTO fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, BatchCritterRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize BatchCritterRequestDTO", e);
        }
    }
}

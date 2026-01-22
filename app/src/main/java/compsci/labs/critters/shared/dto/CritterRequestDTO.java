package compsci.labs.critters.shared.dto;

import compsci.labs.critters.shared.OpCode;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public record CritterRequestDTO(UUID critterId, OpCode opCode, CritterInfoDTO info) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CritterRequestDTO", e);
        }
    }

    public static CritterRequestDTO fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, CritterRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize CritterRequestDTO", e);
        }
    }
}

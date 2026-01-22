package compsci.labs.critters.client;

import compsci.labs.critters.shared.Critter;
import compsci.labs.critters.shared.Animal;
import compsci.labs.critters.shared.dto.CritterCreationDTO;

import java.lang.reflect.Constructor;
import java.util.*;
import java.awt.Color;

import compsci.labs.critters.shared.dto.CritterRequestDTO;
import compsci.labs.critters.shared.dto.CritterResponseDTO;
import compsci.labs.critters.shared.dto.CritterInfoDTO;
import compsci.labs.critters.shared.DebugLogger;

import compsci.labs.critters.shared.dto.BatchCritterRequestDTO;
import compsci.labs.critters.shared.dto.BatchCritterResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class ClientCritterManager {
    private final Map<UUID, Critter> critters = new HashMap<>();
    private final SocketHandler socketHandler;

    public ClientCritterManager(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
        socketHandler.setRequestConsumer(this::handleRequest);
        socketHandler.setBatchRequestConsumer(this::handleBatchRequest);
    }

    private void handleBatchRequest(BatchCritterRequestDTO batchRequest) {
        DebugLogger.log("[Client] Handling batch request with " + batchRequest.requests().size() + " items");
        List<CritterResponseDTO> responses = new ArrayList<>();
        for (CritterRequestDTO req : batchRequest.requests()) {
            Critter c = critters.get(req.critterId());
            if (c != null) {
                if (req.opCode() == compsci.labs.critters.shared.OpCode.MOVE) {
                    Critter.Action action = c.getMove(req.info());
                    responses.add(new CritterResponseDTO(req.critterId(), action, null));
                }
            }
        }
        DebugLogger.log("[Client] Sending batch response with " + responses.size() + " items");
        socketHandler.send(new BatchCritterResponseDTO(responses).toJson());
    }

    private void handleRequest(CritterRequestDTO request) {
        DebugLogger.log("[Client] Handling request for " + request.critterId() + ": " + request.opCode());
        Critter c = critters.get(request.critterId());
        if (c == null) {
            DebugLogger.log("[Client] Critter not found: " + request.critterId());
            return;
        }

        switch (request.opCode()) {
            case MOVE:
                Critter.Action action = c.getMove(request.info());
                DebugLogger.log("[Client] Sending move response: " + action);
                socketHandler.send(new CritterResponseDTO(request.critterId(), action, null).toJson());
                break;
            case COLOR:
                // Not implemented fully yet, server uses initial color
                break;
            case TO_STRING:
                // Not implemented fully yet, server uses initial appearance
                break;
        }
    }

    public void add(int count, Class<? extends Critter> critterClass) {
        for (int i = 0; i < count; i++) {
            try {

                Constructor<? extends Critter> constructor = (Constructor<? extends Critter>) critterClass.getConstructors()[0];
                Critter c;
                if (critterClass.getSimpleName().contains("Bear")) {
                    c = constructor.newInstance(new Random().nextBoolean());
                }
                else {
                    c = constructor.newInstance();
                }
                UUID id;
                if (c instanceof Animal) {
                    id = ((Animal) c).id;
                } else {
                    // wrap legacy Critter objects to make them Animal
                    id = UUID.randomUUID();
                }
                critters.put(id, c);
                
                String colorHex = String.format("#%06x", c.getColor().getRGB() & 0xFFFFFF);
                CritterCreationDTO dto = new CritterCreationDTO(id, critterClass.getSimpleName(), colorHex, c.toString());
                socketHandler.send(dto.toJson());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

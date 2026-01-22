package compsci.labs.critters.server;

import compsci.labs.critters.shared.Critter;
import compsci.labs.critters.shared.CritterInfo;
import compsci.labs.critters.shared.OpCode;
import compsci.labs.critters.shared.dto.CritterRequestDTO;
import compsci.labs.critters.shared.dto.CritterResponseDTO;
import compsci.labs.critters.shared.DebugLogger;
import io.javalin.websocket.WsContext;

import java.awt.Color;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RemoteCritter extends Critter {
    private final UUID id;
    private final String species;
    private final WsContext ctx;
    private final String initialAppearance;
    private final Color initialColor;
    
    private CompletableFuture<CritterResponseDTO> pendingResponse;

    public RemoteCritter(UUID id, String species, String colorHex, String appearance, WsContext ctx) {
        this.id = id;
        this.species = species;
        this.ctx = ctx;
        this.initialAppearance = appearance;
        this.initialColor = Color.decode(colorHex);
    }

    public UUID getId() {

        return id;
    }

    private Action cachedAction;

    public void setNextAction(Action action) {
        this.cachedAction = action;
    }

    public void completeResponse(CritterResponseDTO response) {
        if (pendingResponse != null) {
            pendingResponse.complete(response);
        }
    }

    public WsContext getContext() {
        return ctx;
    }

    @Override
    public Action getMove(CritterInfo info) {
        if (cachedAction != null) {
            Action a = cachedAction;
            cachedAction = null;
            return a;
        }
        return Action.LEFT;
    }

    @Override
    public Color getColor() {
        return initialColor;
    }

    @Override
    public String toString() {
        return initialAppearance;
    }
}

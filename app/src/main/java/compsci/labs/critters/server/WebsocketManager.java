package compsci.labs.critters.server;

import compsci.labs.critters.shared.dto.BoardSnapshot;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.util.UUID;

import compsci.labs.critters.shared.dto.CritterCreationDTO;
import compsci.labs.critters.shared.dto.CritterResponseDTO;
import compsci.labs.critters.shared.DebugLogger;

import compsci.labs.critters.shared.dto.BatchCritterResponseDTO;

class WebsocketManager {
    public static void start(Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                String gameIdStr = ctx.queryParam("gameId");
                String idStr = ctx.queryParam("id");
                if (gameIdStr != null && idStr != null) {
                    handleConnect(UUID.fromString(gameIdStr), UUID.fromString(idStr), ctx);
                }
            });
            ws.onClose(BoardUpdateService::unregister);
            ws.onMessage(ctx -> {
                String gameIdStr = ctx.queryParam("gameId");
                if (gameIdStr == null) return;
                UUID gameId = UUID.fromString(gameIdStr);
                GameState state = GameManager.games.get(gameId);
                if (state == null) return;

                String msg = ctx.message();
                DebugLogger.log("[Server] Raw message: " + msg);
                if (msg.contains("species")) {
                    // Creation
                    DebugLogger.log("[Server] Received creation request");
                    CritterCreationDTO dto = CritterCreationDTO.fromJson(msg);
                    RemoteCritter rc = new RemoteCritter(dto.id(), dto.species(), dto.colorHex(), dto.appearance(), ctx);
                    state.board().add(rc);
                    state.registerRemoteCritter(rc);
                } else if (msg.contains("responses")) {
                    // Batch Response
                    BatchCritterResponseDTO batch = BatchCritterResponseDTO.fromJson(msg);
                    DebugLogger.log("[Server] Received batch response with " + batch.responses().size() + " items");
                    state.board().completeBatch(ctx, batch.responses());
                } else if (msg.toLowerCase().contains("hello")){
                    ctx.send("Hello World!");
                } else {
                    // Single Response (Legacy/Fallback)
                    CritterResponseDTO dto = CritterResponseDTO.fromJson(msg);
                    DebugLogger.log("[Server] Received response for " + dto.critterId());
                    RemoteCritter rc = state.getRemoteCritter(dto.critterId());
                    if (rc != null) {
                        rc.completeResponse(dto);
                    } else {
                        DebugLogger.log("[Server] RemoteCritter not found for " + dto.critterId());
                    }
                }
            });
        });
    }

    private static void handleConnect(UUID gameId, UUID id, WsContext ctx) {
        GameState state = GameManager.games.get(gameId);
        if (state == null) return; // Or close connection
        BoardSnapshot snapshot = BoardSnapshotBuilder.fromModel(state.board());
        BoardUpdateService.register(ctx);
        ctx.send(snapshot.toJson());
    }
}

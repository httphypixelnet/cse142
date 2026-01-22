package compsci.labs.critters.server;

import compsci.labs.critters.shared.dto.BoardSnapshot;
import io.javalin.websocket.WsContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Simple broadcaster that streams board updates to all connected contexts. */
final class BoardUpdateService {
    private static final List<WsContext> listeners = new CopyOnWriteArrayList<>();

    private BoardUpdateService() {}

    static void register(WsContext ctx) {
        listeners.add(ctx);
    }

    static void unregister(WsContext ctx) {
        listeners.remove(ctx);
    }

    static void broadcast(BoardSnapshot snapshot) {
        String payload = snapshot.toJson();
        listeners.removeIf(ctx -> !ctx.session.isOpen());
        listeners.forEach(ctx -> ctx.send(payload));
    }
}

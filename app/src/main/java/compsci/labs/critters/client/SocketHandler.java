package compsci.labs.critters.client;

import compsci.labs.critters.shared.OpCode;
import compsci.labs.critters.shared.DebugLogger;
import compsci.labs.critters.shared.dto.BatchCritterRequestDTO;
import compsci.labs.critters.shared.dto.BoardSnapshot;
import compsci.labs.critters.shared.dto.CritterRequestDTO;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class SocketHandler {
    private final WebSocketClient client;
    private final StatusConsumer statusConsumer;
    private RequestConsumer requestConsumer;
    private BatchRequestConsumer batchRequestConsumer;

    private WebSocket webSocket;

    public SocketHandler(String url, StatusConsumer statusConsumer, BoardSnapshotConsumer snapshotConsumer) {
        this.client = new WebSocketClient(url, new Handler(statusConsumer, snapshotConsumer, this), GameClient.createUnsafeClient());
        this.statusConsumer = statusConsumer;
    }

    public void setRequestConsumer(RequestConsumer requestConsumer) {
        this.requestConsumer = requestConsumer;
    }

    public void setBatchRequestConsumer(BatchRequestConsumer batchRequestConsumer) {
        this.batchRequestConsumer = batchRequestConsumer;
    }

    public void connect() {
        client.connect().thenAccept(ws -> this.webSocket = ws);
    }

    public void send(String message) {
        if (webSocket != null) {
            webSocket.sendText(message, true);
        }
    }

    private record Handler(StatusConsumer statusConsumer, BoardSnapshotConsumer snapshotConsumer,
                           SocketHandler parent) implements WebSocket.Listener {

        @Override
            public void onOpen(WebSocket webSocket) {
                statusConsumer.onStatus(SocketStatus.CONNECTED);
                webSocket.sendText("Hello, server!", true);
                DebugLogger.log("[Client] Connected to server!");
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                String msg = data.toString();
                DebugLogger.log("[Client] Raw message: " + msg);
                if (msg.contains("requests")) {
                    // Batch Request
                    if (parent.batchRequestConsumer != null) {
                        parent.batchRequestConsumer.onBatchRequest(BatchCritterRequestDTO.fromJson(msg));
                    }
                } else if (msg.contains("opCode")) {
                    // Request
                    if (parent.requestConsumer != null) {
                        parent.requestConsumer.onRequest(CritterRequestDTO.fromJson(msg));
                    } else {
                        DebugLogger.log("[Client] No request consumer registered!");
                    }
                } else if (msg.contains("Hello")) {
                    DebugLogger.log("[Client] Server said hello!");
                } else {
                    // Snapshot
                    snapshotConsumer.onSnapshot(BoardSnapshot.fromJson(msg));
                }
                return null;
            }
        }

    interface StatusConsumer {
        void onStatus(SocketStatus status);
    }

    interface BoardSnapshotConsumer {
        void onSnapshot(compsci.labs.critters.shared.dto.BoardSnapshot snapshot);
    }

    interface RequestConsumer {
        void onRequest(compsci.labs.critters.shared.dto.CritterRequestDTO request);
    }

    interface BatchRequestConsumer {
        void onBatchRequest(compsci.labs.critters.shared.dto.BatchCritterRequestDTO request);
    }

    enum SocketStatus { CONNECTED, DISCONNECTED }
}

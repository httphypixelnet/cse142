package compsci.labs.critters.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class WebSocketClient {
    private final URI uri;
    private final HttpClient httpClient;
    private final WebSocket.Listener listener;

    WebSocketClient(String uri, WebSocket.Listener listener, HttpClient httpClient) {
        this.uri = URI.create(Objects.requireNonNull(uri, "uri"));
        this.listener = Objects.requireNonNull(listener, "listener");
        this.httpClient = httpClient;
    }

    public CompletableFuture<WebSocket> connect() {
        return httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(5)).buildAsync(uri, listener);
    }
}

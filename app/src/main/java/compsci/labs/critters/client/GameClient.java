package compsci.labs.critters.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class GameClient {
    private static final String BASE_URL = "https://game.local:8443";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public GameClient() {
        this.httpClient = createUnsafeClient();
        this.mapper = new ObjectMapper();
    }

    public UUID createGame() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/create"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to create game: " + response.statusCode() + " " + response.body());
            }
            JsonNode node = mapper.readTree(response.body());
            return UUID.fromString(node.get("id").asText());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create game", e);
        }
    }

    public String joinGame(UUID gameId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/join/" + gameId))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to join game: " + response.statusCode() + " " + response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to join game", e);
        }
    }

    public static HttpClient createUnsafeClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return HttpClient.newBuilder().sslContext(sc).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

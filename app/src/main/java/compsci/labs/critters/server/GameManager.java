package compsci.labs.critters.server;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

class GameManager {
    private static final String HTTPS_HOST = resolve("critters.host", "CRITTERS_TLS_HOST", "game.local");
    private static final int HTTPS_PORT = Integer.parseInt(resolve("critters.port", "CRITTERS_TLS_PORT", "8443"));
    private static final String KEYSTORE_LOCATION = resolve("critters.keystore", "CRITTERS_TLS_KEYSTORE", "certs/localhost.pfx");
    private static final String KEYSTORE_PASSWORD = resolve("critters.keystore.password", "CRITTERS_TLS_PASSWORD", "test");

    static HashMap<UUID, GameState> games = new HashMap<>();
    static final Javalin app = Javalin.create(GameManager::configureServer)
            .post("/create", (ctx) -> {
                GameState state = new GameState();
                games.put(state.getGameId(), state);
                ctx.result(String.format("{ \"accepted\": \"true\", \"id\": \"%s\" }", state.getGameId()));
            }).get("/join/{gameId}", ctx -> {
                String gameString = ctx.pathParam("gameId");
                if (gameString.isBlank()) {
                    ctx.status(400);
                }

                UUID gameId = UUID.fromString(gameString);
                UUID newManager = games.get(gameId).createAnimalManager();
                ctx.result(String.format("/ws?gameId=%s&id=%s", gameId, newManager));
            });

    static {
        WebsocketManager.start(app);
    }

    private static void configureServer(JavalinConfig config) {
        config.jetty.defaultHost = HTTPS_HOST;
        config.jetty.defaultPort = HTTPS_PORT;
        config.jetty.modifyServer(GameManager::configureConnectors);
        config.jetty.modifyHttpConfiguration(cfg -> cfg.addCustomizer(new SecureRequestCustomizer()));
    }

    private static void configureConnectors(Server server) {
        HttpConfiguration httpsConfig = new HttpConfiguration();
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(resolveKeystoreResource());
        sslContextFactory.setKeyStorePassword(KEYSTORE_PASSWORD);
        sslContextFactory.setKeyManagerPassword(KEYSTORE_PASSWORD);

        ServerConnector sslConnector = new ServerConnector(
                server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(httpsConfig)
        );
        sslConnector.setHost(HTTPS_HOST);
        sslConnector.setPort(HTTPS_PORT);

        server.setConnectors(new Connector[]{sslConnector});
    }

    private static Resource resolveKeystoreResource() {
        Path directPath = Path.of(KEYSTORE_LOCATION);
        try {
            if (Files.exists(directPath)) {
                return Resource.newResource(directPath.toUri());
            }
            Resource classpathResource = Resource.newClassPathResource(KEYSTORE_LOCATION);
            if (classpathResource == null) {
                throw new IllegalStateException("TLS keystore not found at " + KEYSTORE_LOCATION);
            }
            return classpathResource;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load TLS keystore " + KEYSTORE_LOCATION, e);
        }
    }

    static int httpsPort() {
        return HTTPS_PORT;
    }

    static String httpsHost() {
        return HTTPS_HOST;
    }

    static String keystoreLocation() {
        return KEYSTORE_LOCATION;
    }

    private static String resolve(String sysProp, String envVar, String defaultValue) {
        return System.getProperty(sysProp, System.getenv().getOrDefault(envVar, defaultValue));
    }
}

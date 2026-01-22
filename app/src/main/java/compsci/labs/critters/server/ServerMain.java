package compsci.labs.critters.server;

public class ServerMain {
    public static void main(String[] args) {
        GameManager.app.start();
        System.out.printf("Critters server listening at https://%s:%d (wss)%n", GameManager.httpsHost(), GameManager.httpsPort());
    }
}

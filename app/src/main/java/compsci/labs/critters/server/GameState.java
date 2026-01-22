package compsci.labs.critters.server;

import compsci.labs.critters.shared.Animal;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static compsci.labs.critters.server.GameManager.app;

class GameState {
    private final UUID gameId = UUID.randomUUID();
    private final CritterModel board = new CritterModel(60, 40);
    private final HashMap<UUID, AnimalManager> ANIMAL_MAP = new HashMap<>();
    private final HashMap<UUID, AnimalManager> MANAGER_MAP = new HashMap<>();

    private final HashMap<UUID, RemoteCritter> REMOTE_CRITTERS = new HashMap<>();
    private boolean running = false;

    public UUID getGameId() { return gameId; }
    public Collection<AnimalManager> managers() { return this.MANAGER_MAP.values(); }
    public AnimalManager getAnimal(UUID animalId) {
        return this.ANIMAL_MAP.get(animalId);
    }
    public AnimalManager getById(UUID managerId) { return MANAGER_MAP.get(managerId); }
    CritterModel board() {
        return board;
    }

    public void registerRemoteCritter(RemoteCritter rc) {
        REMOTE_CRITTERS.put(rc.getId(), rc);
        if (!running) {
            startLoop();
        }
    }

    public RemoteCritter getRemoteCritter(UUID id) {
        return REMOTE_CRITTERS.get(id);
    }

    private void startLoop() {
        running = true;
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(250); // Game tick
                    board.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public UUID createAnimalManager() {
        AnimalManager m = new AnimalManager(this);
        this.MANAGER_MAP.put(m.id, m);
        // WebsocketManager.init(gameId, m.id, app); // Removed
        return m.id;
    }
    public void startManaging(Animal animal, AnimalManager manager) {
        if (animal == null || manager == null) {
            return;
        }
        this.ANIMAL_MAP.put(animal.id, manager);
        if (!this.MANAGER_MAP.containsKey(manager.id)) {
            this.MANAGER_MAP.put(manager.id, manager);
        }
    }
}
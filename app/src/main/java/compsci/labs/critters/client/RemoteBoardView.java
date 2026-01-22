package compsci.labs.critters.client;

import compsci.labs.critters.shared.dto.BoardSnapshot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** Client-side view that updates whenever a new snapshot arrives. */
final class RemoteBoardView implements BoardView, SocketHandler.BoardSnapshotConsumer {
    private final AtomicReference<BoardSnapshot> snapshotRef;

    RemoteBoardView(BoardSnapshot initial) {
        snapshotRef = new AtomicReference<>(initial);
    }

    @Override
    public int width() {
        return snapshotRef.get().width();
    }

    @Override
    public int height() {
        return snapshotRef.get().height();
    }

    @Override
    public List<Cell> cells() {
        BoardSnapshot snapshot = snapshotRef.get();
        List<Cell> cells = new ArrayList<>(snapshot.cells().size());
        for (BoardSnapshot.Cell cell : snapshot.cells()) {
            cells.add(new Cell(cell.x(), cell.y(), cell.appearance(), Color.decode(cell.colorHex())));
        }
        return cells;
    }

    @Override
    public void onSnapshot(BoardSnapshot snapshot) {
        snapshotRef.set(snapshot);
    }
}


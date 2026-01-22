package compsci.labs.critters.server;

import compsci.labs.critters.shared.Critter;
import compsci.labs.critters.shared.dto.BoardSnapshot;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Converts the authoritative model into lightweight snapshots for clients. */
final class BoardSnapshotBuilder {
    private BoardSnapshotBuilder() {}

    static BoardSnapshot fromModel(CritterModel model) {
        List<BoardSnapshot.Cell> cells = new ArrayList<>();
        Iterator<Critter> critters = model.iterator();
        while (critters.hasNext()) {
            Critter critter = critters.next();
            Point point = model.getPoint(critter);
            Color color = model.getColor(critter);
            String appearance = model.getAppearance(critter);
            cells.add(new BoardSnapshot.Cell(point.x, point.y, appearance, toHex(color)));
        }
        return new BoardSnapshot(model.getWidth(), model.getHeight(), cells);
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}


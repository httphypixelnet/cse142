package compsci.labs.critters.client;

import java.awt.Color;
import java.util.List;

/** Simple read-only view over a critter board for rendering. */
public interface BoardView {
    int width();
    int height();
    List<Cell> cells();

    record Cell(int x, int y, String appearance, Color color) {}
}


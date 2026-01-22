// PREMADE
package compsci.labs.critters.client;// Class CritterPanel displays a grid of critters

import javax.swing.*;
import java.awt.*;

public class CritterPanel extends JPanel {
    private final BoardView boardView;
    private final Font myFont;
    private static boolean created;

    public static final int FONT_SIZE = 12;

    public CritterPanel(BoardView boardView) {
        if (created)
            throw new RuntimeException("Only one world allowed");
        created = true;

        this.boardView = boardView;
        myFont = new Font("Monospaced", Font.BOLD, FONT_SIZE + 4);
        setBackground(Color.CYAN);
        setPreferredSize(new Dimension(FONT_SIZE * boardView.width() + 20,
                                       FONT_SIZE * boardView.height() + 20));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(myFont);
        for (BoardView.Cell cell : boardView.cells()) {
            int drawX = cell.x() * FONT_SIZE + 10;
            int drawY = cell.y() * FONT_SIZE + 20;
            g.setColor(Color.BLACK);
            g.drawString(cell.appearance(), drawX + 1, drawY + 1);
            g.setColor(cell.color());
            g.drawString(cell.appearance(), drawX, drawY);
        }
    }
}

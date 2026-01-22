// PREMADE
package compsci.labs.critters.client;// Class CritterFrame provides the user interface for a simple simulation
// program.

import compsci.labs.critters.shared.Critter;
// import compsci.labs.critters.server.CritterModel; // Removed
import compsci.labs.critters.shared.OpCode;
import compsci.labs.critters.shared.dto.BoardSnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

public class CritterFrame extends JFrame {
    private final RemoteBoardView boardView;
    private final SocketHandler socketHandler;
    private SocketHandler.SocketStatus status = SocketHandler.SocketStatus.DISCONNECTED;
    private static boolean created;
    private javax.swing.Timer myTimer;
    private JButton[] counts;
    private JButton countButton;
    private boolean started;
    // private CritterModel myModel; // Removed
    private ClientCritterManager critterManager; // Added
    private CritterPanel myPicture;

    public CritterFrame(int width, int height) {
        // this prevents someone from trying to create their own copy of
        // the GUI components
        if (CritterFrame.created)
            throw new RuntimeException("Only one world allowed");
        CritterFrame.created = true;

        // create frame and model
        setTitle("CSE142 critter simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // myModel = new CritterModel(width, height); // Removed
        boardView = new RemoteBoardView(BoardSnapshot.empty(width, height));
        CritterPanel myPicture = new CritterPanel(boardView);
        add(myPicture, BorderLayout.CENTER);
        this.myPicture = myPicture;

        GameClient gameClient = new GameClient();
        UUID gameId = gameClient.createGame();
        String wsPath = gameClient.joinGame(gameId);
        String wsUrl = "ws://game.local:7777" + wsPath;
        socketHandler = new SocketHandler(
                wsUrl,
                status -> this.status = status,
                snapshot -> {
                    boardView.onSnapshot(snapshot);
                    SwingUtilities.invokeLater(myPicture::repaint);
                }
        );
        critterManager = new ClientCritterManager(socketHandler); // Initialize manager
        socketHandler.connect();

        addTimer();
        constructSouth();
        // initially it has not started
        started = false;
    }

    public void add(int count, Class<? extends Critter> critterClass) {
        critterManager.add(count, critterClass);
    }

    // construct the controls and label for the southern panel
    private void constructSouth() {
        // add timer controls to the south
        JPanel p = new JPanel();

        final JSlider slider = new JSlider();
        slider.addChangeListener(e -> {
            double ratio = 1000.0 / (1 + Math.pow(slider.getValue(), 0.3));
            myTimer.setDelay((int) (ratio - 180));
        });
        slider.setValue(20);
        p.add(new JLabel("slow"));
        p.add(slider);
        p.add(new JLabel("fast"));

        JButton b1 = new JButton("start");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myTimer.start();
            }
        });
        p.add(b1);
        JButton b2 = new JButton("stop");
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myTimer.stop();
            }
        });
        p.add(b2);
        
        // add debug button
        JButton b4 = new JButton("debug");
        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // myModel.toggleDebug(); // Removed
                myPicture.repaint();
            }
        });
        p.add(b4);

        add(p, BorderLayout.SOUTH);
    }

    // starts the simulation...assumes all critters have already been added
    public void start() {
        // don't let anyone start a second time and remember if we have started
        if (started) {
            return;
        }
        started = true;
        pack();
        setVisible(true);
    }

    private void addTimer() {
        // remote view updates happen via socket, so timer just requests repaints
        ActionListener updater = e -> myPicture.repaint();
        myTimer = new javax.swing.Timer(250, updater);
        myTimer.setCoalesce(true);
    }
}

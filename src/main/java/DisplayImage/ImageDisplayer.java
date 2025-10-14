package DisplayImage;

import bmp.BitmapImage;
import bmp.BmpColor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ImageDisplayer {


    private static String TITLE = "Bitmap Image Viewer!";

    //The factor to scale down the window from the size of the screen
    private static final double WINDOW_SIZE_FACTOR = 0.7;

    private static final int MIN_ZOOM = 10;
    private static final int MAX_ZOOM = 1600;

    private static final int[] ZOOM_LEVELS = {100, 200, 300, 400, 600, 800, 1000, 1200, 1600, 2000, 2800, 4000};

    private int zoomIndex = 0;

    //The array containing all boards
    //Each thread owns a board slot in the array and can put itself into it when it has completed a generation
    private final BitmapImage img;

    private JFrame frame;
    private JPanel pixelGrid;
    private JLabel uiLabel;

    private String path;

    public ImageDisplayer(BitmapImage img, String path) {
        this.img = img;

        this.path = path;
    }

    public void run() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    initDisplay(); //Initialize the display
                    populate(); //Add pixels to the display
                    keyBinding();
                    frame.pack();
                    frame.setVisible(true);

//                    //Update UI every TIME_WAIT_MS milliseconds
//                    new Timer(TIME_WAIT_MS, e -> {
//                        updateDisplay();
//                    }).start();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void initDisplay() throws InterruptedException {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Rectangle screenBounds = frame.getGraphicsConfiguration().getBounds();
        Rectangle windowBounds = new Rectangle((int) (screenBounds.width*WINDOW_SIZE_FACTOR), (int) (screenBounds.height*WINDOW_SIZE_FACTOR));

        int xPixels = this.img.getWidth();
        int yPixels = this.img.getHeight();

        frame.setSize(windowBounds.width, (int) (windowBounds.height));

        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setPreferredSize(new Dimension(windowBounds.width, (int) (windowBounds.height)));
        verticalPanel.setBorder(new LineBorder(Color.BLACK, 2));


        this.uiLabel = new JLabel(this.path);
        this.uiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        //this.pixelGrid  = new JPanel(new GridBagLayout());
        this.pixelGrid  = new JPanel(new GridLayout(yPixels, xPixels));

        pixelGrid.setPreferredSize(new Dimension(xPixels, yPixels));
        //pixelGrid.setBorder(new LineBorder(Color.BLACK, 2));

        pixelGrid.setAlignmentX(Component.CENTER_ALIGNMENT);

        //This wrapper keeps the grid centered instead of stretching when the window is too large
        JPanel centeredWrapper = new JPanel(new GridBagLayout()); // centers contents
        centeredWrapper.add(pixelGrid);

        verticalPanel.add(uiLabel);
        verticalPanel.add(new JScrollPane(centeredWrapper));


        frame.add(verticalPanel);
    }

    public void keyBinding() {
        InputMap inputMap = pixelGrid.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = pixelGrid.getActionMap();

        //'Ctrl' + '+'
        KeyStroke keyStrokeZoomIn = KeyStroke.getKeyStroke("control EQUALS");

        //'Ctrl' + '-'
        KeyStroke keyStrokeZoomOut = KeyStroke.getKeyStroke("control MINUS");

        // Bind it to an action name
        inputMap.put(keyStrokeZoomIn, "zoomIn");
        inputMap.put(keyStrokeZoomOut, "zoomOut");

        actionMap.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("crtl+");
                zoomIn();
            }
        });

        actionMap.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("crtl-");
                zoomOut();
            }
        });
    }

    //Populate the pixelGrid with every pixel from the bitmap
    public void populate() {
        int pixelSize = Math.min(frame.getSize().width/this.img.getWidth(), frame.getSize().height/this.img.getHeight());

        Random r = new Random();

        //GridBagConstraints gbc = new GridBagConstraints();
        //gbc.fill = GridBagConstraints.BOTH; // Allow components to fill their display area
        //gbc.weightx = 1.0; // Distribute extra horizontal space
        //gbc.weighty = 1.0; // Distribute extra vertical space

        for(BmpColor bmpColor : img) {
            pixelGrid.add(new PixelComponent(pixelSize,bmpColor));
        }

//        for(int i = this.img.getHeight()-1; i >= 0; i++) {
//            for(int j = this.img.getWidth()-1; j >= 0; j++) {
//                //gbc.gridx = j;
//                //gbc.gridy = i;
//
//                BmpColor bmpColor = img.get(i*img.getWidth()+j);
//                //pixelGrid.add(new PixelComponent(pixelSize,new BmpColor(r.nextInt(255), r.nextInt(255), r.nextInt(255))));
//                pixelGrid.add(new PixelComponent(pixelSize,bmpColor));
//            }
//        }
    }

    public void zoomIn() {
        if(this.zoomIndex == ZOOM_LEVELS.length-1) {
            return;
        }
        zoomIndex++;
        pixelGrid.setPreferredSize(new Dimension(
                (int) (img.getWidth()*((double)ZOOM_LEVELS[zoomIndex]/100)),
                (int) (img.getWidth()*((double)ZOOM_LEVELS[zoomIndex]/100)))
        );
        pixelGrid.revalidate();
        pixelGrid.repaint();

    }

    public void zoomOut() {
        if(this.zoomIndex == 0) {
            return;
        }
        zoomIndex--;
        pixelGrid.setPreferredSize(new Dimension(
                (int) (img.getWidth()*((double)ZOOM_LEVELS[zoomIndex]/100)),
                (int) (img.getWidth()*((double)ZOOM_LEVELS[zoomIndex]/100)))
        );
        pixelGrid.repaint();
        pixelGrid.revalidate();

    }

}

package DisplayImage;

import bmp.BitmapImage;
import bmp.BmpColor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageDisplayer {
    //The factor to scale down the window from the size of the screen
    private static final double WINDOW_SIZE_FACTOR = 0.7;

    private static final int STARTING_ZOOM_IDX = 4;
    private static final int[] ZOOM_LEVELS = {10, 25, 40, 60, 100, 200, 300, 400, 600, 800, 1000, 1200, 1600, 2000, 2800, 4000};

    private static final int MENU_BAR_HEIGHT = 40;

    private int zoomIndex = STARTING_ZOOM_IDX;

    private BitmapImage img;
    private boolean imageLoaded;

    private final ActionMap actionMap;

    private JFrame frame;
    private JPanel verticalPanel; //The outer panel which contains the UI

    private JMenuBar menuBar;
    private JLabel fNameLabel;  //The image file path label
    //private JPanel pixelGrid; //The image display area
    private BufferedImage bufferedImage;
    private JLabel displayedImageLabel;

    private String path;

    public ImageDisplayer(String path) throws IOException {
        //Create bitmap
        this.img = loadImageData(path);
        imageLoaded = true;

        this.path = path;
        this.actionMap = fillActionMap();
    }

    public BitmapImage loadImageData(String path) throws IOException {
        //Read file
        File file = new File(path);
        byte[] fileContent = Files.readAllBytes(file.toPath());

        //Create bitmap
        return new BitmapImage(fileContent);
    }

    public void run() {

        SwingUtilities.invokeLater(() -> {
            try {
                initDisplay(); //Initialize the display
                displayImage();

                keyBinding(); //set key bindings
                frame.pack();
                frame.setVisible(true);

//                    //Update UI every TIME_WAIT_MS milliseconds
//                    new Timer(TIME_WAIT_MS, e -> {
//                        updateDisplay();
//                    }).start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    //Updates the UI to display the image currently loaded in 'img'
    private void displayImage() {
        populate(); //Add pixels to the display
        applyZoom();
        fNameLabel.setText(this.path); //set text for display
    }

    private void initDisplay() throws InterruptedException {
        //Create the frame which will contain the program
        String TITLE = "Bitmap Image Viewer!";
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Rectangle screenBounds = frame.getGraphicsConfiguration().getBounds();
        Rectangle windowBounds = new Rectangle((int) (screenBounds.width*WINDOW_SIZE_FACTOR), (int) (screenBounds.height*WINDOW_SIZE_FACTOR));

        int xPixels = this.img.getWidth();
        int yPixels = this.img.getHeight();

        frame.setSize(windowBounds.width, (windowBounds.height));

        //Create a panel to reside within the frame which will hold all UI elements
        this.verticalPanel = new JPanel();
        verticalPanel.setLayout(new BorderLayout());
        verticalPanel.setPreferredSize(new Dimension(windowBounds.width, (windowBounds.height)));
        verticalPanel.setBorder(new LineBorder(Color.BLACK, 2));

        //Create UI elements to populate verticalPanel

        //Menu bar
        this.menuBar = initMenuBar();

        //label to show the name of the current file
        this.fNameLabel = initFNameLabel(""); //empty

        //grid to display the pixels
        this.bufferedImage = initBufferedImage(xPixels, yPixels);
        this.displayedImageLabel = new JLabel(new ImageIcon(bufferedImage));

        //This wrapper keeps the grid centered instead of stretching when the window is too large
        JPanel centeredWrapper = new JPanel(new GridBagLayout()); //centers contents
        centeredWrapper.add(fNameLabel);
        centeredWrapper.add(displayedImageLabel);

        verticalPanel.add(menuBar, BorderLayout.NORTH);

        JPanel pixelGridBox = new JPanel(); //vertical stack of pixelGrid + fileNameLabel
        pixelGridBox.setLayout(new BoxLayout(pixelGridBox, BoxLayout.Y_AXIS));
        pixelGridBox.add(fNameLabel);
        pixelGridBox.add(new JScrollPane(centeredWrapper));

        verticalPanel.add(pixelGridBox, BorderLayout.CENTER);


        frame.add(verticalPanel);
    }

    //Make menuBar
    private JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuBar.add(initFileMenu());
        menuBar.add(initViewMenu());

        return menuBar;
    }

    //Make File Menu
    private JMenu initFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F); //alt+F

        JMenuItem itemOpen = new JMenuItem("Open", KeyEvent.VK_O); //alt+O
        itemOpen.setAction(this.actionMap.get("Open"));

        fileMenu.add(itemOpen);

        return fileMenu;
    }

    //Make File Menu
    private JMenu initViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V); //alt+V

        JMenuItem itemZoomIn = new JMenuItem("zoomIn");
        itemZoomIn.setAction(this.actionMap.get("zoomIn"));

        JMenuItem itemZoomOut = new JMenuItem("zoomOut");
        itemZoomOut.setAction(this.actionMap.get("zoomOut"));

        viewMenu.add(itemZoomIn);
        viewMenu.add(itemZoomOut);

        return viewMenu;
    }

    //Make label to contain current file name centered
    private JLabel initFNameLabel(String fname) {
        JLabel fnameLabel = new JLabel(fname);
        fnameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return fnameLabel;
    }

    //Make panel to contain the pixel grid
    private JPanel initPixelGrid(int xPixels, int yPixels) {
        JPanel pixelGrid  = new JPanel(new GridLayout(yPixels, xPixels));

        pixelGrid.setPreferredSize(new Dimension(xPixels, yPixels));

        pixelGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        return pixelGrid;
    }

    private BufferedImage initBufferedImage(int xPixels, int yPixels) {
        return new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);
    }

//    //Clears the pixel grid and changes the size
//    private void resetPixelGrid(int xPixels, int yPixels) {
//        this.pixelGrid.removeAll();
//        ((GridLayout)this.pixelGrid.getLayout()).setColumns(xPixels);
//        ((GridLayout)this.pixelGrid.getLayout()).setRows(yPixels);
//
//        //Reset zoom
//        //clearZoom();
//
//        //Keep current zoom
//        pixelGrid.setPreferredSize(new Dimension(
//                (int) (xPixels*((double)ZOOM_LEVELS[zoomIndex]/100)),
//                (int) (yPixels*((double)ZOOM_LEVELS[zoomIndex]/100)))
//        );
//
//        pixelGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
//    }
    private void resetDisplayedImage(int newXPixels, int newYPixels) {
        this.bufferedImage = new BufferedImage(newXPixels, newYPixels, BufferedImage.TYPE_INT_ARGB);
        this.displayedImageLabel.removeAll();
        //this.displayedImageLabel.setIcon(new ImageIcon(this.bufferedImage));
    }

    public ActionMap fillActionMap() {
        ActionMap actionMap = new ActionMap();

        actionMap.put("Open", new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open");
                chooseNewImage();
            }
        });

        actionMap.put("zoomIn", new AbstractAction("Zoom (+)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("crtl+");
                zoomIn();
            }
        });

        actionMap.put("zoomOut", new AbstractAction("Zoom (-)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("crtl-");
                zoomOut();
            }
        });

        return actionMap;
    }

    public void keyBinding() {
        InputMap inputMap = verticalPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        //ActionMap actionMap = verticalPanel.getActionMap();
        verticalPanel.setActionMap(this.actionMap);

        //'Ctrl' + '+'
        KeyStroke keyStrokeZoomIn = KeyStroke.getKeyStroke("control EQUALS");

        //'Ctrl' + '-'
        KeyStroke keyStrokeZoomOut = KeyStroke.getKeyStroke("control MINUS");

        //Bind it to an action name
        inputMap.put(keyStrokeZoomIn, "zoomIn");
        inputMap.put(keyStrokeZoomOut, "zoomOut");

        //actionMap.put("zoomIn", (AbstractAction) actionMap.get("zoomIn"));
//        actionMap.put("zoomIn",  new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("crtl+");
//                zoomIn();
//            }
//        });


    }

    //Populate the pixelGrid with every pixel from the bitmap
    public void populate() {
//        int pixelSize = Math.min(frame.getSize().width/this.img.getWidth(), frame.getSize().height/this.img.getHeight());
//
//        for(BmpColor bmpColor : img) {
//            pixelGrid.add(new PixelComponent(pixelSize,bmpColor));
//        }
        //Get the raster, populate it one pixel at a time
        WritableRaster raster = this.bufferedImage.getRaster();
        int x = 0;
        int y = 0;
        for(BmpColor bmpColor : img) {
            raster.setPixel(x, y, bmpColor.RGBArray());
            x++;
            if(x >= img.getWidth()) {
                x = 0;
                y++;
            }
        }
    }

    public void chooseNewImage() {
        JFileChooser fileChooser = new JFileChooser();

        //Only files can be selected
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        //The files must be BMP files
        FileFilter bmpFilter = new FileNameExtensionFilter("BMP file", "bmp");
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.setFileFilter(bmpFilter);

        //set directory to same directory as current file
        fileChooser.setCurrentDirectory(new File(path));

        //open dialog
        int result = fileChooser.showOpenDialog(ImageDisplayer.this.frame);

        //Process the result
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Open: " + selectedFile.getName());

            //Do not verify the selected file here as it is guaranteed to be a bmp file

            //Load the bmp and display it
            try {
                ImageDisplayer.this.path = selectedFile.getPath();
                ImageDisplayer.this.img = loadImageData(path);
                //resetPixelGrid(ImageDisplayer.this.img.getWidth(), ImageDisplayer.this.img.getHeight());
                resetDisplayedImage(ImageDisplayer.this.img.getWidth(), ImageDisplayer.this.img.getHeight());
                displayImage();
                frame.pack();
                frame.repaint();
                frame.revalidate();
            } catch (IOException ex) {
                System.err.println(selectedFile.toString() + " was not a valid bmp file.");
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("File Open Cancelled");
        } else {
            System.err.println("File Open Error!");
        }
    }

    public void zoomIn() {
        if(this.zoomIndex == ZOOM_LEVELS.length-1) {
            return;
        }
        zoomIndex++;
        applyZoom();
        displayedImageLabel.revalidate();
        displayedImageLabel.repaint();
    }

    public void zoomOut() {
        if(this.zoomIndex == 0) {
            return;
        }
        zoomIndex--;
        applyZoom();
        displayedImageLabel.revalidate();
        displayedImageLabel.repaint();
    }

    public synchronized void applyZoom() {
        displayedImageLabel.setIcon(
                new ImageIcon((new ImageIcon(bufferedImage)).getImage().getScaledInstance(
                        (int) (img.getWidth()*((double)ZOOM_LEVELS[zoomIndex]/100)),
                        (int) (img.getHeight()*((double)ZOOM_LEVELS[zoomIndex]/100)),
                        Image.SCALE_FAST)));
    }

    public void clearZoom() {
        zoomIndex = STARTING_ZOOM_IDX;
    }
}

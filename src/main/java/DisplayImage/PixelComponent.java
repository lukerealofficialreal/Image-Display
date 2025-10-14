package DisplayImage;

import bmp.BmpColor;
import bmp.BmpTest;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class PixelComponent extends JPanel {

    private int size;
    private Color color;

    public PixelComponent(int size, BmpColor c) {
        this.size = size;
        this.color = new Color(c.red(), c.green(), c.blue());
    }

    public PixelComponent(boolean isDoubleBuffered, int size,BmpColor c) {
        super(isDoubleBuffered);
        this.size = size;
        this.color = new Color(c.red(), c.green(), c.blue());
    }

    public PixelComponent(LayoutManager layout,  int size, BmpColor c) {
        super(layout);

        this.size = size;
        this.color = new Color(c.red(), c.green(), c.blue());
    }

    public PixelComponent(LayoutManager layout, boolean isDoubleBuffered, int size, BmpColor c) {
        super(layout, isDoubleBuffered);
        this.size = size;
        this.color = new Color(c.red(), c.green(), c.blue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        //Get the width and height based on the type of the station
        //int width = stationType.getXlen()*size;
        //int height = stationType.getYlen()*size;

        g.setColor(this.color);
        g.fillRect(0, 0, getWidth(), getHeight());

        //g.setFont(new Font("Arial", Font.PLAIN, (int) (size*(0.7))));

        //Calculate the positions of the text inside the station
        //int[] textPositions = calcFontPosition(g, getWidth(),
        //        getHeight(), Integer.toString(id));

        //g.setColor(Color.black);
        //g.drawString(stationType.name(), textPositions[0], textPositions[1]);
        //g.drawString(Integer.toString(id), textPositions[0], textPositions[1]);
        //g.drawString(Integer.toString(id), textPositions[2], textPositions[3]);
    }

    //Takes the graphics object, the width and height of the box, and a set of strings,
    //and returns an array containing the x and y positions of each given string within the box
    //such that the strings are centered horizontally and stacked vertically
    public int[] calcFontPosition(Graphics g, int width, int height, String ...str) {
        int numStrings = str.length;

        FontMetrics fm = g.getFontMetrics(); //get the font metrics of g

        int[] xyPairs = new int[numStrings*2];

//        int offsetLoc = numStrings/2;
//        for(int i = 0; i<numStrings; i++) {
//            int stringWidth = fm.stringWidth(str[i]);
//            int x = (width - stringWidth) / 2;
//
//            int textHeight = fm.getHeight();
//            int y = ((height - textHeight) / 2) + fm.getAscent();
//            y += (int) (offsetLoc*(y*1.1));
//            offsetLoc--;
//        }

        AtomicInteger index = new AtomicInteger(0);
        //return (width - fm.stringWidth(string))/2;})
        //return (y)+(fm.getHeight()*((str.length/2)-index.getAndIncrement()));

        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();

        Function<String, Integer> getX = (String string) -> (width - fm.stringWidth(string))/2;
        Function<String, Integer> getY = (String string) -> ((y)+(fm.getHeight()*((str.length/2)-index.getAndIncrement())));

        return Stream.of(str).flatMap(string -> Stream.of(getX.apply(string), getY.apply(string)))
                .mapToInt(Integer::intValue).toArray();


    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(size, size);
//    }
}

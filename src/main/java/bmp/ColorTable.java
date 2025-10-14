package bmp;

import Utilities.DataInterpretation;

public class ColorTable extends DataInterpretation {
    protected final byte[] colors;

    public ColorTable(byte[] colorData) {
        colors = colorData;
    }

    public int size() {
        return colors.length/4;
    }

    //TODO: Palette is completely wrong. The indexes seem maybe correct.
    public BmpColor getColor(int index) {
        int realIndex = index*4;
        BmpColor color = new BmpColor((int)colors[realIndex+2]<0 ? (int)colors[realIndex+2]+256 : (int)colors[realIndex+2],
                (int)colors[realIndex+1]<0 ? (int)colors[realIndex+1]+256 : (int)colors[realIndex+1],
                (int)colors[realIndex]<0 ? (int)colors[realIndex]+256 : (int)colors[realIndex]);

        return color;
    }

    //Find the index of a given color. Returns -1 if not found
    public int indexOf(BmpColor c) {
        for(int i = 0; i<colors.length; i+=4) {
            if(c.red() == colors[i] && c.green() == colors[i+1] && c.blue() == colors[i+2]) {
                return i/4;
            }
        }
        return -1;
    }

    //Check if the color is in the color table
    public boolean contains(BmpColor c) {
        return indexOf(c) != -1;
    }

}

package bmp;

import Utilities.DataInterpretation;

public class ImageData {
    private static final int BYTE_BITS = 8;

    //Rows are stored upside down, such that rawData[0][0] would be the bottom left of the image
    private final byte[][] rawData;

    public ImageData(byte[] rawData, int numLines, int lineLenNoPad, int linePaddingBytes) {
        this.rawData = new byte[numLines][lineLenNoPad];
        for(int i = 0; i<numLines; i++) {
            System.arraycopy(rawData,(i*(lineLenNoPad+linePaddingBytes)),this.rawData[i],0,lineLenNoPad);
        }
    }


    //Returns either an array containing values for Blue Green and Red,
    //Or an array with a single value which represents an index into the color table
    public int[] getValue(int pixelNum, int row, int bitsPerPixel) {
        //non palettized
        if(bitsPerPixel>8) {
            int startingByte = pixelNum*(bitsPerPixel/BYTE_BITS);
            //int numBytes = bitsPerPixel/BYTE_BITS;
            int bitsPerColor = bitsPerPixel/3;
            //int startingBit = bitsPerPixel%3;
            int startingBit = 0;
            return new int[] {DataInterpretation.bitsFromBytesToInt(this.rawData[row],startingByte,startingBit,bitsPerColor),
                    DataInterpretation.bitsFromBytesToInt(this.rawData[row],startingByte+(bitsPerColor/8),startingBit + (bitsPerColor%8),bitsPerColor),
                    DataInterpretation.bitsFromBytesToInt(this.rawData[row],startingByte+((bitsPerColor*2)/8),startingBit + ((bitsPerColor*2)%8),bitsPerColor)};
        //palettized
        } else {
            int startingByte = pixelNum/(BYTE_BITS/bitsPerPixel);
            int startingBit = pixelNum%(BYTE_BITS/bitsPerPixel)*bitsPerPixel;

            return new int[] {DataInterpretation.bitsFromBytesToInt(this.rawData[row],startingByte,startingBit,bitsPerPixel)};

        }

    }
}

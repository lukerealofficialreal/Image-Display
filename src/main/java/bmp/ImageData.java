package bmp;

import Utilities.DataInterpretation;

public class ImageData {
    private static final int BYTE_BITS = 8;

    //Rows are stored upside down, such that rawData[0][0] would be the bottom left of the image
    private final byte[][] imageData2D;

    public ImageData(byte[] rawData, int numLines, int lineLenNoPad, int linePaddingBytes) {
        this.imageData2D = new byte[numLines][lineLenNoPad];

        for(int i = 0; i<numLines; i++) {
            System.arraycopy(rawData,(i*(lineLenNoPad+linePaddingBytes)),this.imageData2D[i],0,lineLenNoPad);
        }
    }


    //Returns either an array containing values for Blue Green and Red,
    //Or an array with a single value which represents an index into the color table
    public int[] getValue(int pixelNum, int row, int bitsPerPixel, boolean hasAlpha){
        //non palettized
        if(bitsPerPixel>8) {
            if(hasAlpha){
                int startingByte = pixelNum * (bitsPerPixel / BYTE_BITS);
                int bitsPerColor = bitsPerPixel / 4;
                int startingBit = 0;
                return new int[]{DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte, startingBit, bitsPerColor),
                        DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte + (bitsPerColor / 8), startingBit + (bitsPerColor % 8), bitsPerColor),
                        DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte + ((bitsPerColor * 2) / 8), startingBit + ((bitsPerColor * 2) % 8), bitsPerColor),
                        DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte + ((bitsPerColor * 3) / 8), startingBit + ((bitsPerColor * 3) % 8), bitsPerColor)};
            } else {
                int startingByte = pixelNum * (bitsPerPixel / BYTE_BITS);
                int bitsPerColor = bitsPerPixel / 3;
                int startingBit = 0;
                return new int[]{DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte, startingBit, bitsPerColor),
                        DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte + (bitsPerColor / 8), startingBit + (bitsPerColor % 8), bitsPerColor),
                        DataInterpretation.bitsFromBytesToInt(this.imageData2D[row], startingByte + ((bitsPerColor * 2) / 8), startingBit + ((bitsPerColor * 2) % 8), bitsPerColor)};
            }
        //palettized
        } else {
            int startingByte = pixelNum/(BYTE_BITS/bitsPerPixel);
            int startingBit = pixelNum%(BYTE_BITS/bitsPerPixel)*bitsPerPixel;

            return new int[] {DataInterpretation.bitsFromBytesToInt(this.imageData2D[row],startingByte,startingBit,bitsPerPixel)};
        }

    }
}

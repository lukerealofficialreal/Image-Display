package bmp;

import java.util.Iterator;

//TODO: Figure out why 24bit colors are wrong in test5
public class BitmapImage implements Iterable<BmpColor> {
    private static final int HEADER_NUM_BYTES = 14;
    private static final int INFO_HEADER_NUM_BYTES = 40;

    private static final int BYTE_BITS = 8;

    protected Header header;
    protected InfoHeader infoHeader;
    protected ColorTable colorTable;
    protected ImageData img;

    public BitmapImage(byte[] rawData) {
        //must construct header and infoHeader first as numColors and imageSize members of infoHeader are necessary to
        //parse the data for colorTable and imageData
        int rawDataPos = 0;
        byte[] headerData = new byte[HEADER_NUM_BYTES];
        System.arraycopy(rawData, rawDataPos, headerData,0,HEADER_NUM_BYTES);
        this.header = new Header(headerData);
        rawDataPos+=HEADER_NUM_BYTES;

        byte[] infoHeaderData = new byte[INFO_HEADER_NUM_BYTES];
        System.arraycopy(rawData, rawDataPos, infoHeaderData,0,INFO_HEADER_NUM_BYTES);
        this.infoHeader = new InfoHeader(infoHeaderData);
        rawDataPos+=INFO_HEADER_NUM_BYTES;

        //the color table only exists if there is less than or equal to 8 bits per pixel in the image
        if(infoHeader.bitsPerPixel.getBits() > 8) {
            this.colorTable = null; //if bits per pixel > 8, then there is no color table. Colors are stored in the bitmap
        } else {
            //get the number of colors in the color table
            int colorTableNumBytes = infoHeader.getNumColors() * 4;
            byte[] colorTableData = new byte[colorTableNumBytes];
            System.arraycopy(rawData, rawDataPos, colorTableData,0, colorTableNumBytes);
            this.colorTable = new ColorTable(colorTableData);
            rawDataPos+= colorTableNumBytes;
        }

        int imageDataNumBytes = (int) (getWidth() * getHeight() * ((double) infoHeader.bitsPerPixel.getBits() / BYTE_BITS));
        byte[] imageData = new byte[imageDataNumBytes];
        System.arraycopy(rawData, rawDataPos, imageData,0, imageDataNumBytes);

        //int padding = this.numPixels%4;
        //padding = (padding==0) ? (0) : (4-padding);
        this.img = new ImageData(imageData, infoHeader.getHeight(), (int) (getWidth()*((double)infoHeader.bitsPerPixel.getBits()/BYTE_BITS)));//, padding);

        //rawDataPos+=imageDataNumBytes;
    }

    public int getWidth() {
        return infoHeader.getWidth();
    }

    public int getHeight() {
        return infoHeader.getHeight();
    }

    public BmpColor get(int col, int row) {
        //Different approach to getting the color depending on the bit depth
        //If color table is null, colors are directly in the imageData.
        //Else, the image data contains indexes into the color table

        return switch (infoHeader.bitsPerPixel) {
            case MONOCHROME -> getMonochromeColor(col, row);
            case PALLETIZED_4_BIT -> switch(infoHeader.compression) {
                case BI_RGB -> getPalettizedColor(col, row, 4);
                case BI_RLE8 -> throw new RuntimeException("BI_RLE8 not supported");
                case BI_RLE4 -> throw new RuntimeException("BI_RLE4 not yet supported");
                case BI_BITFIELDS -> throw new RuntimeException("BI_BITFIELDS not supported");
                case BI_ALPHABITFIELDS -> throw new RuntimeException("BI_ALPHABITFIELDS not supported");
            };
            case PALLETIZED_8_BIT -> switch(infoHeader.compression) {
                case BI_RGB -> getPalettizedColor(col, row, 8);
                case BI_RLE8 -> throw new RuntimeException("BI_RLE8 not yet supported");
                case BI_RLE4 -> throw new RuntimeException("BI_RLE4 not supported");
                case BI_BITFIELDS -> throw new RuntimeException("BI_BITFIELDS not supported");
                case BI_ALPHABITFIELDS -> throw new RuntimeException("BI_ALPHABITFIELDS not supported");
            };
            case RGB_16_BIT -> getDirectColor(col, row, 16);
            case RGB_24_BIT -> getDirectColor(col, row, 24);
        };
    }

    public BmpColor getMonochromeColor(int col, int row) {
        return (this.img.getValue(col, row, 1)[0] == 1) ? (new BmpColor(255, 255, 255)) : (new BmpColor(0,0,0));
    }

    //Depth must be less than 8
    public BmpColor getPalettizedColor(int col, int row, int depth) {
        return this.colorTable.getColor(this.img.getValue(col, row, depth)[0]);
    }

    public BmpColor getDirectColor(int col, int row, int depth) {
        int[] colorsBGR = this.img.getValue(col, row, depth);
        return new BmpColor(colorsBGR[2], colorsBGR[1], colorsBGR[0]);
    }

    @Override
    public Iterator<BmpColor> iterator() {
        return new Iterator<>() {
            private int row = getHeight()-1;
            private int col = 0;

            @Override
            public boolean hasNext() {
                return row>=0;
            }

            @Override
            public BmpColor next() {
                BmpColor color = get(col, row);
                col++;
                if(col == getWidth()) {
                    col = 0;
                    row--;
                }
                return color;
            }
        };
    }


}

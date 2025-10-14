package bmp;

import Utilities.DataInterpretation;
import Utilities.EnumValueMap;
import interfaces.RepresentativeValue;

public class InfoHeader extends DataInterpretation {
    private static final int SIZE_NUM_BYTES = Integer.BYTES;
    private static final int INFO_HEADER_SIZE = 40; //size expected for info header
    protected int size; //the size of the infoHeader;

    private static final int WIDTH_NUM_BYTES = Integer.BYTES;
    protected int width; //the width of the image (pixels)

    private static final int HEIGHT_NUM_BYTES = Integer.BYTES;
    protected int height; //the height of the image (pixels)


    private static final int PLANES_NUM_BYTES = Short.BYTES;
    private static final int NUM_PLANES = 1; //number of planes expected
    protected short planes;

    public enum BitsPerPixelEnum implements RepresentativeValue<Short> {
        MONOCHROME((short)1,1),
        PALLETIZED_4_BIT((short)4,16),
        PALLETIZED_8_BIT((short)8,256),
        RGB_16_BIT((short)16,65536),
        RGB_24_BIT((short)24,16777216);

        private final short bits;
        private final int numColors;

        BitsPerPixelEnum(short bits, int numColors) {
            this.bits = bits;
            this.numColors = numColors;
        }

        public Short getBits() {
            return bits;
        }
        public int getNumColors() {
            return numColors;
        }

        @Override
        public Short getRepVal() {
            return getBits();
        }

    }

    private static final int BITS_PER_PIXEL_NUM_BYTES = Short.BYTES;
    protected BitsPerPixelEnum bitsPerPixel;

    public enum CompressionEnum implements RepresentativeValue<Integer> {
        BI_RGB(0), //No compression
        BI_RLE8(1), //8bit RLE encoding
        BI_RLE4(2), //4bit RLE encoding
        BI_BITFIELDS(3), //NOT SUPPORTED
        BI_ALPHABITFIELDS(4); //NOT SUPPORTED
        private final int repVal;

        CompressionEnum(int repVal) {
            this.repVal = repVal;
        }

        @Override
        public Integer getRepVal() {
            return repVal;
        }

    }
    private static final int COMPRESSION_NUM_BYTES = Integer.BYTES;
    protected CompressionEnum compression; //The type of compression used (or no compression)

    private static final int COMPR_IMG_SIZE_NUM_BYTES = Integer.BYTES;
    protected int compressedImageSize; //The size in bytes of the compressed image. Not strictly defined if the image is
                                        //not compressed (either 0 or the total image size in this case)

    private static final int X_PIXEL_M_NUM_BYTES = Integer.BYTES;
    protected int xPixelsPerMeter; //horizontal Pixels per meter (for cameras)

    private static final int Y_PIXEL_M_NUM_BYTES = Integer.BYTES;
    protected int yPixelsPerMeter; //vertical Pixels per meter (for cameras)

    private static final int COLORS_USED_NUM_BYTES = Integer.BYTES;
    protected int colorsUsed; //Colors used in the image

    private static final int IMPORTANT_COLOR_NUM_BYTES = Integer.BYTES;
    protected int importantColors; //Number of important colors (0 if all are important)

    public InfoHeader(byte[] rawInfoHeaderData) {
        int rawDataPos = 0;
        this.size = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=SIZE_NUM_BYTES;

        this.width = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=WIDTH_NUM_BYTES;

        this.height = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=HEIGHT_NUM_BYTES;

        this.planes = getShortFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=PLANES_NUM_BYTES;

        {
            EnumValueMap<Short, BitsPerPixelEnum> bitsPerPixelValueMap = new EnumValueMap<>(BitsPerPixelEnum.class);
            this.bitsPerPixel = bitsPerPixelValueMap.get(getShortFromData(rawInfoHeaderData, rawDataPos));
        }
        rawDataPos+=BITS_PER_PIXEL_NUM_BYTES;

        {
            EnumValueMap<Integer, CompressionEnum> compressionValueMap = new EnumValueMap<>(CompressionEnum.class);
            this.compression = compressionValueMap.get(getIntFromData(rawInfoHeaderData, rawDataPos));
        }
        rawDataPos+=COMPRESSION_NUM_BYTES;

        this.compressedImageSize = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=COMPR_IMG_SIZE_NUM_BYTES;

        this.xPixelsPerMeter = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=X_PIXEL_M_NUM_BYTES;

        this.yPixelsPerMeter = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=Y_PIXEL_M_NUM_BYTES;

        this.colorsUsed = getIntFromData(rawInfoHeaderData, rawDataPos);
        rawDataPos+=COLORS_USED_NUM_BYTES;

        this.importantColors = getIntFromData(rawInfoHeaderData, rawDataPos);
        //rawDataPos+=IMPORTANT_COLOR_NUM_BYTES;
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public short getPlanes() {
        return planes;
    }

    public BitsPerPixelEnum getBitsPerPixel() {
        return bitsPerPixel;
    }

    public CompressionEnum getCompression() {
        return compression;
    }

    public int getCompressedImageSize() {
        return compressedImageSize;
    }

    public int getxPixelsPerMeter() {
        return xPixelsPerMeter;
    }

    public int getyPixelsPerMeter() {
        return yPixelsPerMeter;
    }

    public int getColorsUsed() {
        return colorsUsed;
    }

    public int getImportantColors() {
        return importantColors;
    }

    public int getNumColors() {
        return colorsUsed;//bitsPerPixel.getNumColors();
    }

}

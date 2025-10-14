package bmp;

import Utilities.DataInterpretation;
import exceptions.InvalidSignatureException;

public class Header extends DataInterpretation {
    private static final String BMP_SIGNATURE = "BM"; //signature expected for BMP files
    private static final int SIGNATURE_NUM_BYTES = 2;
    protected String signature; //must always be "BM" if the file is a valid BMP

    private static final int FILE_SIZE_NUM_BYTES = Integer.BYTES;
    protected int fileSize; //The file size in bytes

    private static final int RESERVED_SIZE = Integer.BYTES; //4 bytes are reserved and contain no important data

    private static final int DATA_OFFSET_NUM_BYTES = Integer.BYTES;
    protected int dataOffset; //Offset from the beginning of the file to the image data

    public Header(byte[] rawHeaderData) {
        int rawDataPos = 0;
        this.signature = new String(rawHeaderData, rawDataPos, SIGNATURE_NUM_BYTES-rawDataPos);
        if(!signature.equals(BMP_SIGNATURE)) {
            throw new InvalidSignatureException(signature);
        }
        rawDataPos+=SIGNATURE_NUM_BYTES;

        this.fileSize = getIntFromData(rawHeaderData, rawDataPos);
        rawDataPos+=FILE_SIZE_NUM_BYTES;

        //Reserved data (unused)
        rawDataPos+=RESERVED_SIZE;

        this.dataOffset = getIntFromData(rawHeaderData, rawDataPos);
        //rawDataPos+=DATA_OFFSET_NUM_BYTES;
    }

}

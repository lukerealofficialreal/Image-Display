package Utilities;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class DataInterpretation {
    private static final int BYTE_BITS = 8;
    private static final int INT_BITS = 32;
//    //Returns a UTF-16 string representation of a null-terminated ascii string found in the data after the starting location
//    //Returns null if none is found
//    //pos is updated to the character after the null terminator at the end of the string
//    protected static String getStringFromData(byte[] rawData, int start /*inclusive*/, int end /*exclusive*/) {
//        assert end<=rawData.length;
//        for(int i = start; i < end; i++)
//        {
//            //if(rawData[end] == '\0') {break;}
//        }
//        pos.set(end + 1); //Set position to 1 after the null terminator
//        return new String(rawData, start, end-start);
//    }

    //gets the next 2 bytes from a byte array,
    //converts two bytes into a single short
    public static short getShortFromData(byte[] raw,  int start /*inclusive*/) {
        //Copy target data into new array
        //convert to a stream of integers
        //add 256 to all negative values
        //left shift each value into it's correct position in the final value
        //or them all together
        byte[] bytes = new byte[Short.BYTES];
        System.arraycopy(raw, start, bytes, 0, Short.BYTES);

        AtomicInteger index = new AtomicInteger(0);
        return (short) IntStream.range(0, bytes.length)
                .map(i -> bytes[i])
                .map(o -> (o<0) ? o+256 : o)
                .map(o -> o << (index.getAndIncrement()*8))
                .reduce(0, (result, o) -> result|o);
    }

    //gets the next 4 bytes from a byte array,
    //converts 4 bytes into a single integer
    public static int getIntFromData(byte[] raw,  int start /*inclusive*/) {
        //Copy target data into new array
        //convert to a stream of integers
        //add 256 to all negative values
        //left shift each value into it's correct position in the final value
        //or them all together
        byte[] bytes = new byte[Integer.BYTES];
        System.arraycopy(raw, start, bytes, 0, Integer.BYTES);

        AtomicInteger index = new AtomicInteger(0);
        return IntStream.range(0, bytes.length)
                .map(i -> bytes[i])
                .map(o -> (o<0) ? o+256 : o)
                .map(o -> o << (index.getAndIncrement()*8))
                .reduce(0, (result, o) -> result|o);
    }
    //gets the next numBytes bytes from a byte array,
    //converts them into a single integer
    //numBytes must be less than 4
    public static int getIntFromData(byte[] raw,  int start /*inclusive*/, int numBytes) {
        //Copy target data into new array
        //convert to a stream of integers
        //add 256 to all negative values
        //left shift each value into it's correct position in the final value
        //or them all together
        byte[] bytes = new byte[Integer.BYTES];
        System.arraycopy(raw, start, bytes, 0, numBytes);

        AtomicInteger index = new AtomicInteger(Integer.BYTES);
        return IntStream.range(0, bytes.length)
                .map(i -> bytes[i])
                .map(o -> (o<0) ? o+256 : o)
                .map(o -> o << (index.decrementAndGet()*8))
                .reduce(0, (result, o) -> result|o);
    }


    //Takes an array of raw data, and converts the bits from an offset in the data into an integer of the
    //specified numBits. numBits cannot be longer than the raw data or longer than 32
    public static int bitsFromBytesToInt(byte[] raw, int startingByte, int startingBit, int numBits) {
        //Convert all the bytes which contain the desired bits from the image into an integer.
        //Crop the integer to only the desired bytes of bytes with shifts and a mask
        int dataAsInteger = getIntFromData(raw, startingByte, numBits/BYTE_BITS);
        dataAsInteger <<= startingBit;
        return dataAsInteger>>>(INT_BITS-numBits);

//        int result = 0;
//        int currByte = startingByte;
//        int currBit = startingBit;
//        for(int i = 0; i<numBits; i++) {
//            int currByteVal = (raw[currByte]<0) ? (raw[currByte]+256) : (raw[currByte]);
//
//
//            currBit++;
//            if(currBit == 8) {
//                currByte++;
//                currBit = 0;
//            }
//        }
    }

    public static int bitSetToInt(BitSet bitSet) {
        return getIntFromData(bitSet.toByteArray(), 0);
    }



    //Takes an array, returns the first index after start at which the given targt exists.
    //Returns -1 if the target is not in arr after the start index
//    public static int findSubArrayIndex(byte[] arr, byte[] target, int start /*inclusive*/) {
//        for (int i = 0; i < arr.length; i++) {
//            if (arr[i] == target) {
//                return i; // Return the index if found
//            }
//        }
//        return -1; // Return -1 if the element is not found
//    }

}

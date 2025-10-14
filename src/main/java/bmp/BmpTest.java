package bmp;

import DisplayImage.ImageDisplayer;
import Utilities.ArgumentMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BmpTest {

    public static String imagePath;

    public static String[] supportedArgs = new String[] {"-p"};
    public static Object[] defaultValues = new Object[] {"bmps/test2.bmp"};
    public static ArgumentMap argumentMap;
    public static ImageDisplayer imageDisplayer;


    public static void main(String[] args) throws IOException {
        //Initialize arguments
        argumentMap = new ArgumentMap(args, supportedArgs, defaultValues);
        imagePath = (String) argumentMap.getVal("-p");

        //Read file
        File file = new File(imagePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());

        //Create bitmap
        BitmapImage bitmapImage = new BitmapImage(fileContent);

        imageDisplayer = new ImageDisplayer(bitmapImage, imagePath);
        imageDisplayer.run();

    }
}

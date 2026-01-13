package bmp;

import DisplayImage.ImageDisplayer;
import Utilities.ArgumentMap;
import exceptions.InvalidArgumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TODO: Test Images created from template.xcf display with the right side of the image on the left, for some reason
//      1 bit, 4 bit, 16 bit images appear blank
public class Main {

    public static String imageFName;
    public static String imageDir;

    public static String[] supportedArgs = new String[] {"-d", "-p"};
    public static Object[] defaultValues = new Object[] {System.getProperty("user.home"), ""};
    public static ArgumentMap argumentMap;
    public static ImageDisplayer imageDisplayer;


    public static void main(String[] args) throws IOException {
        //Initialize arguments
        argumentMap = new ArgumentMap(args, supportedArgs, defaultValues);
        imageDir = (String) argumentMap.getVal("-d");
        imageFName = (String) argumentMap.getVal("-p");

        if(!Files.isDirectory(Path.of(imageDir))) {
            throw new InvalidArgumentException(imageDir, "Directory does not exist.");
        }
        if(!Files.isRegularFile(Path.of(imageDir + "/" + imageFName))) {
            throw new InvalidArgumentException(imageDir, "Image does not exist at given directory.");
        }

        imageDisplayer = new ImageDisplayer(imageDir + "/" + imageFName);
        imageDisplayer.run();

    }
}

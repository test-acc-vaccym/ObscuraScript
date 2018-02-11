package tralolta.obscurascript.imagemanip.writers;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import tralolta.obscurascript.util.DataUtil;
import tralolta.obscurascript.imagemanip.exceptions.UnsupportedFormatException;

/**
 * Created by Nemanja Đokić on 18/02/09.
 */

public class ImageWriterFactory {

    public ImageWriterStrategy getInstance(byte[] inputBytes, File myFolder) throws UnsupportedFormatException, FileNotFoundException {
            byte[] twoBytes = new byte[2];
            twoBytes[0] = inputBytes[0];
            twoBytes[1] = inputBytes[1];
            String firstTwoBytes = DataUtil.bytesToHex(twoBytes);
            switch(firstTwoBytes){
                case "ffd8": //This is JPEG
                    String timeStampJpg = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileNameJpg = "obscuraScript_" + timeStampJpg + ".jpg";
                    File outputImageJpg = new File(myFolder, imageFileNameJpg);
                    return new JPGImageWriter(inputBytes, new FileOutputStream(outputImageJpg));
                case "8950": //This is PNG
                    String timeStampPng = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileNamePng = "obscuraScript_" + timeStampPng + ".png";
                    File outputImagePng = new File(myFolder, imageFileNamePng);
                    return new PNGImageWriter(inputBytes, new FileOutputStream(outputImagePng));
                default:
                    throw new UnsupportedFormatException();
            }
    }

}

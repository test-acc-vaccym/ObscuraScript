package tralolta.obscurascript.imagemanip.readers;

import tralolta.obscurascript.util.DataUtil;
import tralolta.obscurascript.imagemanip.exceptions.UnsupportedFormatException;

/**
 * Created by Nemanja Đokić on 18/02/10.
 */

public class ImageReaderFactory {

    public ImageReaderStrategy getInstance(byte[] inputBytes) throws UnsupportedFormatException {
        byte[] twoBytes = new byte[2];
        twoBytes[0] = inputBytes[0];
        twoBytes[1] = inputBytes[1];
        String firstTwoBytes = DataUtil.bytesToHex(twoBytes);
        switch(firstTwoBytes){
            case "ffd8": //JPG
                return new JPGImageReader(inputBytes);
            case "8950": //PNG
                return new PNGImageReader(inputBytes);
            default:
                throw new UnsupportedFormatException();
        }
    }

}

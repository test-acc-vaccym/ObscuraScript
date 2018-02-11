package tralolta.obscurascript.imagemanip.readers;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nemanja Đokić on 18/02/10.
 */

public abstract class ImageReaderStrategy {
    protected byte[] inputBytes;
    public ImageReaderStrategy(byte[] inputBytes){
        this.inputBytes = inputBytes;
    }

    public abstract byte[] readFromImage() throws IOException;
}

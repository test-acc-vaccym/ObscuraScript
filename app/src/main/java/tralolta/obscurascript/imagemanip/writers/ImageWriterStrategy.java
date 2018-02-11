package tralolta.obscurascript.imagemanip.writers;

import android.graphics.Bitmap;
import android.icu.util.Output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tralolta.obscurascript.imagemanip.exceptions.BufferWriteException;

/**
 * Created by Nemanja Đokić on 18/02/09.
 */

public abstract class ImageWriterStrategy {
    protected byte[] inputBytes;
    protected OutputStream outputStream;
    public ImageWriterStrategy(byte[] inputBytes, OutputStream outputStream){
        this.inputBytes = inputBytes;
        this.outputStream = outputStream;
    }

    public abstract void writeToImage(byte[] buffer) throws IOException;
}

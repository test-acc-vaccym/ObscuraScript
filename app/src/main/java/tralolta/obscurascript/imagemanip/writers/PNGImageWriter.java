package tralolta.obscurascript.imagemanip.writers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import tralolta.obscurascript.imagemanip.exceptions.BufferWriteException;

/**
 * Created by Nemanja Đokić on 18/02/09.
 */

public class PNGImageWriter extends ImageWriterStrategy {

    Bitmap image;

    public PNGImageWriter(byte[] inputBytes, OutputStream outputStream){
        super(inputBytes, outputStream);
    }

    @Override
    public void writeToImage(byte[] buffer) throws FileNotFoundException, BufferWriteException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        image = BitmapFactory.decodeStream(new ByteArrayInputStream(inputBytes), null, options);
        int bufferLength = buffer.length;
        int numOfPixels = image.getWidth() * image.getHeight();
        if (((bufferLength * 8) + 4) > numOfPixels) {
            throw new BufferWriteException();
        }
        byte[] sizeOfBuffer = ByteBuffer.allocate(4).putInt(bufferLength).array();
        int numOfBytes = image.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocate(numOfBytes);
        image.copyPixelsToBuffer(byteBuffer);
        byte[] pixels = byteBuffer.array();
        for (int i = 0; i < 4; i++) {
            byte toWrite = sizeOfBuffer[i];
            int mask = 128;
            for (int j = 0; j < 8; j++) {
                byte pixel = pixels[i * 8 + j];
                int currentBit = toWrite & mask;
                if (currentBit != 0) {
                    currentBit = 1;
                }
                int pixelLastBit = pixel & 1;
                if (pixelLastBit > currentBit) {
                    pixel--;
                } else if (pixelLastBit < currentBit) {
                    pixel++;
                }
                pixels[i * 8 + j] = pixel;

                mask = mask >> 1;
            }
        }
        for (int i = 0; i < bufferLength; i++) {
            byte toWrite = buffer[i];
            int mask = 128;
            for (int j = 0; j < 8; j++) {
                byte pixel = pixels[(i + 4) * 8 + j];
                int currentBit = toWrite & mask;
                if (currentBit != 0) {
                    currentBit = 1;
                }
                int pixelLastBit = pixel & 1;
                if (pixelLastBit > currentBit) {
                    pixel--;
                } else if (pixelLastBit < currentBit) {
                    pixel++;
                }
                pixels[(i + 4) * 8 + j] = pixel;

                mask = mask >> 1;
            }
        }
        image.copyPixelsFromBuffer(ByteBuffer.wrap(pixels));
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
    }
}

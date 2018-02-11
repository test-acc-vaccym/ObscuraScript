package tralolta.obscurascript.imagemanip.readers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Nemanja Đokić on 18/02/10.
 */

public class PNGImageReader  extends ImageReaderStrategy{

    public PNGImageReader(byte[] inputBytes){
        super(inputBytes);
    }

    @Override
    public byte[] readFromImage() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap image = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length, options);
        ByteBuffer buffer = ByteBuffer.allocate(image.getByteCount());
        image.copyPixelsToBuffer(buffer);
        byte[] pixels = buffer.array();
        byte[] bufferSize = new byte[4];
        for (int i = 0; i < 4; i++) {
            byte readInto = 0;
            for (int j = 0; j < 8; j++) {
                byte pixel = pixels[i * 8 + j];
                int lastBit = pixel & 1;
                if (lastBit == 1) {
                    readInto++;
                }
                if (j == 7)
                    continue;
                readInto = (byte) (readInto << 1);
            }
            bufferSize[i] = readInto;
        }
        int bufferLength = ByteBuffer.wrap(bufferSize).getInt();
        byte[] data = new byte[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
            byte readInto = 0;
            for (int j = 0; j < 8; j++) {
                byte pixel = pixels[(i + 4) * 8 + j];
                int lastBit = pixel & 1;
                if (lastBit == 1) {
                    readInto++;
                }
                if (j == 7)
                    continue;
                readInto = (byte) (readInto << 1);
            }
            data[i] = readInto;
        }
        return data;
    }
}

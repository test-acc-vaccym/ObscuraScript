package tralolta.obscurascript.imagemanip.writers;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nemanja Đokić on 18/02/09.
 */

public class JPGImageWriter extends ImageWriterStrategy {

    public JPGImageWriter(byte[] inputBytes, OutputStream outputStream){
        super(inputBytes, outputStream);
    }

    @Override
    public void writeToImage(byte[] buffer) throws IOException {
        int imageLen = 0;
        for(int i = inputBytes.length-1 ; i >= 0 ; i--){
            if(inputBytes[i] != (-39)) continue;
            else if((i-1) >= 0 && inputBytes[i-1] == -1){
                imageLen = i+1;
                break;
            }
        }
        if(imageLen == 0){
            throw new IOException();
        }
        byte[] resultBytes = new byte[imageLen + buffer.length];
        for(int i = 0; i<imageLen+buffer.length; i++){
            if(i < imageLen){
                resultBytes[i] = inputBytes[i];
            }else{
                resultBytes[i] = buffer[i%imageLen];
            }
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        bufferedOutputStream.write(resultBytes, 0, resultBytes.length);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }
}

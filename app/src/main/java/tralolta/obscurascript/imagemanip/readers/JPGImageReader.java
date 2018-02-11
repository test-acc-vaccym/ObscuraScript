package tralolta.obscurascript.imagemanip.readers;

import java.io.IOException;

/**
 * Created by Nemanja Đokić on 18/02/10.
 */

public class JPGImageReader extends ImageReaderStrategy{

    public JPGImageReader(byte[] inputBytes){
        super(inputBytes);
    }

    @Override
    public byte[] readFromImage() throws IOException {
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
        byte[] toReturn = new byte[inputBytes.length - imageLen];
        for(int i = 0; i<inputBytes.length - imageLen ; i++){
            toReturn[i] = inputBytes[imageLen + i];
        }
        return toReturn;
    }
}

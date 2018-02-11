package tralolta.obscurascript.imagemanip.exceptions;

import java.io.IOException;

/**
 * Created by Nemanja Đokić on 18/02/09.
 */

public class BufferWriteException extends IOException {


    public BufferWriteException() {
        super("Buffer to write too large");
    }

    public BufferWriteException(String message) {
        super(message);
    }

}

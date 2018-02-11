package tralolta.obscurascript.imagemanip.exceptions;

/**
 * Created by Nemanja Đokić on 18/02/09.
 */

public class UnsupportedFormatException extends Exception {

    public UnsupportedFormatException() {
        super("Unsupported image format");
    }

    public UnsupportedFormatException(String message) {
        super(message);
    }

}
package me.bassihassan.exception;

public class VoofException extends RuntimeException {
    public VoofException() {
    }

    public VoofException(String s) {
        super(s);
    }

    public VoofException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VoofException(Throwable throwable) {
        super(throwable);
    }

    public VoofException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

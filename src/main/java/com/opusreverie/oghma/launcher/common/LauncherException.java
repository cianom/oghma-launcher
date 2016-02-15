package com.opusreverie.oghma.launcher.common;

/**
 * Created by keen on 27/01/16.
 */
public class LauncherException extends RuntimeException {

    public LauncherException(String message) {
        super(message);
    }

    public LauncherException(String message, Throwable cause) {
        super(message, cause);
    }

    public LauncherException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }

}

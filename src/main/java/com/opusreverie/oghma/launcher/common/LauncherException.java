package com.opusreverie.oghma.launcher.common;

import io.lyra.oghma.common.OghmaException;

/**
 * Generic unchecked Launcher exception.
 *
 * @author Cian.
 */
public class LauncherException extends OghmaException {

    public LauncherException(final String message) {
        super(message);
    }

    public LauncherException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LauncherException(final Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }

}

package com.opusreverie.oghma.launcher.common;

import io.lyra.oghma.common.OghmaException;

/**
 * Generic unchecked Launcher exception.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class LauncherException extends OghmaException {

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

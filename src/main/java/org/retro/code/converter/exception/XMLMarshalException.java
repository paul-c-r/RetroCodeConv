/*
 * RetroCodeConv is released under the GNU GPL v3.0 licence.
 * You may copy, distribute and modify the software as long as you keep modifications under GPL.
 * All derived works of, or applications using, RetroCodeConv must be released under the same licence and be made available to the Open Source community.
 *
 * Please refer to https://www.gnu.org/licenses/gpl-3.0.html for all licence conditions.
 *
 * Copyright © Paul C. Rau (Financial Systems Developer)
 *
 */

package org.retro.code.converter.exception;

import org.retro.code.converter.aspects.ExceptionAspect;

/**
 * Exception thrown while processing XML files.
 */
public class XMLMarshalException extends RuntimeException {
    /**
     * This is thrown when there is a marshal error while processing the
     * settings, conversion or translation files.
     * <p/>
     * It is caught by {@link ExceptionAspect}
     *
     * @param cause A wrapped exception indicating the cause.
     */
    public XMLMarshalException(Throwable cause) {
        super(cause);
    }
}

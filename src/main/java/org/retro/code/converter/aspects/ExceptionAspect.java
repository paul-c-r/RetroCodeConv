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

package org.retro.code.converter.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect class for catching exceptions.
 */
@Aspect
public class ExceptionAspect {
    static private final Logger logger = Logger.getLogger(ExceptionAspect.class);

    /**
     * Catches all the exceptions thrown by RetroCodeConv and logs them.
     *
     * @param ex The Exception thrown.
     */
    @AfterThrowing(pointcut = "execution(* *(..))", throwing = "ex")
    public void logException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
    }

}

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

package org.retro.code.converter.utils;

import org.apache.log4j.Logger;

/**
 *  This class assists in the logging by supplying a convenient functions.
 *  The main reason for these functions is to log the level of code processing.
 *  The level in this case refers to the number of children code section
 *  from the parent.
 */

public class LogUtil {

    public static void debug(Logger logger, String output) {
        debug(logger, 0, output);
    }

    public static void debug(Logger logger, int level, String output) {
        if (level > 0) {
            output = "L" + level + ": " + output;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(output);
        }
    }

    public static void trace(Logger logger, String output) {
        trace(logger, 0, output);
    }

    public static void trace(Logger logger, int level, String output) {
        if (level > 0) {
            output = "L" + level + ": " + output;
        }
        if (logger.isTraceEnabled()) {
            logger.trace(output);
        }
    }

    public static String getSingleLineOutput(String code, int requiredLength) {
        String result = "[" + code.replaceAll("\n","") + "]";
        if (result.length() < requiredLength) {
            return result;
        } else {
            return result.substring(0, (requiredLength/2)) +
                   " ... " +
                   result.substring(result.length() - (requiredLength/2));
        }
    }
}

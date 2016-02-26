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

import org.retro.code.converter.xml.v1.types.XTranslateEntry;
import org.retro.code.converter.xml.v1.types.XTranslationOperationType;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates (substitutes) regex strings to another strings.
 */
public class TranslationUtils {

    /**
     * Translates all the supplied translation entries against the supplied code.
     *
     * @param source           The code to translate
     * @param translateEntries The translation entries
     * @return The translated code
     */
    public static String translate(String source, Collection<XTranslateEntry> translateEntries) {

        String target = source;
        for (XTranslateEntry translateEntry : translateEntries) {
            if (translateEntry.getOperation() == null ||
                translateEntry.getOperation() == XTranslationOperationType.REPLACE) {
                target = performReplacement(target, translateEntry.getFromRegex(), translateEntry.getToString(),
                                           translateEntry.isProcessOnce());
            } else {
                target = regexReplace(target, translateEntry);
            }
        }
        return target;
    }

    private static String performReplacement(String source, String regexFrom, String toString, boolean single) {
        if (single) {
            return source.replaceFirst(regexFrom, toString);
        }
        return source.replaceAll(regexFrom, toString);
    }

    private static String regexReplace(String source, XTranslateEntry translateEntry) {
        Pattern p = Pattern.compile(translateEntry.getFromRegex());
        Matcher matcher = p.matcher(source);
        if (matcher.find()) {
            String toString = source.substring(matcher.start(), matcher.end());
            if (translateEntry.getOperation() == XTranslationOperationType.ALL_UPPER) {
                toString = toString.toUpperCase();
            } else {
                toString = toString.toLowerCase();
            }
            if (translateEntry.isProcessOnce()) {
                return source.substring(0, matcher.start()) + toString + source.substring(matcher.end());
            } else {
                return source.substring(0, matcher.start()) + toString +
                       regexReplace(source.substring(matcher.end()), translateEntry);
            }
        }
        return source;
    }

}

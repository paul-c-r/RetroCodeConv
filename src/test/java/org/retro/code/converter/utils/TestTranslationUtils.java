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

import org.junit.Before;
import org.junit.Test;
import org.retro.code.converter.xml.v1.types.XTranslateEntry;
import org.retro.code.converter.xml.v1.types.XTranslationOperationType;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This runs two tests that perform many single and multiple translations.
 */

public class TestTranslationUtils {

    private static final String TRANSLATE_THIS = "1234 translate(){} 1234";
    private static final String TRANSLATE_FROM_1 = "translate\\(\\)\\{\\}";
    private static final String TRANSLATE_TO_1 = "- 4321 -";
    private static final String TRANSLATE_FROM_2 = "4321";
    private static final String TRANSLATE_TO_2 = "9999";
    private static final String TRANSLATE_FROM_3 = "1234";
    private static final String TRANSLATE_TO_3 = "8888";

    private static final String TRANSLATE_FROM_4 = "123.*"; // the whole line
    private static final String TRANSLATE_TO_4 = "1111";

    private static final String TRANSLATE_TO_INIT_1 = "ABcdE abCde ABcDe";
    private static final String TRANSLATE_TO_INIT_2 = "abCdE abcdE AbcdE";
    private static final String TRANSLATE_TO_INIT_3 = "abCdE ABcdE ABcdE";

    private static final String TRANSLATE_TO_SEARCH_1 = "[abcde]";
    private static final String TRANSLATE_TO_SEARCH_2 = "[ABCDE]";
    private static final String TRANSLATE_TO_SEARCH_3 = "ABcdE";

    private static final String EXPECTED_UPPER = "ABCDE ABCDE ABCDE";
    private static final String EXPECTED_LOWER = "abcde abcde abcde";
    private static final String EXPECTED_LOWER_SINGLE = "abCdE abcde ABcdE";

    private static final String EXPECTED_OUTPUT_1 = "8888 - 9999 - 1234";
    private static final String EXPECTED_OUTPUT_2 = "8888 - 9999 - 8888";
    private static final String EXPECTED_OUTPUT_3 = "1111";

    private static final String TRANSLATE_MULTI_LINE = "1234 5678;\n4567 8901;\n1234 4321;\n";
    private static final String TRANSLATE_FROM_MULTI = "(?m)^1234\\s.*;\n";
    private static final String EXPECTED_MULTI_1 = "4567 8901;\n1234 4321;\n";
    private static final String EXPECTED_MULTI_2 = "4567 8901;\n";

    private static final XTranslateEntry entry1 = new XTranslateEntry();
    private static final XTranslateEntry entry2 = new XTranslateEntry();
    private static final XTranslateEntry entry3 = new XTranslateEntry();
    private static final XTranslateEntry entry4 = new XTranslateEntry();
    private static final XTranslateEntry entry5 = new XTranslateEntry();

    private static final XTranslateEntry multiEntry1 = new XTranslateEntry();
    private static final XTranslateEntry multiEntry2 = new XTranslateEntry();

    private static final XTranslateEntry toUpper = new XTranslateEntry();
    private static final XTranslateEntry toLower = new XTranslateEntry();

    @Before
    public void init() throws Throwable {
        entry1.setId(1);
        entry1.setFromRegex(TRANSLATE_FROM_1);
        entry1.setToString(TRANSLATE_TO_1);
        entry1.setProcessOnce(true);
        entry2.setId(2);
        entry2.setFromRegex(TRANSLATE_FROM_2);
        entry2.setToString(TRANSLATE_TO_2);
        entry2.setProcessOnce(true);
        entry3.setId(3);
        entry3.setFromRegex(TRANSLATE_FROM_3);
        entry3.setToString(TRANSLATE_TO_3);
        entry3.setProcessOnce(true);
        entry4.setId(4);
        entry4.setFromRegex(TRANSLATE_FROM_3);
        entry4.setToString(TRANSLATE_TO_3);
        entry4.setProcessOnce(false);
        entry5.setId(5);
        entry5.setFromRegex(TRANSLATE_FROM_4);
        entry5.setToString(TRANSLATE_TO_4);
        entry5.setProcessOnce(false);
        multiEntry1.setId(6);
        multiEntry1.setFromRegex(TRANSLATE_FROM_MULTI);
        multiEntry1.setToString("");
        multiEntry1.setProcessOnce(true);
        multiEntry2.setId(7);
        multiEntry2.setFromRegex(TRANSLATE_FROM_MULTI);
        multiEntry2.setToString("");
        multiEntry2.setProcessOnce(false);
        toUpper.setId(8);
        toUpper.setFromRegex(TRANSLATE_TO_SEARCH_1);
        toUpper.setOperation(XTranslationOperationType.ALL_UPPER);
        toUpper.setProcessOnce(false);
        toLower.setId(9);
        toLower.setFromRegex(TRANSLATE_TO_SEARCH_2);
        toLower.setOperation(XTranslationOperationType.ALL_LOWER);
        toLower.setProcessOnce(false);
    }

    @Test
    public void testTranslation1() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(entry1);
        xList.add(entry2);
        xList.add(entry3);
        assertEquals(EXPECTED_OUTPUT_1, TranslationUtils.translate(TRANSLATE_THIS, xList));
    }

    @Test
    public void testTranslation2() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(entry1);
        xList.add(entry2);
        xList.add(entry4);
        assertEquals(EXPECTED_OUTPUT_2, TranslationUtils.translate(TRANSLATE_THIS, xList));
    }

    @Test
    public void testTranslation3() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(entry5);
        assertEquals(EXPECTED_OUTPUT_3, TranslationUtils.translate(TRANSLATE_THIS, xList));
    }

    @Test
    public void testTranslation4() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(multiEntry1);
        assertEquals(EXPECTED_MULTI_1, TranslationUtils.translate(TRANSLATE_MULTI_LINE, xList));
    }

    @Test
    public void testTranslation5() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(multiEntry2);
        assertEquals(EXPECTED_MULTI_2, TranslationUtils.translate(TRANSLATE_MULTI_LINE, xList));
    }

    @Test
    public void testTranslationToUpper() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(toUpper);
        assertEquals(EXPECTED_UPPER, TranslationUtils.translate(TRANSLATE_TO_INIT_1, xList));
    }

    @Test
    public void testTranslationToLower() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        xList.add(toLower);
        assertEquals(EXPECTED_LOWER, TranslationUtils.translate(TRANSLATE_TO_INIT_2, xList));
    }

    @Test
    public void testTranslationToSingleLower() {
        List<XTranslateEntry> xList = new LinkedList<XTranslateEntry>();
        toLower.setProcessOnce(true);
        toLower.setFromRegex(TRANSLATE_TO_SEARCH_3);
        xList.add(toLower);
        assertEquals(EXPECTED_LOWER_SINGLE, TranslationUtils.translate(TRANSLATE_TO_INIT_3, xList));
    }
}

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

package org.retro.code.converter.execution.convert.types;


import org.retro.code.converter.xml.TranslationsLoader;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class TestProperties {

    public static final String PUBLIC_STATIC_FINAL_STRING = "public static final String ";
    public static final String KEY = "com.p_c_r.property";
    public static final String EQUALS = "=";
    public static final String LINE_CONTINUED = "1st line of sql \\\n ";
    public static final String LINE_ENDED = "2nd line of sql\n";
    public static final String START_STRING = " = \"";
    public static final String END_STRING = "\"";
    public static final String NEW_LINE = "\n";

    @Test
    public void convertSingleLineTest() {
        StringBuilder source = new StringBuilder(KEY);
        source.append(EQUALS).append(LINE_ENDED);
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setSearchStart("com.p_c_r");
        codeSection.setOutputPreamble(PUBLIC_STATIC_FINAL_STRING +
                                      Properties.KEY + START_STRING +
                                      Properties.VALUE + END_STRING);

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Properties properties = new Properties(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> map = new LinkedHashMap<Integer, StringBuilder>();
        properties.convert(source, codeSection, map);
        StringBuilder expected = new StringBuilder(PUBLIC_STATIC_FINAL_STRING);
        expected.append(KEY);
        expected.append(START_STRING);
        expected.append(StringUtils.removeEnd(LINE_ENDED, "\n"));
        expected.append(END_STRING);
        expected.append(NEW_LINE);
        assertEquals(expected.toString(), map.get(1).toString());
    }

    @Test
    public void convertMultiLineTest() {
        StringBuilder source = new StringBuilder(KEY);
        source.append(EQUALS).append(LINE_CONTINUED).append(LINE_ENDED);
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setSearchStart("com.p_c_r.property");
        codeSection.setOutputPreamble(PUBLIC_STATIC_FINAL_STRING +
                                      Properties.KEY + START_STRING +
                                      Properties.VALUE + END_STRING);
        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Properties properties = new Properties(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> map = new LinkedHashMap<Integer, StringBuilder>();
        properties.convert(source, codeSection, map);
        StringBuilder expected = new StringBuilder(PUBLIC_STATIC_FINAL_STRING);
        expected.append(KEY);
        expected.append(START_STRING);
        expected.append(StringUtils.removeEnd(LINE_CONTINUED, "\\\n "));
        expected.append(StringUtils.removeEnd(LINE_ENDED, "\n"));
        expected.append(END_STRING);
        expected.append(NEW_LINE);
        assertEquals(expected.toString(), map.get(1).toString());
    }
}

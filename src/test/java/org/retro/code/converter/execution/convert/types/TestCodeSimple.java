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

import org.junit.Test;
import org.retro.code.converter.xml.TranslationsLoader;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XIdList;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class TestCodeSimple {

    private static final String SOURCE = "public void function(String[] args) {\n" +
                                         "    Object object = new Object();\n" +
                                         "    object.class();\n" +
                                         "}\n";

    private static final String FUNCTION_SEARCH_START = "public void.*\\{";
    private static final String FUNCTION_SEARCH_END = "}";

    private static final String FUNCTION_OUTPUT_PRE = "public void newFunction() {";
    private static final String FUNCTION_OUTPUT_POST = "";

    private static final String EXPECTED_OUTPUT = "public void newFunction() {\n" +
                                                  "    Object object = new Object();\n" +
                                                  "    object.class();\n" +
                                                  "}\n";

    @Test
    public void testSingleConvert() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setProcessOnce(false);
        codeSection.setSearchStart(FUNCTION_SEARCH_START);
        codeSection.setSearchEnd(FUNCTION_SEARCH_END);
        codeSection.setRemoveSearch(true);
        codeSection.setOutputPreamble(FUNCTION_OUTPUT_PRE);
        codeSection.setOutputPostamble(FUNCTION_OUTPUT_POST);
        codeSection.setTitle("TITLE");
        codeSection.setTranslations(new XIdList());

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE), codeSection, result);
        assertEquals(EXPECTED_OUTPUT, result.get(1).toString());
    }

}

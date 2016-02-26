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
import org.retro.code.converter.xml.v1.types.XCodeSectionList;
import org.retro.code.converter.xml.v1.types.XIdList;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class TestCodeRecursive {

    private static final String FUNCTION_SEARCH_START = "public void.*\\{";
    private static final String FUNCTION_OUTPUT_PRE = "public void newFunction() {";

    private static final String SOURCE_CLASS = "public class cls {\n" +
                                               "  public void function1(String[] args) {\n" +
                                               "    Object object1 = new Object();\n" +
                                               "    object.class();\n" +
                                               "  }\n" +
                                               "  public void function2(String[] args) {\n" +
                                               "    Object object2 = new Object();\n" +
                                               "    object.class();\n" +
                                               "  }\n" +
                                               "}";

    private static final String SEARCH_CLASS_START = "public class.*\\{";
    private static final String OUTPUT_CLASS_PRE = "public class cls {";

    private static final String EXPECTED_OUTPUT = "public class cls {\n" +
                                                  "  public void newFunction() {\n" +
                                                  "    Object object1 = new Object();\n" +
                                                  "    object.class();\n" +
                                                  "  }\n" +
                                                  "  public void newFunction() {\n" +
                                                  "    Object object2 = new Object();\n" +
                                                  "    object.class();\n" +
                                                  "  }\n" +
                                                  "}";


    @Test
    public void testRecursiveConvert() {
        XCodeSection childSection = new XCodeSection();
        childSection.setId(1);
        childSection.setTargetFileId(1);
        childSection.setProcessOnce(false);
        childSection.setTranslations(new XIdList());
        childSection.setTitle("INTERNAL");
        childSection.setSearchStart(FUNCTION_SEARCH_START);
        childSection.setRemoveSearch(true);
        childSection.setOutputPreamble(FUNCTION_OUTPUT_PRE);

        XCodeSection outerSection = new XCodeSection();
        outerSection.setId(2);
        outerSection.setTargetFileId(1);
        outerSection.setProcessOnce(true);
        outerSection.setSearchStart(SEARCH_CLASS_START);
        outerSection.setRemoveSearch(true);
        outerSection.setOutputPreamble(OUTPUT_CLASS_PRE);
        outerSection.setTitle("TITLE");
        outerSection.setCodeSections(new XCodeSectionList());
        outerSection.getCodeSections().getCodeSection().add(childSection);
        outerSection.setTranslations(new XIdList());

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_CLASS), outerSection, result);
        assertEquals(EXPECTED_OUTPUT, result.get(1).toString());
    }

}

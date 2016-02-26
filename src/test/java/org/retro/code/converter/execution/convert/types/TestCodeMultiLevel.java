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
import org.retro.code.converter.xml.v1.types.XCodeBlockType;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XCodeSectionList;
import org.retro.code.converter.xml.v1.types.XIdList;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * This test keeps the name of the class (the first contents marker) and changes the type of the class.
 * It then copies over the contents of the class itself.
 */

public class TestCodeMultiLevel {

    private static final String SEARCH_CLASS_NAME_START = "public class *";
    private static final String SEARCH_CLASS_NAME_END = " *\\{";
    private static final String OUTPUT_CLASS_PRE = "public abstract class ";
    private static final String SEARCH_FUNCTION_START = "public";
    private static final String OUTPUT_FUNCTION_PRE = "public";
    private static final String SEARCH_OBJECT_FUNCTION_START = "Object object1";
    private static final String OUTPUT_OBJECT_PRE = "Object newObject";

    private static final String SOURCE_CLASS = "public class cls {\n" +
                                               "  public void function1() {\n" +
                                               "    Object object1 = new Object();\n" +
                                               "    object.class();\n" +
                                               "  }\n" +
                                               "  private void function() {\n" +
                                               "    Object priv = new Object();\n" +
                                               "    priv.class();\n" +
                                               "  }\n" +
                                               "  public void function2() {\n" +
                                               "    Object object2 = new Object();\n" +
                                               "    object.class();\n" +
                                               "  }\n" +
                                               "}";

    private static final String EXPECTED_CLASS_OUTPUT = "public abstract class cls {\n" +
                                                        "  public void function1() {\n" +
                                                        "    Object newObject = new Object();\n" +
                                                        "    object.class();\n" +
                                                        "  }\n" +
                                                        "  private void function() {\n" +
                                                        "    Object priv = new Object();\n" +
                                                        "    priv.class();\n" +
                                                        "  }\n" +
                                                        "  public void function2() {\n" +
                                                        "    Object object2 = new Object();\n" +
                                                        "    object.class();\n" +
                                                        "  }\n" +
                                                        "}";

    @Test
    public void testClassNameConvert() {
        XCodeSection className = new XCodeSection();
        className.setId(1);
        className.setTargetFileId(1);
        className.setTranslations(new XIdList());
        className.setTitle("CLASS NAME PROCESSING");
        className.setSearchStart(SEARCH_CLASS_NAME_START);
        className.setSearchEnd(SEARCH_CLASS_NAME_END);
        className.setRemoveSearch(true);
        className.setOutputPreamble(OUTPUT_CLASS_PRE);
        className.setOutputPostamble("");
        className.setProcessOnce(true);
        className.setRemoveBrackets(false);

        XCodeSection objectContents = new XCodeSection();
        objectContents.setId(2);
        objectContents.setTargetFileId(1);
        objectContents.setTranslations(new XIdList());
        objectContents.setTitle("OBJECT PROCESSING");
        objectContents.setSearchStart(SEARCH_OBJECT_FUNCTION_START);
        objectContents.setSearchEnd("");
        objectContents.setRemoveSearch(true);
        objectContents.setOutputPreamble(OUTPUT_OBJECT_PRE);
        objectContents.setOutputPostamble("");
        objectContents.setProcessOnce(true);
        objectContents.setCodeBlock(XCodeBlockType.FUNCTION);

        XCodeSection classContents = new XCodeSection();
        classContents.setId(3);
        classContents.setTargetFileId(1);
        classContents.setTranslations(new XIdList());
        classContents.setTitle("FUNCTION PROCESSING");
        classContents.setSearchStart(SEARCH_FUNCTION_START);
        classContents.setSearchEnd("");
        classContents.setRemoveSearch(true);
        classContents.setCodeBlock(XCodeBlockType.FUNCTION);
        classContents.setOutputPreamble(OUTPUT_FUNCTION_PRE);
        classContents.setOutputPostamble("");
        classContents.setCodeSections(new XCodeSectionList());
        classContents.getCodeSections().getCodeSection().add(objectContents);
        classContents.setProcessOnce(false);

        XCodeSection outerSection = new XCodeSection();
        outerSection.setId(4);
        outerSection.setTargetFileId(1);
        outerSection.setSearchStart(".*");
        outerSection.setSearchEnd("");
        outerSection.setOutputPreamble("");
        outerSection.setOutputPostamble("");
        outerSection.setTitle("CONTAINER");
        outerSection.setCodeSections(new XCodeSectionList());
        outerSection.getCodeSections().getCodeSection().add(className);
        outerSection.getCodeSections().getCodeSection().add(classContents);
        outerSection.setTranslations(new XIdList());
        outerSection.setProcessOnce(true);
        outerSection.setCodeBlock(XCodeBlockType.ALL);

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_CLASS), outerSection, result);
        assertEquals(EXPECTED_CLASS_OUTPUT, result.get(1).toString());
    }
}

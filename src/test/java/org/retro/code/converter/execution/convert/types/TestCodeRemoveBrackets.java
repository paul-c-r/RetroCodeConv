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
 * This test removes the brackets (both round and curly) from a class and statement.
 */

public class TestCodeRemoveBrackets {

    private static final String SEARCH_UP_TO_BRACKET = "[a-zA-Z 0-9]*";
    private static final String SEARCH_SINGLE_SPACE = "\\s";
    private static final String SEARCH_EVERYTHING = ".*";

    private static final String SOURCE_CLASS = "public class cls {\n" +
                                               "  public void function1() {\n" +
                                               "    Object object1 = new Object();\n" +
                                               "    object.class();\n" +
                                               "  }\n" +
                                               "  public void function2() {\n" +
                                               "    Object object2 = new Object();\n" +
                                               "    object.class();\n" +
                                               "  }\n" +
                                               "}";
    private static final String EXPECTED_CLASS_OUTPUT = "\n" +
                                                        "  public void function1() {\n" +
                                                        "    Object object1 = new Object();\n" +
                                                        "    object.class();\n" +
                                                        "  }\n" +
                                                        "  public void function2() {\n" +
                                                        "    Object object2 = new Object();\n" +
                                                        "    object.class();\n" +
                                                        "  }\n";

    private static final String SOURCE_STATEMENT_1 = "for (int i=0; i<10; i++) {}";
    private static final String EXPECTED_STATEMENT_OUTPUT_1 = "int i=0; i<10; i++ {}";

    private static final String SOURCE_STATEMENT_2 = "for (int i=0; i<10; i++) {" +
                                                     "  for (Object o : objectList) {" +
                                                     "      o.function();" +
                                                     "  }" +
                                                     "}";
    private static final String EXPECTED_STATEMENT_OUTPUT_2 = "  for (Object o : objectList) {" +
                                                              "      o.function();" +
                                                              "  }";

    @Test
    public void testRemoveClassBrackets() {
        XCodeSection cls = new XCodeSection();
        cls.setId(1);
        cls.setTargetFileId(1);
        cls.setTranslations(new XIdList());
        cls.setTitle("CLASS");
        cls.setSearchStart(SEARCH_UP_TO_BRACKET);
        cls.setCodeBlock(XCodeBlockType.FUNCTION);
        cls.setProcessOnce(true);
        cls.setRemoveBrackets(true);
        cls.setRemoveSearch(true);

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_CLASS), cls, result);
        assertEquals(EXPECTED_CLASS_OUTPUT, result.get(1).toString());
    }

    @Test
    public void testRemoveStatementBrackets() {
        XCodeSection stmt = new XCodeSection();
        stmt.setId(1);
        stmt.setTargetFileId(1);
        stmt.setTranslations(new XIdList());
        stmt.setTitle("STATEMENT");
        stmt.setSearchStart(SEARCH_UP_TO_BRACKET);
        stmt.setCodeBlock(XCodeBlockType.STATEMENT);
        stmt.setProcessOnce(true);
        stmt.setRemoveBrackets(true);
        stmt.setRemoveSearch(true);

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_STATEMENT_1), stmt, result);
        assertEquals(EXPECTED_STATEMENT_OUTPUT_1, result.get(1).toString());
    }

    /**
     * This test removes the round brackets around the for statement parameters and then the
     * curly brackets from the contents for statement. The result is the contents of the
     * for statement exposed.
     */
    @Test
    public void testRemoveFullStatementBrackets() {
        // this is one way of deleting the contents of a section of code
        // ie. select everything and then do not specify output settings.
        XCodeSection deleteContents = new XCodeSection();
        deleteContents.setId(100);
        deleteContents.setTargetFileId(1);
        deleteContents.setTitle("TERMINATOR");
        deleteContents.setTranslations(new XIdList());
        deleteContents.setSearchStart(SEARCH_EVERYTHING);
        deleteContents.setCodeBlock(XCodeBlockType.ALL);
        deleteContents.setRemoveSearch(true);

        // this will give its contents to the deleteContents code section
        // which will gobble it up
        // another way of doing this is with the translation
        XCodeSection stmt1 = new XCodeSection();
        stmt1.setId(1);
        stmt1.setTargetFileId(1);
        stmt1.setTranslations(new XIdList());
        stmt1.setTitle("FOR");
        stmt1.setSearchStart(SEARCH_UP_TO_BRACKET);
        stmt1.setRemoveSearch(true);
        stmt1.setCodeBlock(XCodeBlockType.STATEMENT);
        stmt1.setProcessOnce(true);
        stmt1.setRemoveBrackets(true);
        stmt1.setCodeSections(new XCodeSectionList());
        stmt1.getCodeSections().getCodeSection().add(deleteContents);

        XCodeSection stmt2 = new XCodeSection();
        stmt2.setId(2);
        stmt2.setTargetFileId(1);
        stmt2.setTranslations(new XIdList());
        stmt2.setTitle("INTERNALS");
        stmt2.setSearchStart(SEARCH_SINGLE_SPACE);
        stmt2.setRemoveSearch(true);
        stmt2.setCodeBlock(XCodeBlockType.FUNCTION);
        stmt2.setProcessOnce(true);
        stmt2.setRemoveBrackets(true);

        XCodeSection container = new XCodeSection();
        container.setId(3);
        container.setTargetFileId(1);
        container.setTranslations(new XIdList());
        container.setTitle("CONTAINER");
        container.setSearchStart(SEARCH_EVERYTHING);
        container.setCodeBlock(XCodeBlockType.ALL);
        container.setProcessOnce(true);
        container.setCodeSections(new XCodeSectionList());
        container.getCodeSections().getCodeSection().add(stmt1);
        container.getCodeSections().getCodeSection().add(stmt2);

        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_STATEMENT_2), container, result);
        assertEquals(EXPECTED_STATEMENT_OUTPUT_2, result.get(1).toString());
    }
}

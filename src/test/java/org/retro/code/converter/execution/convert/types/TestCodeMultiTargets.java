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

import org.junit.Before;
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
 * This test tests the situation where there are many targets from a single source.
 * It tests outs put to 2 or 3 targets, along with removal of pieces of code.
 */

public class TestCodeMultiTargets {

    private static final String FIND_CLASS = "public[ 0-9a-zA-Z]*";
    private static final String FUNCTION_1 = "  public functionToTarget1";
    private static final String FUNCTION_2 = "  public functionToTarget2";

    private static final String SOURCE_CLASS = "public class cls {" +
                                               "  public functionToTarget1() { object1 }" +
                                               "  public functionToTarget2() { object2 }" +
                                               "}";

    private static final String EXPECTED_1 = "public class cls ";
    private static final String EXPECTED_2 = "  public functionToTarget1() { object1 }";
    private static final String EXPECTED_3 = "  public functionToTarget2() { object2 }";

    private static final XCodeSection CODE_SECTION_1 = new XCodeSection();
    private static final XCodeSection CODE_SECTION_2 = new XCodeSection();
    private static final XCodeSection CONTAINER = new XCodeSection();

    private static final TranslationsLoader TRANS_MOCK = mock(TranslationsLoader.class);

    @Before
    public void init() {
        CODE_SECTION_1.setId(111);
        CODE_SECTION_1.setTargetFileId(2);
        CODE_SECTION_1.setTranslations(new XIdList());
        CODE_SECTION_1.setTitle("FUNCTION 1");
        CODE_SECTION_1.setSearchStart(FUNCTION_1);
        CODE_SECTION_1.setRemoveSearch(false);
        CODE_SECTION_1.setProcessOnce(true);
        CODE_SECTION_1.setCodeBlock(XCodeBlockType.FUNCTION);

        CODE_SECTION_2.setId(222);
        CODE_SECTION_2.setTargetFileId(3);
        CODE_SECTION_2.setTranslations(new XIdList());
        CODE_SECTION_2.setTitle("FUNCTION 2");
        CODE_SECTION_2.setSearchStart(FUNCTION_2);
        CODE_SECTION_2.setRemoveSearch(false);
        CODE_SECTION_2.setProcessOnce(true);
        CODE_SECTION_2.setCodeBlock(XCodeBlockType.FUNCTION);

        CONTAINER.setId(1);
        CONTAINER.setTargetFileId(1);
        CONTAINER.setTranslations(new XIdList());
        CONTAINER.setTitle("CONTAINER");
        CONTAINER.setSearchStart(FIND_CLASS);
        CONTAINER.setRemoveSearch(false);
        CONTAINER.setRemoveBrackets(true);
        CONTAINER.setProcessOnce(true);
        CONTAINER.setCodeBlock(XCodeBlockType.FUNCTION);
        CONTAINER.setCodeSections(new XCodeSectionList());
        CONTAINER.getCodeSections().getCodeSection().add(CODE_SECTION_1);
        CONTAINER.getCodeSections().getCodeSection().add(CODE_SECTION_2);
    }

    @Test
    public void testSimpleThreeTargets() {
        Code code = new Code(TRANS_MOCK, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_CLASS), CONTAINER, result);
        assertEquals(EXPECTED_1, result.get(1).toString());
        assertEquals(EXPECTED_2, result.get(2).toString());
        assertEquals(EXPECTED_3, result.get(3).toString());
    }

    @Test
    public void testSimpleTwoTargets() {
        Code code = new Code(TRANS_MOCK, new LinkedHashMap<Integer, StringBuilder>());
        CONTAINER.setRemoveSearch(true);
        CODE_SECTION_1.setTargetFileId(1);
        CODE_SECTION_2.setTargetFileId(2);
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE_CLASS), CONTAINER, result);
        assertEquals(EXPECTED_2, result.get(1).toString());
        assertEquals(EXPECTED_3, result.get(2).toString());
    }

}

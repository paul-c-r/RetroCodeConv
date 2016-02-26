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

import org.junit.Before;
import org.junit.Test;
import org.retro.code.converter.exception.ExecutionException;
import org.retro.code.converter.execution.Executor;
import org.retro.code.converter.xml.CodeConversionsLoader;
import org.retro.code.converter.xml.TranslationsLoader;
import org.retro.code.converter.xml.v1.types.XCodeConversionFileType;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XCodeSectionList;
import org.retro.code.converter.xml.v1.types.XIdList;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This mocks the code conversion loader so that the code sections can be
 * created for the testing of the component references.
 */

public class TestExecutor {

    private static final XCodeSection CS_BASE = new XCodeSection();

    private static final XCodeSection CS_L1_1   = new XCodeSection();
    private static final XCodeSection CS_L1_2   = new XCodeSection();
    private static final XCodeSection CS_L2_1_1 = new XCodeSection();
    private static final XCodeSection CS_L2_1_2 = new XCodeSection();
    private static final XCodeSection CS_L2_2_1 = new XCodeSection();
    private static final XCodeSection CS_L2_2_2 = new XCodeSection();

    private CodeConversionsLoader codeConversionsLoader = mock(CodeConversionsLoader.class);

    /**
     * Adding code sections by reference and by actual instances.
     *
     * @throws Throwable
     */
    @Before
    public void init() throws Throwable {
        CS_L2_2_1.setId(100);
        CS_L2_2_1.setCodeSections(new XCodeSectionList());
        CS_L2_2_1.setCodeSectionComponents(new XIdList());
        CS_L2_2_2.setId(90);
        CS_L2_2_2.setCodeSections(new XCodeSectionList());
        CS_L2_2_2.setCodeSectionComponents(new XIdList());
        CS_L2_1_1.setId(80);
        CS_L2_1_1.setCodeSections(new XCodeSectionList());
        CS_L2_1_1.setCodeSectionComponents(new XIdList());
        CS_L2_1_2.setId(70);
        CS_L2_1_2.setCodeSections(new XCodeSectionList());
        CS_L2_1_2.setCodeSectionComponents(new XIdList());
        CS_L1_1.setId(60);
        CS_L1_1.setCodeSections(new XCodeSectionList());
        CS_L1_1.setCodeSectionComponents(new XIdList());
        CS_L1_2.setId(50);
        CS_L1_2.setCodeSections(new XCodeSectionList());
        CS_L1_2.setCodeSectionComponents(new XIdList());

        CS_L1_1.getCodeSections().getCodeSection().add(CS_L2_1_1);
        CS_L1_1.getCodeSectionComponents().getId().add(CS_L2_1_2.getId());

        CS_L1_2.getCodeSections().getCodeSection().add(CS_L2_2_1);
        CS_L1_2.getCodeSectionComponents().getId().add(CS_L2_2_2.getId());

        CS_BASE.setId(1);
        CS_BASE.setCodeSections(new XCodeSectionList());
        CS_BASE.setCodeSectionComponents(new XIdList());
        CS_BASE.setSearchStart("");
        CS_BASE.getCodeSections().getCodeSection().add(CS_L1_1);
        CS_BASE.getCodeSectionComponents().getId().add(CS_L1_2.getId());

        XCodeConversionFileType codeConversionFileType = new XCodeConversionFileType();
        codeConversionFileType.setCodeSections(new XCodeSectionList());
        codeConversionFileType.getCodeSections().getCodeSection().add(CS_BASE);
        List<XCodeConversionFileType> codeConversionFileTypeList = new LinkedList<XCodeConversionFileType>();
        codeConversionFileTypeList.add(codeConversionFileType);

        List<XCodeSection> codeSectionList = new LinkedList<XCodeSection>();
        codeSectionList.add(CS_L1_1);
        codeSectionList.add(CS_L1_2);
        codeSectionList.add(CS_L2_1_1);
        codeSectionList.add(CS_L2_1_2);
        codeSectionList.add(CS_L2_2_1);
        codeSectionList.add(CS_L2_2_2);

        when(codeConversionsLoader.getCodeConversionTypes()).thenReturn(codeConversionFileTypeList);
        when(codeConversionsLoader.getCodeSectionComponents()).thenReturn(codeSectionList);
    }

    @Test
    public void testLinkCodeSections() {
        TranslationsLoader translationsLoader = mock(TranslationsLoader.class);
        Executor executor = new Executor();
        executor.setTranslationsLoader(translationsLoader);
        executor.setCodeConversionsLoader(codeConversionsLoader);
        executor.linkAllCodeSectionComponents();

        assertEquals(2, CS_BASE.getCodeSections().getCodeSection().size());
        assertEquals(2, CS_L1_1.getCodeSections().getCodeSection().size());
        assertEquals(2, CS_L1_2.getCodeSections().getCodeSection().size());

        assertEquals(CS_L1_1, CS_BASE.getCodeSections().getCodeSection().get(0));
        assertEquals(CS_L1_2, CS_BASE.getCodeSections().getCodeSection().get(1));

        assertEquals(CS_L2_1_1, CS_L1_1.getCodeSections().getCodeSection().get(0));
        assertEquals(CS_L2_1_2, CS_L1_1.getCodeSections().getCodeSection().get(1));
    }

    @Test(expected = ExecutionException.class)
    public void testCyclic() {
        // cycle back to level one parent
        CS_L2_1_1.getCodeSectionComponents().getId().add(CS_L1_1.getId());

        TranslationsLoader translationsLoader = mock(TranslationsLoader.class);
        Executor executor = new Executor();
        executor.setTranslationsLoader(translationsLoader);
        executor.setCodeConversionsLoader(codeConversionsLoader);
        executor.linkAllCodeSectionComponents();
    }

}

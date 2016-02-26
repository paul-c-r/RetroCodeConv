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

import static org.mockito.Mockito.*;

/**
 * We just need to verify that the translation static is called from the code conversion.
 */

public class TestCodeTranslations {

    private static final String SEARCH_NAME_START = "123";
    private static final String SOURCE = "123";

    @Test
    public void testCodeTranslate() {
        final XIdList translationList = new XIdList();
        translationList.getId().add(1);
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setTranslations(translationList);
        codeSection.setSearchStart(SEARCH_NAME_START);
        TranslationsLoader transMock = mock(TranslationsLoader.class);
        Code code = new Code(transMock, new LinkedHashMap<Integer, StringBuilder>());
        Map<Integer, StringBuilder> result = new LinkedHashMap<Integer, StringBuilder>();
        code.convert(new StringBuilder(SOURCE), codeSection, result);
        verify(transMock, atLeastOnce()).getTranslationEntries(translationList.getId());
    }
}

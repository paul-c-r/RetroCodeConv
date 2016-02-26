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

package org.retro.code.converter.xml;

import org.junit.Test;
import org.retro.code.converter.xml.v1.types.XCodeConversionFileType;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XFileNameEntryList;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestCodeConversionLoader {
    @Test
    public void testLoader() {
        List<XCodeConversionFileType> codeConversionFileTypes = getCodeConversionList("settings/");

        assertEquals("Conversion", codeConversionFileTypes.get(0).getTitle());

        XCodeSection codeSection = codeConversionFileTypes.get(0).getCodeSections().getCodeSection().get(0);
        assertEquals("Property File with weird chars", codeSection.getTitle());
        assertEquals("com/\\.*()!\"%^&*()-=", codeSection.getSearchStart());
        assertEquals("//\\.\\{\\}\\(\\\\)", codeSection.getSearchEnd());
        assertEquals("public static final String ##_KEY_## = \"##_VALUE_##\";", codeSection.getOutputPreamble());

        assertEquals(1, codeSection.getTranslations().getId().get(0).intValue());
        assertEquals(55, codeSection.getTranslations().getId().get(1).intValue());
    }

    @Test(expected = Exception.class)
    public void testLoaderException() {
        getCodeConversionList("");
    }

    private List<XCodeConversionFileType> getCodeConversionList(String pathName) {
        XFileNameEntryList fileEntryList = new XFileNameEntryList();
        fileEntryList.getFileName().add("conv1.xml");
        TransformLoader transformLoader = new TransformLoader();
        transformLoader.setSettingsFolder(pathName);
        CodeConversionsLoader codeConversionsLoader = new CodeConversionsLoader();
        codeConversionsLoader.setTransformLoader(transformLoader);
        codeConversionsLoader.load(fileEntryList);
        return codeConversionsLoader.getCodeConversionTypes();
    }

}

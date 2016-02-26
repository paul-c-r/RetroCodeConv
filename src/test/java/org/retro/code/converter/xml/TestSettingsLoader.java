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
import org.retro.code.converter.exception.XMLMarshalException;
import org.retro.code.converter.xml.v1.types.XTransformFile;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestSettingsLoader {
    @Test
    public void testLoadingFromFolder() {
        TransformLoader transformLoader = new TransformLoader();
        transformLoader.setSettingsFolder("settings/");
        XTransformFile transformFile = transformLoader.load();

        assertEquals(true, transformFile.isOverwriteTargets());

        assertEquals("conv.jdbc.prop.xml\\", transformFile.getConversionFiles().getFileName().get(0));

        assertEquals("\\sample.sql.properties", transformFile.getSources().getFileItem().get(0).getFileName());
        assertEquals(1, transformFile.getSources().getFileItem().get(0).getId());

        assertEquals("\\SampleDAOSQL.java", transformFile.getTargets().getFileItem().get(0).getFileName());
        assertEquals(2, transformFile.getTargets().getFileItem().get(0).getId());

        assertEquals("\\/template.java.sql.txt", transformFile.getTemplateFiles().getFileItem().get(0).getFileName());
        assertEquals(3, transformFile.getTemplateFiles().getFileItem().get(0).getId());

        assertEquals("/trans.prop.sql.xml", transformFile.getTranslationFiles().getFileName().get(0));
        assertEquals("/trans.java.xml", transformFile.getTranslationFiles().getFileName().get(1));
    }

    @Test(expected = XMLMarshalException.class)
    public void testException() {
        TransformLoader transformLoader = new TransformLoader();
        // dont set the directory
        // will not find the file
        transformLoader.load();
    }
}

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

import org.retro.code.converter.utils.LogUtil;
import org.apache.log4j.Logger;
import org.retro.code.converter.exception.XMLMarshalException;
import org.retro.code.converter.xml.v1.types.XCodeConversionFileType;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XCodeSectionList;
import org.retro.code.converter.xml.v1.types.XFileNameEntryList;

import java.util.LinkedList;
import java.util.List;

/**
 * Loads the XML conversion files.
 * There are two types of conversion files that are loaded.
 * They have the element roots XCodeConversionFileType or XCodeSectionList.
 *
 * The XCodeConversionFileType is used by the RetroCodeConv to start the conversion process,
 * while XCodeSectionList is used by the former as components to use in the conversion.
 * In other words, code sections declared in a XCodeSectionList file are used by the code sections
 * declared in XCodeConversionFileType for the conversion (declared in the codeSectionComponents element).
 */
public class CodeConversionsLoader {

    private static final Logger logger = Logger.getLogger(CodeConversionsLoader.class);
    private List<XCodeConversionFileType> codeConversionTypes = new LinkedList<XCodeConversionFileType>();
    private List<XCodeSection> codeSectionList = new LinkedList<XCodeSection>();
    private TransformLoader transformLoader;

    /**
     * The code conversions constructor.
     */
    public CodeConversionsLoader() {
    }

    /**
     * Sets the settings loader.
     * @param transformLoader The loader set via a spring bean.
     */
    public void setTransformLoader(TransformLoader transformLoader) {
        this.transformLoader = transformLoader;
    }

    /**
     * Loads all the XML conversion files, which are declared in the setting file.
     *
     * @param fileNames The file names to load
     * @throws XMLMarshalException The wrapped Marshal exception.
     */
    public void load(XFileNameEntryList fileNames) throws XMLMarshalException {
        for (String fileName : fileNames.getFileName()) {
            Object conversionFile = XMLUtils.unmarshal(transformLoader.getSettingsFolder() +
                                                       fileName, XCodeConversionFileType.class);
            if (conversionFile instanceof XCodeConversionFileType) {
                codeConversionTypes.add((XCodeConversionFileType) conversionFile);
            }
            if (conversionFile instanceof XCodeSectionList) {
                for (XCodeSection codeSection : ((XCodeSectionList) conversionFile).getCodeSection()) {
                    codeSectionList.add(codeSection);
                }
            }
            LogUtil.debug(logger, "Loaded conversion file: " + transformLoader.getSettingsFolder() + fileName);
        }
    }

    /**
     * @return The preloaded conversion types.
     */
    public List<XCodeConversionFileType> getCodeConversionTypes() {
        return new LinkedList<XCodeConversionFileType>(codeConversionTypes);
    }

    /**
     * @return The preloaded conversion lists with code section to be used by conversion files.
     */
    public List<XCodeSection> getCodeSectionComponents() {
        return new LinkedList<XCodeSection>(codeSectionList);
    }
}

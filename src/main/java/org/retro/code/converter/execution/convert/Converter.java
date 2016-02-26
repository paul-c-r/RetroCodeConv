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

package org.retro.code.converter.execution.convert;

import org.apache.log4j.Logger;
import org.retro.code.converter.exception.CodeConversionException;
import org.retro.code.converter.execution.convert.types.Code;
import org.retro.code.converter.execution.convert.types.Properties;
import org.retro.code.converter.utils.LogUtil;
import org.retro.code.converter.xml.TranslationsLoader;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XFileContentsType;

import java.util.Map;

/**
 * This class is the main class responsible for the code conversion.
 */
public class Converter {
    private static final Logger logger = Logger.getLogger(Converter.class);
    public final TranslationsLoader translationsLoader;

    /**
     * The constructor set the translator.
     *
     * @param translationsLoader The loader that allows for the lookup of the translations.
     */
    public Converter(TranslationsLoader translationsLoader) {
        this.translationsLoader = translationsLoader;
    }

    /**
     * This function is called by the Executor after loading the configuration and source files.
     * It converts one piece of source code to many targets.
     *
     * @param source       The contents of the source file.
     * @param contentsType Either CODE or PROPERTIES {@link org.retro.code.converter.xml.v1.types.XFileContentsType}
     * @param codeSection  The code section configured in the conversion file
     *                     {@link org.retro.code.converter.xml.v1.types.XCodeConversionFileType}
     *                     {@link org.retro.code.converter.xml.v1.types.XCodeSectionList}
     * @param templates    The templates to be used for code insertion.
     * @param targetOutput The output map
     * @throws CodeConversionException Any exception thrown by the conversion process.
     */
    public void convert(StringBuilder source, XFileContentsType contentsType,
                        XCodeSection codeSection, Map<Integer, StringBuilder> templates,
                       Map<Integer, StringBuilder> targetOutput)
    throws CodeConversionException {

        if (contentsType == null) {
            throw new CodeConversionException(new Throwable("Null ContentsType"));
        }
        switch (contentsType) {
            case PROPERTIES:
                LogUtil.debug(logger, "Processing properties for " + codeSection.getTitle());
                Properties properties = new Properties(translationsLoader, templates);
                properties.convert(source, codeSection, targetOutput);
                break;

            case CODE:
                LogUtil.debug(logger, "Processing code for " + codeSection.getTitle());
                Code code = new Code(translationsLoader, templates);
                code.convert(source, codeSection, targetOutput);
                break;
        }
    }
}

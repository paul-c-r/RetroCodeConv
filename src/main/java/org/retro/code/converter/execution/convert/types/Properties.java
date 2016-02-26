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

import org.retro.code.converter.utils.*;
import org.retro.code.converter.xml.TranslationsLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.retro.code.converter.exception.CodeConversionException;
import org.retro.code.converter.xml.v1.types.XCodeSection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Converts property files.
 */
public class Properties {
    private static final Logger logger    = Logger.getLogger(Properties.class);
    public static final  String KEY       = "##_KEY_##";
    public static final  String VALUE     = "##_VALUE_##";
    public static final  String NEW_LINE  = "\n";
    public static final  String CR        = "\r";
    public static final  String LINE_JOIN = "\\";

    private final TranslationsLoader          translationsLoader;
    private final Map<Integer, StringBuilder> templates;

    /**
     * The constructor set the translator and templates.
     * @param translationsLoader The loader that allows for the lookup of the translations.
     * @param templates The templates to be used for the code sections.
     */
    public Properties(TranslationsLoader translationsLoader, Map<Integer, StringBuilder> templates) {
        this.translationsLoader = translationsLoader;
        this.templates = templates;
    }

    /**
     * This function processes multi-line properties as per the configuration.
     * It replaces the KEY and VALUE placeholders in the output format string with
     * the key and value from the matching properties.
     * Only the lines that satisfy the search are processed.
     * <p/>
     * Note: This function does not perform recursive processing (only one code section).
     *
     * @param source      The source with properties.
     * @param codeSection The code section from the XML conversion configuration.
     * @param targetMap   The output map
     */
    public void convert(StringBuilder source, XCodeSection codeSection,
                                              Map<Integer, StringBuilder> targetMap)
    throws CodeConversionException {
        LogUtil.trace(logger, "Converting PROPERTIES for code section " + codeSection.getTitle());
        CodeSectionUtils.verifyCodeSection(codeSection);
        String templatePre = TemplateUtils.getTemplatePreamble(codeSection, templates);
        String templatePost = TemplateUtils.getTemplatePostamble(codeSection, templates);

        StringBuilder output = new StringBuilder();
        LogUtil.trace(logger, "Pre-template [" + templatePre + "]");
        output.append(templatePre);

        Pattern p = Pattern.compile(codeSection.getSearchStart());
        for (StringBuilder line : getLines(source)) {
            if (!p.matcher(line).find()) {
                continue;
            }
            int equalsPos = line.indexOf("=");
            if (equalsPos > 0 && line.length() != equalsPos + 1) {
                String key = line.toString().substring(0, line.indexOf("="));
                String value = line.toString().substring(line.indexOf("=") + 1);
                String outLine = codeSection.getOutputPreamble().replace(KEY, key);
                outLine = outLine.replace(VALUE, value);
                LogUtil.trace(logger, "Key [" + key + "]");
                LogUtil.trace(logger, "Value [" + value + "]");
                Collection<Integer> ids;
                if (codeSection.getTranslations() == null) {
                    ids = new LinkedList<Integer>();
                } else {
                    ids = codeSection.getTranslations().getId();
                }
                final String translated = TranslationUtils.translate(outLine, translationsLoader.getTranslationEntries(ids));
                output.append(translated);
                output.append(NEW_LINE);
            }
        }
        LogUtil.trace(logger, "Post-template [" + templatePost + "]");
        output.append(templatePost);
        ConverterUtils.insertStringIntoOutput(targetMap, output, codeSection.getTargetFileId());
    }

    private Collection<StringBuilder> getLines(StringBuilder source) {
        Collection<StringBuilder> lines = new LinkedList<StringBuilder>();
        String[] parts = source.toString().split(NEW_LINE);
        StringBuilder newLine = new StringBuilder();
        for (String part : parts) {
            part = StringUtils.trim(part);
            part = StringUtils.removeEnd(part, CR);
            newLine.append(StringUtils.removeEnd(part, LINE_JOIN));
            if (part.length() == 0 || !part.endsWith(LINE_JOIN)) {
                LogUtil.trace(logger, "Extracting property line: " + newLine);
                lines.add(newLine);
                newLine = new StringBuilder();
            }
        }
        return lines;
    }
}

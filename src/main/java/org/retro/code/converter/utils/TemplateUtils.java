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

package org.retro.code.converter.utils;

import org.apache.log4j.Logger;
import org.retro.code.converter.xml.v1.types.XCodeSection;

import java.util.Map;

/**
 * This class wraps templates around code.
 */
public class TemplateUtils {
    private static final Logger logger = Logger.getLogger(TemplateUtils.class);
    public static final String CONTENTS_MARKER = "##_CONTENTS_##";

    /**
     * This function gets the first part of the template to add to the
     * code map as it is being created. It is added to the first position.
     *
     * @param codeSection The relative code section.
     * @param templates   All the templates loaded for this conversion.
     * @return The first part of the template, up to the content marker.
     *         If there is not content marker, then an empty string is returned.
     */
    public static String getTemplatePreamble(XCodeSection codeSection, Map<Integer, StringBuilder> templates) {
        if (codeSection.getTemplateId() == null || codeSection.getTemplateId() <= 0) {
            return "";
        }
        StringBuilder template = templates.get(codeSection.getTemplateId());
        if (template.indexOf(CONTENTS_MARKER) == -1) {
            return "";
        }
        return template.substring(0, template.indexOf(CONTENTS_MARKER));
    }

    /**
     * This function gets the end part of the template to add to the
     * code map as it is being created. It is added to the last position.
     *
     * @param codeSection The relative code section.
     * @param templates   All the templates loaded for this conversion.
     * @return The end part of the template, after the content marker.
     *         If there is not content marker, then an empty string is returned.
     */
    public static String getTemplatePostamble(XCodeSection codeSection, Map<Integer, StringBuilder> templates) {
        if (codeSection.getTemplateId() == null || codeSection.getTemplateId() <= 0) {
            return "";
        }
        StringBuilder template = templates.get(codeSection.getTemplateId());
        if (template.indexOf(CONTENTS_MARKER) == -1) {
            return "";
        }
        return template.substring(template.indexOf(CONTENTS_MARKER) + CONTENTS_MARKER.length());
    }

}

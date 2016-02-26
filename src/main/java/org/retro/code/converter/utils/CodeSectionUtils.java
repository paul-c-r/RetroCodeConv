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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.retro.code.converter.exception.CodeConversionException;
import org.retro.code.converter.xml.v1.types.XCodeBlockType;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XCodeSectionList;
import org.retro.code.converter.xml.v1.types.XIdList;

/**
 *  A class for preparing the code sections before the conversion.
 */
public class CodeSectionUtils {
    private static final Logger logger = Logger.getLogger(CodeSectionUtils.class);
    private static final String CODE_SECTION_INVALID = "No code section specified.";
    private static final String SEARCH_ERROR = "Search regex not specified.";
    private static final String ID_ERROR = "Zero or no ID specified for code section.";
    private static final String TARGET_ID_ERROR = "Target ID is invalid. Must be greater than 0.";
    private static final String TEMPLATE_ID_ERROR = "Template ID is invalid. Must be greater than 0.";
    private static final String CODE_SECTION_TITLE = "CODE_SECTION_";
    private static final String REMOVE_BRACKETS = "Remove Brackets only works for FUNCTION and STATEMENT code block types.";
    public static final String TITLE_MISSING = "Title for code section not specified. Creating title ";
    public static final String CODE_BLOCK_MISSING = "CodeBlock not specified. Default to AUTO for code section ";
    public static final String PROCESS_ONCE_MISSING = "ProcessOnce not specified. Default to TRUE for code section ";
    public static final String REMOVE_BRACKETS_MISSING = "RemoveBrackets not specified. Default to FALSE for code section ";
    public static final String REMOVE_SEARCH_MISSING = "RemoveSearch not specified. Default to FALSE for code section ";

    private static int CODE_SECTION_COUNTER = 1;

    /**
     * Ensures that all the fields of a code section are checked or specified
     * so that there are no errors during runtime.
     *
     * @param codeSection The code section to be checked.
     * @throws CodeConversionException In the case where required information is missing.
     */
    public static void verifyCodeSection(XCodeSection codeSection) throws CodeConversionException {
        if (codeSection == null) {
            throw new CodeConversionException(new Throwable(CODE_SECTION_INVALID));
        }
        if (codeSection.getId() == 0) {
            throw new CodeConversionException(new Throwable(ID_ERROR));
        }
        if (codeSection.getTargetFileId() <= 0) {
            throw new CodeConversionException(new Throwable(TARGET_ID_ERROR));
        }
        if (StringUtils.isBlank(codeSection.getSearchStart())) {
            throw new CodeConversionException(new Throwable(SEARCH_ERROR));
        }
        if (codeSection.getTemplateId() != null && codeSection.getTemplateId() <= 0) {
            throw new CodeConversionException(new Throwable(TEMPLATE_ID_ERROR));
        }
        if (StringUtils.isBlank(codeSection.getTitle())) {
            String title = CODE_SECTION_TITLE + CODE_SECTION_COUNTER++;
            logger.info(TITLE_MISSING + title);
            codeSection.setTitle(title);
        }
        if (codeSection.getCodeBlock() == null ||
            StringUtils.isBlank(codeSection.getCodeBlock().value())) {
            LogUtil.debug(logger, CODE_BLOCK_MISSING + codeSection.getTitle());
            codeSection.setCodeBlock(XCodeBlockType.AUTO);
        }
        if (codeSection.isProcessOnce() == null) {
            LogUtil.debug(logger, PROCESS_ONCE_MISSING + codeSection.getTitle());
            codeSection.setProcessOnce(true);
        }
        if (codeSection.isRemoveBrackets() == null) {
            LogUtil.debug(logger, REMOVE_BRACKETS_MISSING + codeSection.getTitle());
            codeSection.setRemoveBrackets(false);
        }
        if (codeSection.isRemoveSearch() == null) {
            LogUtil.debug(logger, REMOVE_SEARCH_MISSING + codeSection.getTitle());
            codeSection.setRemoveSearch(false);
        }
        if (codeSection.getSearchEnd() == null) {
            codeSection.setSearchEnd("");
        }
        if (codeSection.getOutputPreamble() == null) {
            codeSection.setOutputPreamble("");
        }
        if (codeSection.getOutputPostamble() == null) {
            codeSection.setOutputPostamble("");
        }
        if (codeSection.getTranslations() == null) {
            codeSection.setTranslations(new XIdList());
        }
        if (codeSection.getCodeSections() == null) {
            codeSection.setCodeSections(new XCodeSectionList());
        }
        if (codeSection.getCodeSectionComponents() == null) {
            codeSection.setCodeSectionComponents(new XIdList());
        }
        if (codeSection.getTargetOrder() == null) {
            codeSection.setTargetOrder(0); // Integer object - not just a normal int as it is an optional field
        }
        if (codeSection.isRemoveBrackets()) {
            if (codeSection.getCodeBlock() != XCodeBlockType.STATEMENT &&
                codeSection.getCodeBlock() != XCodeBlockType.FUNCTION) {
                logger.warn(REMOVE_BRACKETS + codeSection.getTitle());
            }
        }
    }
}

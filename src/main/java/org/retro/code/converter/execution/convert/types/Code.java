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

import org.retro.code.converter.utils.ConverterUtils;
import org.retro.code.converter.utils.LogUtil;
import org.retro.code.converter.xml.TranslationsLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.retro.code.converter.exception.CodeConversionException;
import org.retro.code.converter.execution.info.CodeBuffer;
import org.retro.code.converter.execution.info.CodeSectionInfo;
import org.retro.code.converter.utils.TemplateUtils;
import org.retro.code.converter.utils.TranslationUtils;
import org.retro.code.converter.xml.v1.types.XCodeSection;
import org.retro.code.converter.xml.v1.types.XIdList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts source code.
 */
public class Code {
    private static final Logger logger = Logger.getLogger(Code.class);
    public static final int SHORT_CODE_LENGTH = 150;
    private final TranslationsLoader translationsLoader;
    private final Map<Integer, StringBuilder> templates;

    /**
     * The constructor set the translator and templates.
     *
     * @param translationsLoader The loader that allows for the lookup of the translations.
     * @param templates          The templates to be used for the code sections.
     */
    public Code(TranslationsLoader translationsLoader, Map<Integer, StringBuilder> templates) {
        this.translationsLoader = translationsLoader;
        this.templates = templates;
    }

    /**
     * This function converts and translates code by applying code section information,
     * that is then used to keep state during the conversion process.
     *
     * @param source       The source code.
     * @param codeSection  The code section configured in the conversion xml file.
     * @param targetOutput The output map
     */
    public void convert(StringBuilder source, XCodeSection codeSection,
                       Map<Integer, StringBuilder> targetOutput)
    throws CodeConversionException {
        MutableInt level = new MutableInt(1);
        LogUtil.debug(logger, level.getValue(), "Converting CODE for code section " + codeSection.getTitle());
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        CodeBuffer codeBuffer = new CodeBuffer(source);
        Map<Integer, Map<Integer, String>> filePieces = new LinkedHashMap<Integer, Map<Integer, String>>();
        processContent(codeBuffer, codeSectionInfo, filePieces, level, 1);
        appendPostprocessedComplete(getResultCode(filePieces, codeSection), codeBuffer,
                                   codeSectionInfo.getCodeSection(), level);
        ConverterUtils.insertMapIntoOutput(targetOutput, filePieces);
    }

    private Map<Integer, String> getResultCode(Map<Integer, Map<Integer, String>> targetOutput, XCodeSection codeSection) {
        Map<Integer, String> sourceMap;
        if ((sourceMap = targetOutput.get(codeSection.getTargetFileId())) == null) {
            sourceMap = new LinkedHashMap<Integer, String>();
            targetOutput.put(codeSection.getTargetFileId(), sourceMap);
        }
        return sourceMap;
    }

    /*
     * This function is recursively called by every code section for its children.
     * The source code passed in here is a functional section (class, function, loop, etc)
     * and then this section is broken down into smaller sections for it's children.
     */
    private CodeBuffer processContent(CodeBuffer externalCode,
                                     CodeSectionInfo codeSectionInfo,
                                     Map<Integer, Map<Integer, String>> targetOutput,
                                     MutableInt level, int sequenceNo)
    throws CodeConversionException {
        CodeBuffer codeBuffer = new CodeBuffer(externalCode.getToBeProcessedCode());
        Pattern p = Pattern.compile(codeSectionInfo.getCodeSection().getSearchStart());
        while (true) {
            final String allCodeFromStartIndex = codeBuffer.getAllCodeFromStartIndex();
            if (StringUtils.isBlank(allCodeFromStartIndex)) {
                break;
            }
            Matcher matcher = p.matcher(allCodeFromStartIndex);
            LogUtil.trace(logger, level.getValue(), "Finding next [" + codeSectionInfo.getCodeSection().getSearchStart() + "]");
            LogUtil.trace(logger, level.getValue(), "In code " + LogUtil.getSingleLineOutput(allCodeFromStartIndex, SHORT_CODE_LENGTH));
            if (matcher.find()) {
                LogUtil.trace(logger, level.getValue(), "Found [" + allCodeFromStartIndex.substring(matcher.start(), matcher.end()) + "]");
                codeBuffer.appendStartIndex(matcher.start());
                appendPreprocessedCode(targetOutput, codeBuffer, codeSectionInfo, level, sequenceNo);
                ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, matcher.start(), matcher.end());
                appendCode(codeSectionInfo, codeBuffer, targetOutput, level, sequenceNo);
                if (codeSectionInfo.getCodeSection().isProcessOnce()) {
                    break;
                }
            } else {
                appendPreprocessedCode(targetOutput, codeBuffer, codeSectionInfo, level, sequenceNo);
                break;
            }
        }
        if (isLastCodeSection(codeSectionInfo, sequenceNo)) {
            XCodeSection codeSection = codeSectionInfo.getCodeSectionParent() == null ?
                                       codeSectionInfo.getCodeSection() : codeSectionInfo.getCodeSectionParent();
            appendPostprocessedComplete(getResultCode(targetOutput, codeSection),
                                       codeBuffer, codeSection, level);
        }
        return codeBuffer;
    }

    private boolean isFirstCodeSectionWithParent(CodeSectionInfo codeSectionInfo, int sequenceNo) {
        return codeSectionInfo.getCodeSectionParent() != null && sequenceNo == 1;
    }

    private boolean isLastCodeSection(CodeSectionInfo codeSectionInfo, int sequenceNo) {
        return codeSectionInfo.getCodeSectionParent() == null ||
               sequenceNo == codeSectionInfo.getCodeSectionParent().getCodeSections().getCodeSection().size();
    }

    private void appendCode(CodeSectionInfo codeSectionInfo, CodeBuffer codeBuffer,
                           Map<Integer, Map<Integer, String>> targetOutput,
                           MutableInt level, int parentSequenceNo) {

        String templatePre = TemplateUtils.getTemplatePreamble(codeSectionInfo.getCodeSection(), templates);
        String templatePost = TemplateUtils.getTemplatePostamble(codeSectionInfo.getCodeSection(), templates);
        Map<Integer, String> targetMap = getResultCode(targetOutput, codeSectionInfo.getCodeSection());

        LogUtil.trace(logger, level.getValue(), "Pre-template [" + templatePre + "]");
        ConverterUtils.insertCodeIntoResultMap(targetMap, templatePre, codeSectionInfo.getCodeSection().getTargetOrder());

        if (StringUtils.isNotBlank(codeSectionInfo.getCodeSection().getOutputPreamble())) {
            LogUtil.trace(logger, level.getValue(), "Preamble [" + codeSectionInfo.getCodeSection().getOutputPreamble() + "]");
            ConverterUtils.insertCodeIntoResultMap(targetMap, codeSectionInfo.getCodeSection().getOutputPreamble(),
                                                  codeSectionInfo.getCodeSection().getTargetOrder());
        }
        if (!codeSectionInfo.isContainer()) {
            translateAndAppend(targetMap, codeBuffer, codeSectionInfo.getCodeSection(), level);
        } else {
            int sequenceNo = 1;
            for (XCodeSection childSection : codeSectionInfo.getCodeSection().getCodeSections().getCodeSection()) {
                level.increment();
                CodeBuffer retStatus = processContent(codeBuffer,
                                                     new CodeSectionInfo(childSection,
                                                                        codeSectionInfo.getCodeSection()),
                                                     targetOutput, level, sequenceNo++);
                codeBuffer.appendStartIndex(retStatus.getEndIndex() + retStatus.getRemoveOffset());
                codeBuffer.setProcessedToStartIndex();
                level.decrement();
            }
            if (isLastCodeSection(codeSectionInfo, parentSequenceNo)) {
                XCodeSection codeSection = codeSectionInfo.getCodeSectionParent() == null ?
                                           codeSectionInfo.getCodeSection() : codeSectionInfo.getCodeSectionParent();
                appendPostprocessedComplete(getResultCode(targetOutput, codeSection),
                                           codeBuffer, codeSection, level);
            }
        }
        if (StringUtils.isNotBlank(codeSectionInfo.getCodeSection().getOutputPostamble())) {
            LogUtil.trace(logger, level.getValue(), "Postamble [" + codeSectionInfo.getCodeSection().getOutputPostamble() + "]");
            ConverterUtils.insertCodeIntoResultMap(targetMap, codeSectionInfo.getCodeSection().getOutputPostamble(),
                                                  codeSectionInfo.getCodeSection().getTargetOrder());
        }
        LogUtil.trace(logger, level.getValue(), "Post-template [" + templatePost + "]");
        ConverterUtils.insertCodeIntoResultMap(targetMap, templatePost, codeSectionInfo.getCodeSection().getTargetOrder());
    }

    private void appendPreprocessedCode(Map<Integer, Map<Integer, String>> targetOutput,
                                       CodeBuffer codeBuffer, CodeSectionInfo codeSectionInfo,
                                       MutableInt level, int sequenceNo) {
        Map<Integer, String> resultCode;
        XIdList translationList;
        final String translated;
        if (isFirstCodeSectionWithParent(codeSectionInfo, sequenceNo)) {
            translationList = codeSectionInfo.getCodeSectionParent().getTranslations();
            resultCode = getResultCode(targetOutput, codeSectionInfo.getCodeSectionParent());
            translated = translate(codeBuffer.getPreprocessedCode(), translationList);
            LogUtil.trace(logger, level.getValue(), "Pre-code [" + translated + "]");
            ConverterUtils.insertCodeIntoResultMap(resultCode, translated,
                                                  codeSectionInfo.getCodeSectionParent().getTargetOrder());
        } else {
            translationList = codeSectionInfo.getCodeSection().getTranslations();
            resultCode = getResultCode(targetOutput, codeSectionInfo.getCodeSection());
            translated = translate(codeBuffer.getPreprocessedCode(), translationList);
            LogUtil.trace(logger, level.getValue(), "Pre-code [" + translated + "]");
            ConverterUtils.insertCodeIntoResultMap(resultCode, translated,
                                                  codeSectionInfo.getCodeSection().getTargetOrder());
        }
        codeBuffer.setProcessedToStartIndex();
    }

    private void appendPostprocessedComplete(Map<Integer, String> resultCode, CodeBuffer codeBuffer,
                                            XCodeSection codeSection, MutableInt level) {
        String translated = translate(codeBuffer.getPostProcessedComplete(),
                                     codeSection.getTranslations());
        LogUtil.trace(logger, level.getValue(), "Complete [" + translated + "]");
        ConverterUtils.insertCodeIntoResultMap(resultCode, translated, codeSection.getTargetOrder());
        codeBuffer.close();
    }

    private void translateAndAppend(Map<Integer, String> resultCode, CodeBuffer codeBuffer,
                                   XCodeSection codeSection, MutableInt level)
    throws CodeConversionException {
        try {
            final String translated = translate(codeBuffer.getToBeProcessedCode(),
                                               codeSection.getTranslations());
            LogUtil.trace(logger, level.getValue(), "Translated [" + translated + "]");
            ConverterUtils.insertCodeIntoResultMap(resultCode, translated, codeSection.getTargetOrder());
            codeBuffer.setStartIndexToEnd();
            codeBuffer.setProcessedToStartIndex();
        } catch (Throwable throwable) {
            throw new CodeConversionException(throwable);
        }
    }

    private String translate(String code, XIdList translationList) throws CodeConversionException {
        Collection<Integer> ids;
        if (translationList == null) {
            ids = new LinkedList<Integer>();
        } else {
            ids = translationList.getId();
        }
        try {
            return TranslationUtils.translate(code, translationsLoader.getTranslationEntries(ids));
        } catch (Throwable throwable) {
            throw new CodeConversionException(throwable);
        }
    }
}

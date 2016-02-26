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
import org.retro.code.converter.execution.info.BracketInfo;
import org.retro.code.converter.execution.info.CodeBuffer;
import org.retro.code.converter.execution.info.CodeSectionInfo;
import org.retro.code.converter.xml.v1.types.XCodeBlockType;
import org.retro.code.converter.xml.v1.types.XCodeSection;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for processing code.
 */
public class ConverterUtils {
    private static final String LINE_END = "\n";
    private static final String SEMI_COLON = ";";
    private static final Logger logger = Logger.getLogger(ConverterUtils.class);

    /**
     * Get the name of the file from the path, which is then used to
     * substitute the CLASS_NAME placeholder in the template.
     *
     * @param filePath The full path with the filename.
     * @return The file name (class name).
     */
    public static String stripPathForClassName(String filePath) {
        final int lastSlash = filePath.lastIndexOf("/");
        if (lastSlash >= 0) {
            filePath = filePath.substring(lastSlash + 1);
        }
        final int lastDot = filePath.lastIndexOf(".");
        if (lastDot >= 0) {
            filePath = filePath.substring(0, lastDot);
        }
        return filePath;
    }

    /**
     * This function finds the first slot available for inserting the new piece of code into the
     * resultCode map. The map uses integers to keep the order of the code to be output to the target file.
     * When the order supplied is greater than one a gap is left (relative to the number supplied) so that
     * the next piece of code that has a zero order will be inserted before that.
     *
     * @param resultCode The result code map with ordered pieces of code
     * @param codePiece  The piece of code to insert
     * @param order      A positive number indicating the gap to leave in the sequencing of the new code.
     */
    public static void insertCodeIntoResultMap(Map<Integer, String> resultCode, String codePiece, int order) {
        int firstGap = 1;
        // there are template and output pieces of code added for every section
        // we therefore leave a nice big gap for all the components.
        order *= 100;
        while (resultCode.get(firstGap) != null) {
            firstGap++;
        }
        if (order <= 0) {
            resultCode.put(firstGap, codePiece);
            LogUtil.trace(logger, "Inserted into result map at index " + firstGap);
        } else {
            // insert it after the value we want
            while (resultCode.get(firstGap + order) != null) {
                firstGap++;
            }
            resultCode.put(firstGap + order, codePiece);
            LogUtil.trace(logger, "Inserting into result map at index " + (firstGap + order));
        }
    }

    /**
     * This function takes a map of pieces for a target file and groups the output
     * per file.
     *
     * @param targetOutput The grouped and end result of the conversion
     * @param targetPieces The little bits per file
     */
    public static void insertMapIntoOutput(Map<Integer, StringBuilder> targetOutput,
                                          Map<Integer, Map<Integer, String>> targetPieces) {
        for (Map.Entry<Integer, Map<Integer, String>> allPieces : targetPieces.entrySet()) {
            StringBuilder fileOutput = getOrderedResultCode(allPieces.getValue());
            StringBuilder currentContents;
            if ((currentContents = targetOutput.get(allPieces.getKey())) == null) {
                targetOutput.put(allPieces.getKey(), fileOutput);
            } else {
                currentContents.append(fileOutput);
            }
        }
    }

    /**
     * Inserts or appends to existing target contents
     * @param targetOutput The target file(s) output
     * @param fileOutput   Single output
     * @param index        Index in the target to insert into
     */
    public static void insertStringIntoOutput(Map<Integer, StringBuilder> targetOutput,
                                             StringBuilder fileOutput, int index) {
        StringBuilder currentContents;
        if ((currentContents = targetOutput.get(index)) == null) {
            targetOutput.put(index, fileOutput);
        } else {
            currentContents.append(fileOutput);
        }
    }

    /**
     * This function returns a string builder that includes all the code to be output for a certain
     * target. The code is ordered as per the order number for each code piece insertion into the list.
     *
     * @param resultCode The map which contains the pieces of code and their order number.
     * @return The ordered resulting code.
     */
    private static StringBuilder getOrderedResultCode(Map<Integer, String> resultCode) {
        StringBuilder output = new StringBuilder();
        Map<Integer, String> treeMap = new TreeMap<Integer, String>(resultCode);
        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            output.append(entry.getValue());
        }
        return output;
    }


    /**
     * This function cuts out the piece of code depending on the code block configuration.
     *
     * @param codeSectionInfo This is the information built around the code (like a helper class)
     * @param codeBuffer      The code buffer to update
     */
    public static void extractCodeBlock(CodeSectionInfo codeSectionInfo, CodeBuffer codeBuffer,
                                       int searchStartIndex, int searchEndIndex) {
        BracketInfo bracketCodeInfo;
        BracketInfo bracketSearchInfo;
        if (codeSectionInfo.getCodeSection().isRemoveSearch()) {
            LogUtil.trace(logger, "Removing search regex from string");
            codeBuffer.appendStartIndex(searchEndIndex - searchStartIndex);
            codeBuffer.setProcessedToStartIndex();
        }
        if (codeSectionInfo.getCodeSection().getCodeBlock() == null ||
            codeSectionInfo.getCodeSection().getCodeBlock() == XCodeBlockType.AUTO) {
            LogUtil.trace(logger, "Processing code block: AUTO");
            bracketCodeInfo = new BracketInfo(codeBuffer.getAllCodeFromStartIndex());
            bracketSearchInfo = new BracketInfo(codeSectionInfo.getCodeSection().getSearchStart());
            int codeBarrierIndex;
            if (StringUtils.isNotBlank(codeSectionInfo.getCodeSection().getSearchEnd())) {
                codeBarrierIndex = getSearchEndIndex(bracketCodeInfo.getCode(), codeSectionInfo);
                if (codeBarrierIndex != -1) {
                    codeBuffer.setEndIndex(codeBuffer.getStartIndex() + codeBarrierIndex);
                    return;
                }
            } else if (bracketSearchInfo.getLeadingBracketType() != 0) {
                int index = getIndexFromLeadingToCorrespondingClosed(bracketSearchInfo, bracketCodeInfo);
                codeBuffer.setEndIndex(codeBuffer.getStartIndex() + index);
                return;
            }
        } else if (codeSectionInfo.getCodeSection().getCodeBlock() == XCodeBlockType.SEARCH_END) {
            LogUtil.trace(logger, "Processing code block: SEARCH_END");
            bracketCodeInfo = new BracketInfo(codeBuffer.getAllCodeFromStartIndex());
            if (StringUtils.isNotBlank(codeSectionInfo.getCodeSection().getSearchEnd())) {
                int codeBarrierIndex = getSearchEndIndex(bracketCodeInfo.getCode(), codeSectionInfo);
                if (codeBarrierIndex != -1) {
                    codeBuffer.setEndIndex(codeBuffer.getStartIndex() + codeBarrierIndex);
                    return;
                }
            }
        } else if (codeSectionInfo.getCodeSection().getCodeBlock() == XCodeBlockType.STATEMENT) {
            LogUtil.trace(logger, "Processing code block: STATEMENT");
            bracketCodeInfo = new BracketInfo(codeBuffer.getAllCodeFromStartIndex());
            if (bracketCodeInfo.getIndexOfClosedRound() != -1) {
                codeBuffer.setEndIndex(codeBuffer.getStartIndex() + bracketCodeInfo.getIndexOfClosedRound());
            }
            removeBrackets(bracketCodeInfo, codeBuffer, codeSectionInfo.getCodeSection(), BracketInfo.ROUND_BRACKET_OPEN);
            return;
        } else if (codeSectionInfo.getCodeSection().getCodeBlock() == XCodeBlockType.FUNCTION) {
            LogUtil.trace(logger, "Processing code block: FUNCTION");
            bracketCodeInfo = new BracketInfo(codeBuffer.getAllCodeFromStartIndex());
            if (bracketCodeInfo.getIndexOfClosedCurly() != -1) {
                codeBuffer.setEndIndex(codeBuffer.getStartIndex() + bracketCodeInfo.getIndexOfClosedCurly());
            }
            removeBrackets(bracketCodeInfo, codeBuffer, codeSectionInfo.getCodeSection(), BracketInfo.CURLY_BRACKET_OPEN);
            return;
        } else if (codeSectionInfo.getCodeSection().getCodeBlock() == XCodeBlockType.COMMAND) {
            LogUtil.trace(logger, "Processing code block: COMMAND");
            bracketCodeInfo = new BracketInfo(codeBuffer.getAllCodeFromStartIndex());
            int codeBarrierIndex = getEndIndex(bracketCodeInfo.getCode(), SEMI_COLON);
            if (codeBarrierIndex != -1) {
                codeBuffer.setEndIndex(codeBuffer.getStartIndex() + codeBarrierIndex);
                return;
            }
        } else if (codeSectionInfo.getCodeSection().getCodeBlock() == XCodeBlockType.LINE_END) {
            LogUtil.trace(logger, "Processing code block: LINE_END");
            bracketCodeInfo = new BracketInfo(codeBuffer.getAllCodeFromStartIndex());
            int codeBarrierIndex = getEndIndex(bracketCodeInfo.getCode(), LINE_END);
            if (codeBarrierIndex != -1) {
                codeBuffer.setEndIndex(codeBuffer.getStartIndex() + codeBarrierIndex);
                return;
            }
        }
        LogUtil.trace(logger, "Processing code block: ALL");
        // return the full buffer.
        codeBuffer.setEndIndex(codeBuffer.getCode().length());
    }

    private static void removeBrackets(BracketInfo bracketInfo, CodeBuffer codeBuffer,
                                      XCodeSection codeSection, char bracket) {
        if (codeSection.isRemoveBrackets()) {
            int firstBracket = 0;
            int lastBracket = 0;
            if (bracket == BracketInfo.CURLY_BRACKET_OPEN) {
                if (bracketInfo.getIndexOfOpenCurly() == -1 ||
                    bracketInfo.getIndexOfClosedCurly() == -1) {
                    return;
                }
                firstBracket = bracketInfo.getIndexOfOpenCurly();
                lastBracket = bracketInfo.getIndexOfClosedCurly();
            } else {
                if (bracketInfo.getIndexOfOpenRound() == -1 ||
                    bracketInfo.getIndexOfClosedRound() == -1) {
                    return;
                }
                firstBracket = bracketInfo.getIndexOfOpenRound();
                lastBracket = bracketInfo.getIndexOfClosedRound();
            }
            codeBuffer.getCode().replace(codeBuffer.getStartIndex() + lastBracket - 1,
                                        codeBuffer.getStartIndex() + lastBracket, "");
            codeBuffer.getCode().replace(codeBuffer.getStartIndex() + firstBracket,
                                        codeBuffer.getStartIndex() + firstBracket + 1, "");
            codeBuffer.setRemoveOffset(2);
            codeBuffer.setEndIndex(codeBuffer.getEndIndex() - codeBuffer.getRemoveOffset());
        }
    }

    private static int getIndexFromLeadingToCorrespondingClosed(BracketInfo bracketSearchInfo,
                                                               BracketInfo bracketCodeInfo) {
        char open = bracketSearchInfo.getLeadingBracketType();
        char close = bracketSearchInfo.getLeadingClosedBracketType();
        int bracketCount = bracketSearchInfo.getLeadingBracketCount();
        return getIndexWithBrackets(bracketCodeInfo.getCode(), open, close, bracketCount);
    }

    private static int getIndexWithBrackets(String code, char open, char close, int searchBracketCount) {
        int currentIndex = 0;

        for (char ch : code.toCharArray()) {
            if (ch == open) {
                searchBracketCount++;
            }
            if (ch == close && searchBracketCount > 0) {
                searchBracketCount--;
                if (searchBracketCount == 0) {
                    return currentIndex + 1;
                }
            }
            currentIndex++;
        }
        return currentIndex;
    }

    /**
     * This function indents the code and removes many new lines
     *
     * @param code The code to reformat
     * @return the reformatted code
     */
    public static StringBuilder formatCode(StringBuilder code) {
        int bracketLevel = 0;
        String indentSpace = "    ";
        StringBuilder output = new StringBuilder();
        boolean spacing = false;
        boolean insertedSpaces = false;
        int nlCount = 0;

        for (char ch : code.toString().toCharArray()) {
            if (spacing) {
                if (ch == ' ' || ch == '\t') {
                    continue;
                }
                if (ch == '\n') {
                    if (nlCount++ > 2) {
                        continue;
                    }
                    insertedSpaces = true;
                }
                if (!insertedSpaces) {
                    for (int i = 0; i < bracketLevel; i++) {
                        output.append(indentSpace);
                    }
                    insertedSpaces = true;
                    nlCount = 0;
                }
                if (ch == BracketInfo.CURLY_BRACKET_CLOSE) {
                    if (output.toString().endsWith(indentSpace)) {
                        output.replace(output.length() - indentSpace.length(),
                                      output.length(), "");
                    }
                }
                spacing = false;
                output.append(ch);
            } else {
                output.append(ch);
            }
            if (ch == BracketInfo.CURLY_BRACKET_CLOSE && bracketLevel > 0) {
                bracketLevel--;
            }
            if (ch == BracketInfo.CURLY_BRACKET_OPEN) {
                bracketLevel++;
            }
            if (ch == '\n') {
                spacing = true;
                insertedSpaces = false;
                nlCount++;
            }
        }
        return output;
    }

    private static int getSearchEndIndex(String source, CodeSectionInfo codeSectionInfo) {
        if (codeSectionInfo.getCodeSection().getSearchEnd().isEmpty()) {
            return -1;
        }
        return getEndIndex(source, codeSectionInfo.getCodeSection().getSearchEnd());
    }

    private static int getEndIndex(String source, String searchFor) {
        Pattern p = Pattern.compile(searchFor);
        Matcher matcher = p.matcher(source);
        if (matcher.find()) {
            return matcher.end();
        } else {
            return -1;
        }
    }
}

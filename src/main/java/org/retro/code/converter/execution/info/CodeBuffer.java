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

package org.retro.code.converter.execution.info;

import org.retro.code.converter.exception.CodeConversionException;
import org.retro.code.converter.utils.LogUtil;
import org.apache.log4j.Logger;
import org.retro.code.converter.execution.convert.types.Code;

/**
 * Holds a piece of code to be processed and the indexes to the processing.
 */
public class CodeBuffer {
    private static Logger logger = Logger.getLogger(CodeBuffer.class);
    public static final String SOURCE_INDEX_ERROR = "Source index out of bounds. ";

    private StringBuilder code;
    private int processedIndex;
    private int startIndex;
    private int endIndex;
    private int removeOffset;

    /**
     * This class is used by {@link Code} to keep state.
     *
     * @param code The piece of code to work with
     */
    public CodeBuffer(StringBuilder code) {
        setCode(code);
    }

    /**
     * This class is used by {@link Code} to keep state.
     *
     * @param code The piece of code to work with
     */
    public CodeBuffer(String code) {
        setCode(new StringBuilder(code));
    }

    private void setCode(StringBuilder code) {
        this.code = code;
        this.startIndex = 0;
        this.processedIndex = 0;
        this.endIndex = code.length();
        this.removeOffset = 0;
        showIndexLogTrace("setCode");
    }

    /**
     * Get the code string builder.
     *
     * @return the code
     */
    public StringBuilder getCode() {
        return code;
    }

    /**
     * The current index of the search regex in the code.
     *
     * @throws CodeConversionException
     */
    public void setProcessedToStartIndex() throws CodeConversionException {
        processedIndex = startIndex;
        showIndexLogTrace("setProcessedToStart");
    }

    /**
     * Increments the start index by a given offset.
     *
     * @param offset the number to increment by
     * @throws CodeConversionException
     */
    public void appendStartIndex(int offset) throws CodeConversionException {
        checkIndex(startIndex + offset);
        this.startIndex += offset;
        if (startIndex > endIndex) {
            this.endIndex = offset;
        }
        showIndexLogTrace("appendStart");
    }

    /**
     * Setting the start index to the end indicates that the next search
     * can be processed on the code.
     *
     * @throws CodeConversionException
     */
    public void setStartIndexToEnd() throws CodeConversionException {
        this.startIndex = endIndex;
        showIndexLogTrace("setStartToEnd");
    }

    /**
     * Retrieves the start index.
     *
     * @return The current search location.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Retrieves the end index.
     *
     * @return The current end of the code section, which starts with the search.
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Sets the end index, which indicates the end of the code section to process.
     *
     * @param index The index to set the end to.
     */
    public void setEndIndex(int index) {
        checkIndex(index);
        this.endIndex = index;
        showIndexLogTrace("setEnd");
    }

    /**
     * The amount of bytes removed from the code for brackets.
     * It is used in the parent for offsetting the buffer.
     *
     * @return Normally 0 or 2.
     */
    public int getRemoveOffset() {
        return removeOffset;
    }

    /**
     * Sets the number of characters removed from the code.
     *
     * @param removeOffset Normally 0 or 2.
     */
    public void setRemoveOffset(int removeOffset) {
        this.removeOffset = removeOffset;
    }

    /**
     * This evaluates to the code between the start and end indexes.
     *
     * @return The code that starts at the search index and ends at the
     *         computed end of the code section.
     */
    public String getToBeProcessedCode() {
        String retString = "";
        if (startIndex < endIndex) {
            retString = code.substring(startIndex, endIndex);
        }
        return retString;
    }

    /**
     * This is the code between the code section end and the end of the full source code.
     * This is used after all the searches or recursive processing have completed.
     *
     * @return The code after the end of the current section.
     */
    public String getPostProcessedComplete() {
        String retString = "";
        if (endIndex < code.length()) {
            retString = code.substring(endIndex);
        }
        return retString;
    }

    /**
     * @return The code between the processed index and the start index.
     */
    public String getPreprocessedCode() {
        String retString = "";
        if (processedIndex < startIndex) {
            retString = code.substring(processedIndex, startIndex);
        }
        return retString;
    }

    /**
     * @return Returns all the code after the start index (the current search index)
     */
    public String getAllCodeFromStartIndex() {
        return code.substring(startIndex);
    }

    /**
     * Closes the code processing on this buffer.
     */
    public void close() {
        LogUtil.trace(logger, "Closing buffer with state");
        showIndexLogTrace("Before close");

        startIndex = code.length();
        endIndex = code.length();
        processedIndex = code.length();

        LogUtil.trace(logger, "Buffer closed with state");
        showIndexLogTrace("After close");
    }

    private void checkIndex(int index) {
        if (index < 0 || index > code.length()) {
            throw new CodeConversionException(new Throwable(SOURCE_INDEX_ERROR + index + " : " + code.length()));
        }
    }

    private void showIndexLogTrace(String index) {
        LogUtil.trace(logger, (index + ": Start,Processed,end,offset: " + startIndex +
                                  " " + processedIndex + " " + endIndex + " " + removeOffset));
    }

}

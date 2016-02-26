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

import org.springframework.util.StringUtils;

/**
 * This class holds the bracket information about a piece of code.
 * Brackets in this system include both rounded and curly () & {}
 */

public class BracketInfo {
    public static final char CURLY_BRACKET_OPEN = '{';
    public static final char CURLY_BRACKET_CLOSE = '}';
    public static final char ROUND_BRACKET_OPEN = '(';
    public static final char ROUND_BRACKET_CLOSE = ')';

    private String code;
    private int countOpenCurlyBrackets = 0;
    private int countClosedCurlyBrackets = 0;
    private int countOpenRoundBrackets = 0;
    private int countClosedRoundBrackets = 0;

    private int indexOfOpenCurly = -1;
    private int indexOfClosedCurly = -1;
    private int indexOfOpenRound = -1;
    private int indexOfClosedRound = -1;

    private char leadingBracketType = 0;
    private int leadingBracketCount = 0;

    /**
     * The constructor that takes a piece of code and analyses the brackets.
     *
     * @param code The piece of code to analyse
     */
    public BracketInfo(String code) {
        setCode(code);
    }

    public void setCode(String code) {
        this.code = code;
        analyse();
    }

    private void analyse() {
        countOpenCurlyBrackets = StringUtils.countOccurrencesOf(code, Character.toString(CURLY_BRACKET_OPEN));
        countClosedCurlyBrackets = StringUtils.countOccurrencesOf(code, Character.toString(CURLY_BRACKET_CLOSE));
        countOpenRoundBrackets = StringUtils.countOccurrencesOf(code, Character.toString(ROUND_BRACKET_OPEN));
        countClosedRoundBrackets = StringUtils.countOccurrencesOf(code, Character.toString(ROUND_BRACKET_CLOSE));
        updateCodeBlockIndex(ROUND_BRACKET_OPEN, ROUND_BRACKET_CLOSE);
        updateCodeBlockIndex(CURLY_BRACKET_OPEN, CURLY_BRACKET_CLOSE);

        if (countOpenCurlyBrackets == countOpenRoundBrackets) {
            if (countOpenCurlyBrackets == countClosedCurlyBrackets &&
                countOpenRoundBrackets > countClosedRoundBrackets) {
                leadingBracketType = BracketInfo.ROUND_BRACKET_OPEN;
                leadingBracketCount = countOpenRoundBrackets - countClosedRoundBrackets;
            } else if (countOpenCurlyBrackets > countClosedCurlyBrackets &&
                       countOpenRoundBrackets == countClosedRoundBrackets) {
                leadingBracketType = BracketInfo.CURLY_BRACKET_OPEN;
                leadingBracketCount = countOpenCurlyBrackets - countClosedCurlyBrackets;
            } else {
                setFirstOccurrenceBracket();
            }
        } else {
            if (countOpenCurlyBrackets == countClosedCurlyBrackets) {
                if (countOpenRoundBrackets > countClosedRoundBrackets) {
                    leadingBracketType = BracketInfo.ROUND_BRACKET_OPEN;
                    leadingBracketCount = countOpenRoundBrackets - countClosedRoundBrackets;
                }
            } else {
                if (countOpenCurlyBrackets > countClosedCurlyBrackets) {
                    if (countOpenRoundBrackets > countClosedRoundBrackets) {
                        setFirstOccurrenceBracket();
                    } else {
                        leadingBracketType = BracketInfo.CURLY_BRACKET_OPEN;
                        leadingBracketCount = countOpenCurlyBrackets - countClosedCurlyBrackets;
                    }
                } else {
                    if (countOpenRoundBrackets > countClosedRoundBrackets) {
                        leadingBracketType = BracketInfo.ROUND_BRACKET_OPEN;
                        leadingBracketCount = countOpenRoundBrackets - countClosedRoundBrackets;
                    }
                }
            }
        }
    }

    private void updateCodeBlockIndex(char open, char close) {
        int currentIndex = 0;
        int bracketCount = 0;

        for (char ch : code.toCharArray()) {
            if (ch == open) {
                bracketCount++;
                if (bracketCount == 1) {
                    if (open == CURLY_BRACKET_OPEN) {
                        indexOfOpenCurly = currentIndex;
                    } else {
                        indexOfOpenRound = currentIndex;
                    }
                }
            }
            if (ch == close && bracketCount > 0) {
                bracketCount--;
                if (bracketCount == 0) {
                    if (open == CURLY_BRACKET_OPEN) {
                        indexOfClosedCurly = currentIndex + 1;
                    } else {
                        indexOfClosedRound = currentIndex + 1;
                    }
                    break;
                }
            }
            currentIndex++;
        }
    }


    /**
     * The code that the bracket info is based on.
     *
     * @return The code passed in the constructor.
     */
    public String getCode() {
        return code;
    }

    /**
     * The first instance of an open curly bracket
     *
     * @return the zero based index
     */
    public int getIndexOfOpenCurly() {
        return indexOfOpenCurly;
    }

    /**
     * The bracket that corresponds to the opening curly.
     * This includes the processing of all intermediate brackets.
     *
     * @return the zero based index
     */
    public int getIndexOfClosedCurly() {
        return indexOfClosedCurly;
    }

    /**
     * The first instance of an open round bracket
     *
     * @return the zero based index
     */
    public int getIndexOfOpenRound() {
        return indexOfOpenRound;
    }

    /**
     * The bracket that corresponds to the opening round.
     * This includes the processing of all intermediate brackets.
     *
     * @return the zero based index
     */
    public int getIndexOfClosedRound() {
        return indexOfClosedRound;
    }

    /**
     * The leading open bracket that does not have a paired closing bracket.
     * Used for the search start to calculate the end of the code section.
     *
     * @return The leading open bracket.
     */
    public char getLeadingBracketType() {
        return leadingBracketType;
    }

    /**
     * Gets the number of open brackets that are not matched by closing ones.
     * Used to calculate the end of the code section.
     *
     * @return The number of open-ended brackets.
     */
    public int getLeadingBracketCount() {
        return leadingBracketCount;
    }

    /**
     * Gets the closing bracket type for the open one.
     */
    public char getLeadingClosedBracketType() {
        if (leadingBracketType == BracketInfo.CURLY_BRACKET_OPEN) {
            return BracketInfo.CURLY_BRACKET_CLOSE;
        }
        if (leadingBracketType == BracketInfo.ROUND_BRACKET_OPEN) {
            return BracketInfo.ROUND_BRACKET_CLOSE;
        }
        return 0;
    }

    /**
     * Total count of the open curly brackets
     *
     * @return integer number
     */
    public int getCountOpenCurlyBrackets() {
        return countOpenCurlyBrackets;
    }

    /**
     * Total count of the closed curly brackets
     *
     * @return integer number
     */
    public int getCountClosedCurlyBrackets() {
        return countClosedCurlyBrackets;
    }

    /**
     * Total count of the open round brackets
     *
     * @return integer number
     */
    public int getCountOpenRoundBrackets() {
        return countOpenRoundBrackets;
    }

    /**
     * Total count of the closed round brackets
     *
     * @return integer number
     */
    public int getCountClosedRoundBrackets() {
        return countClosedRoundBrackets;
    }

    private void setFirstOccurrenceBracket() {
        if (code.indexOf(BracketInfo.CURLY_BRACKET_OPEN) <
            code.indexOf(BracketInfo.ROUND_BRACKET_OPEN)) {
            leadingBracketType = BracketInfo.CURLY_BRACKET_OPEN;
            leadingBracketCount = countOpenCurlyBrackets - countClosedCurlyBrackets;
        }
        if (code.indexOf(BracketInfo.CURLY_BRACKET_OPEN) >
            code.indexOf(BracketInfo.ROUND_BRACKET_OPEN)) {
            leadingBracketType = BracketInfo.ROUND_BRACKET_OPEN;
            leadingBracketCount = countOpenRoundBrackets - countClosedRoundBrackets;
        }
    }
}

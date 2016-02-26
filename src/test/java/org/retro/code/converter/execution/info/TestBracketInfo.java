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


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestBracketInfo {

    private static final String SEARCH_START_BRACKETS1 = "Test {}";
    private static final int EXPECTED1_OPENING_INDEX = 5;
    private static final int EXPECTED1_CLOSING_INDEX = 7;
    private static final char EXPECTED1_LEADING_CHAR = 0;
    private static final int EXPECTED1_LEADING_COUNT = 0;

    private static final String SEARCH_START_BRACKETS2 = "Test {{}";
    private static final char EXPECTED2_LEADING_CHAR = '{';
    private static final int EXPECTED2_LEADING_COUNT = 1;

    private static final String SEARCH_START_BRACKETS3 = "Test() {{}";
    private static final char EXPECTED3_LEADING_CHAR = '{';
    private static final int EXPECTED3_LEADING_COUNT = 1;

    private static final String SEARCH_START_BRACKETS4 = "Test()( {{}";
    private static final char EXPECTED4_LEADING_CHAR = '(';
    private static final int EXPECTED4_LEADING_COUNT = 1;

    private static final String SEARCH_START_BRACKETS5 = "Test() {{{}";
    private static final char EXPECTED5_LEADING_CHAR = '{';
    private static final int EXPECTED5_LEADING_COUNT = 2;
    private static final int EXPECTED5_OPENING_CURLY_INDEX = 7;
    private static final int EXPECTED5_CLOSING_CURLY_INDEX = -1;
    private static final int EXPECTED5_OPENING_ROUND_INDEX = 4;
    private static final int EXPECTED5_CLOSING_ROUND_INDEX = 6;

    private static final String SEARCH_START_BRACKETS6 = "{Test(() {";
    private static final char EXPECTED6_LEADING_CHAR = '{';
    private static final int EXPECTED6_LEADING_COUNT = 2;

    private static final String SEARCH_START_BRACKETS7 = "(Test{{} (";
    private static final char EXPECTED7_LEADING_CHAR = '(';
    private static final int EXPECTED7_LEADING_COUNT = 2;

    private static final String SEARCH_START_BRACKETS8 = "Test(";
    private static final char EXPECTED8_LEADING_CHAR = '(';
    private static final int EXPECTED8_LEADING_COUNT = 1;

    private static final String SEARCH_START_BRACKETS9 = "Test(1,2,3) { statement(){{}} }";
    private static final char EXPECTED9_LEADING_CHAR = 0;
    private static final int EXPECTED9_LEADING_COUNT = 0;
    private static final int EXPECTED9_OPENING_CURLY_INDEX = 12;
    private static final int EXPECTED9_CLOSING_CURLY_INDEX = SEARCH_START_BRACKETS9.length();
    private static final int EXPECTED9_OPENING_ROUND_INDEX = 4;
    private static final int EXPECTED9_CLOSED_ROUND_INDEX = 11;
    private static final int EXPECTED9_ROUND_OPEN_COUNT = 2;
    private static final int EXPECTED9_CURLY_OPEN_COUNT = 3;
    private static final int EXPECTED9_ROUND_CLOSED_COUNT = 2;
    private static final int EXPECTED9_CURLY_CLOSED_COUNT = 3;

    @Test
    public void testBracketSearchCounts1() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS1);
        assertEquals(EXPECTED1_OPENING_INDEX, bracketInfo.getIndexOfOpenCurly());
        assertEquals(EXPECTED1_CLOSING_INDEX, bracketInfo.getIndexOfClosedCurly());
        assertEquals(EXPECTED1_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
        assertEquals(EXPECTED1_LEADING_CHAR, bracketInfo.getLeadingBracketType());
    }

    @Test
    public void testBracketSearchCounts2() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS2);
        assertEquals(EXPECTED2_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED2_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts3() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS3);
        assertEquals(EXPECTED3_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED3_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts4() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS4);
        assertEquals(EXPECTED4_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED4_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts5() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS5);
        assertEquals(EXPECTED5_OPENING_CURLY_INDEX, bracketInfo.getIndexOfOpenCurly());
        assertEquals(EXPECTED5_OPENING_ROUND_INDEX, bracketInfo.getIndexOfOpenRound());
        assertEquals(EXPECTED5_CLOSING_CURLY_INDEX, bracketInfo.getIndexOfClosedCurly());
        assertEquals(EXPECTED5_CLOSING_ROUND_INDEX, bracketInfo.getIndexOfClosedRound());
        assertEquals(EXPECTED5_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED5_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts6() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS6);
        assertEquals(EXPECTED6_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED6_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts7() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS7);
        assertEquals(EXPECTED7_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED7_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts8() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS8);
        assertEquals(EXPECTED8_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED8_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }

    @Test
    public void testBracketSearchCounts9() {
        BracketInfo bracketInfo = new BracketInfo(SEARCH_START_BRACKETS9);
        assertEquals(EXPECTED9_OPENING_CURLY_INDEX, bracketInfo.getIndexOfOpenCurly());
        assertEquals(EXPECTED9_OPENING_ROUND_INDEX, bracketInfo.getIndexOfOpenRound());
        assertEquals(EXPECTED9_CLOSED_ROUND_INDEX, bracketInfo.getIndexOfClosedRound());
        assertEquals(EXPECTED9_CLOSING_CURLY_INDEX, bracketInfo.getIndexOfClosedCurly());
        assertEquals(EXPECTED9_CURLY_OPEN_COUNT, bracketInfo.getCountOpenCurlyBrackets());
        assertEquals(EXPECTED9_ROUND_OPEN_COUNT, bracketInfo.getCountOpenRoundBrackets());
        assertEquals(EXPECTED9_CURLY_CLOSED_COUNT, bracketInfo.getCountClosedCurlyBrackets());
        assertEquals(EXPECTED9_ROUND_CLOSED_COUNT, bracketInfo.getCountClosedRoundBrackets());
        assertEquals(EXPECTED9_LEADING_CHAR, bracketInfo.getLeadingBracketType());
        assertEquals(EXPECTED9_LEADING_COUNT, bracketInfo.getLeadingBracketCount());
    }
}

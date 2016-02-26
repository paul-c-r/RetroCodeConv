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

import org.junit.BeforeClass;
import org.junit.Test;
import org.retro.code.converter.execution.info.BracketInfo;
import org.retro.code.converter.execution.info.CodeBuffer;
import org.retro.code.converter.execution.info.CodeSectionInfo;
import org.retro.code.converter.xml.v1.types.XCodeBlockType;
import org.retro.code.converter.xml.v1.types.XCodeSection;

import static org.junit.Assert.assertEquals;

/**
 * REGEX reserved chars are <([{\^-=$!|]})?*+.>
 */
public class TestConverterUtils {

    private static final String CODE_1 = "class { 123 } ";
    private static final String CODE_2 = "function() { 123 } ";
    private static final String CODE_3 = "for (i=0;i<10;i++) { 123 } ";
    private static final String CODE_4 = "int field1 = 123; ";
    private static final String SEARCH_AUTO_1 = "class \\{";
    private static final String SEARCH_AUTO_2 = "function\\(\\) \\{";
    private static final String SEARCH_END_2 = "\\{";
    private static final String SEARCH_3 = "for";
    private static final String SEARCH_4 = "int";
    private static final int SEARCH_INDEX_RESULT_2 = 4;
    private static final int SEARCH_INDEX_RESULT_3 = 15;

    private static final String FUNCTION = "function";
    private static final String CLASS = "class";
    private static final BracketInfo BRACKET_INFO_SEARCH_1 =
    new BracketInfo(CODE_1.substring(SEARCH_AUTO_1.replaceAll("\\\\", "").length()));

    private static final StringBuilder INDENT_CODE = new StringBuilder();
    private static final StringBuilder EXPECTED_INDENT = new StringBuilder();

    @BeforeClass
    public static void initIndent() {
        INDENT_CODE.append("import com.package.class;\n\n");
        INDENT_CODE.append("public class ClassName {\n\n\n\n\n");
        INDENT_CODE.append("         \t    public void function() {\n");
        INDENT_CODE.append("  int oddIndent1 = 1;\n");
        INDENT_CODE.append("                int oddIndent2 = 1;\n");
        INDENT_CODE.append("  int oddIndent3 = 1;\n\n\n");
        INDENT_CODE.append("         while (true) {\n\n");
        INDENT_CODE.append("\tint oddIndent4 = 1;\n");
        INDENT_CODE.append("    \toddIndent4 = oddIndent1;\n");
        INDENT_CODE.append("            if (oddIndent4 == 1)\n");
        INDENT_CODE.append("       {\n");
        INDENT_CODE.append("break;\n\n\n");
        INDENT_CODE.append("                 }\n");
        INDENT_CODE.append("       }\n");
        INDENT_CODE.append("}\n");
        INDENT_CODE.append("         }\n");

        EXPECTED_INDENT.append("import com.package.class;\n\n");
        EXPECTED_INDENT.append("public class ClassName {\n\n");
        EXPECTED_INDENT.append("    public void function() {\n");
        EXPECTED_INDENT.append("        int oddIndent1 = 1;\n");
        EXPECTED_INDENT.append("        int oddIndent2 = 1;\n");
        EXPECTED_INDENT.append("        int oddIndent3 = 1;\n\n");
        EXPECTED_INDENT.append("        while (true) {\n\n");
        EXPECTED_INDENT.append("            int oddIndent4 = 1;\n");
        EXPECTED_INDENT.append("            oddIndent4 = oddIndent1;\n");
        EXPECTED_INDENT.append("            if (oddIndent4 == 1)\n");
        EXPECTED_INDENT.append("            {\n");
        EXPECTED_INDENT.append("                break;\n\n");
        EXPECTED_INDENT.append("            }\n");
        EXPECTED_INDENT.append("        }\n");
        EXPECTED_INDENT.append("    }\n");
        EXPECTED_INDENT.append("}\n");
    }

    @Test
    public void testEndIndexAUTO1() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.AUTO);
        codeSection.setSearchStart(SEARCH_AUTO_1);
        codeSection.setRemoveSearch(true);
        codeSection.setProcessOnce(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_1);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, BRACKET_INFO_SEARCH_1.getCode().length());
        assertEquals(CODE_1.substring(SEARCH_AUTO_1.replaceAll("\\\\", "").length()).length() - 1,
                    codeBuffer.getEndIndex() - codeBuffer.getStartIndex());
    }

    @Test
    public void testEndIndexAUTO2() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.AUTO);
        codeSection.setSearchStart(SEARCH_AUTO_2);
        codeSection.setRemoveSearch(true);
        codeSection.setProcessOnce(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_2);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, SEARCH_AUTO_2.replaceAll("\\\\", "").length());
        assertEquals(CODE_2.substring(SEARCH_AUTO_2.replaceAll("\\\\", "").length()).length() - 1,
                    codeBuffer.getEndIndex() - codeBuffer.getStartIndex());
    }

    @Test
    public void testEndIndexSEARCH_END() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.SEARCH_END);
        codeSection.setSearchStart(FUNCTION);
        codeSection.setSearchEnd(SEARCH_END_2);
        codeSection.setRemoveSearch(true);
        codeSection.setProcessOnce(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_2);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, FUNCTION.length());
        assertEquals(SEARCH_INDEX_RESULT_2, codeBuffer.getEndIndex() - codeBuffer.getStartIndex());
    }

    @Test
    public void testEndIndexCLASS() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.FUNCTION);
        codeSection.setSearchStart(CLASS);
        codeSection.setProcessOnce(true);
        codeSection.setRemoveSearch(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_1);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, CLASS.length());
        assertEquals(CODE_1.substring(CLASS.length()).length() - 1,
                    codeBuffer.getEndIndex() - codeBuffer.getStartIndex());
    }

    @Test
    public void testEndIndexFUNCTION() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.FUNCTION);
        codeSection.setSearchStart(FUNCTION);
        codeSection.setRemoveSearch(true);
        codeSection.setProcessOnce(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_2);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, FUNCTION.length());
        assertEquals(CODE_2.substring(FUNCTION.length()).length() - 1,
                    codeBuffer.getEndIndex() - codeBuffer.getStartIndex());
    }

    @Test
    public void testEndIndexSTATEMENT() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.STATEMENT);
        codeSection.setSearchStart(SEARCH_3);
        codeSection.setRemoveSearch(true);
        codeSection.setProcessOnce(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_3);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, SEARCH_3.length());
        assertEquals(SEARCH_INDEX_RESULT_3, codeBuffer.getEndIndex() - codeBuffer.getStartIndex());
    }

    @Test
    public void testEndIndexCOMMAND() {
        XCodeSection codeSection = new XCodeSection();
        codeSection.setId(1);
        codeSection.setTargetFileId(1);
        codeSection.setCodeBlock(XCodeBlockType.COMMAND);
        codeSection.setSearchStart(SEARCH_4);
        codeSection.setRemoveSearch(true);
        codeSection.setProcessOnce(true);
        CodeSectionInfo codeSectionInfo = new CodeSectionInfo(codeSection);
        final CodeBuffer codeBuffer = new CodeBuffer(CODE_4);
        ConverterUtils.extractCodeBlock(codeSectionInfo, codeBuffer, 0, SEARCH_4.length());
        assertEquals(CODE_4.substring(SEARCH_4.length()).length() - 1,
                    codeBuffer.getEndIndex() - codeBuffer.getStartIndex());

    }

    @Test
    public void testIndent() {
        assertEquals(EXPECTED_INDENT.toString(), ConverterUtils.formatCode(INDENT_CODE).toString());
    }
}

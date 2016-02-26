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
import org.retro.code.converter.utils.CodeSectionUtils;
import org.retro.code.converter.xml.v1.types.XCodeSection;

/**
 * This class holds the parent and container information for a code section.
 */

public class CodeSectionInfo {
    private XCodeSection codeSection;
    private XCodeSection codeSectionParent;
    private boolean isContainer = false;


    /**
     * Constructor for code section and parent.
     *
     * @param codeSection       The code section
     * @param codeSectionParent The code section's parent
     * @throws CodeConversionException
     */
    public CodeSectionInfo(XCodeSection codeSection, XCodeSection codeSectionParent) throws CodeConversionException {
        this(codeSection);
        this.codeSectionParent = codeSectionParent;
    }

    /**
     * Initialises the code section info with a code section.
     *
     * @param codeSection The relevant code section.
     * @throws CodeConversionException
     */
    public CodeSectionInfo(XCodeSection codeSection) throws CodeConversionException {
        CodeSectionUtils.verifyCodeSection(codeSection);
        this.codeSection = codeSection;
        this.codeSectionParent = null;
        if (codeSection.getCodeSections().getCodeSection().size() > 0) {
            isContainer = true;
        }
    }

    /**
     * @return The code section used during initialisation.
     */
    public XCodeSection getCodeSection() {
        return codeSection;
    }

    /**
     * @return True if this code section has children.
     */
    public boolean isContainer() {
        return isContainer;
    }

    /**
     * @return The parent of the current code section.
     */
    public XCodeSection getCodeSectionParent() {
        return codeSectionParent;
    }
}

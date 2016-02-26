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

package org.retro.code.converter.execution;


import org.apache.log4j.Logger;
import org.retro.code.converter.exception.ExecutionException;
import org.retro.code.converter.exception.TranslationIdException;
import org.retro.code.converter.execution.convert.Converter;
import org.retro.code.converter.utils.*;
import org.retro.code.converter.xml.CodeConversionsLoader;
import org.retro.code.converter.xml.TransformLoader;
import org.retro.code.converter.xml.TranslationsLoader;
import org.retro.code.converter.xml.v1.types.*;

import java.util.*;

/**
 * The methods in this class are called after the settings file is loaded.
 */
public class Executor {
    private static       Logger log                       = Logger.getLogger(Executor.class);
    private static final String INVALID_SOURCE_ID         = "Source not found with id ";
    private static final String INVALID_TEMPLATE_ID       = "Template not found with id ";
    private static final String INVALID_TARGET_ID         = "Target not found with id ";
    private static final String CLASS_NAME                = "##_CLASS_NAME_##";
    private static final String DUPLICATE_CODE_SECTION_ID = "Duplicate code section id ";
    private static final String INVALID_CODE_SECTION_ID   = "Invalid code section id ";
    private static final String CYCLIC_CODE_SECTION_ID    = "Circular or duplicate code section id ";

    private TranslationsLoader    translationsLoader;
    private CodeConversionsLoader codeConversionsLoader;
    private TransformLoader       transformLoader;

    /**
     * The executor constructor.
     */
    public Executor() {
    }

    /**
     * Set the translationsLoader.
     * @param translationsLoader The loader to be set.
     */
    public void setTranslationsLoader(TranslationsLoader translationsLoader) {
        this.translationsLoader = translationsLoader;
    }

    /**
     * Sets the conversions loader.
     * @param codeConversionsLoader The loader to be set.
     */
    public void setCodeConversionsLoader(CodeConversionsLoader codeConversionsLoader) {
        this.codeConversionsLoader = codeConversionsLoader;
    }

    /**
     * Sets the settings loader.
     * @param transformLoader The loader to be set.
     */
    public void setTransformLoader(TransformLoader transformLoader) {
        this.transformLoader = transformLoader;
    }

    /**
     * This loads the conversion and translations file after the transform.xml file has been loaded.
     * It also executes the actual conversion.
     *
     * @param transformFile The settings file contents, that are used to load the
     *                         relevant conversion and translation files.
     */
    public void processSettingsAndExecute(XTransformFile transformFile) throws ExecutionException {
        codeConversionsLoader.load(transformFile.getConversionFiles());
        translationsLoader.load(transformFile.getTranslationFiles());
        execute(transformFile);
    }

    /**
     * Once the translations and conversions are loaded then this function performs the other
     * required actions for completion of the process. This includes the following:
     * 1. Reading of the source files (either code or properties files to be converted)
     * 2. Conversion of the code as per the code sections.
     * 3. Inclusion of templates and processing of translations.
     *
     * @param transformFile The settings file contents
     * @throws ExecutionException
     *          Any exception thrown by the loading mechanism;
     *          this includes the config, source, translation files and target contents.
     */
    private void execute(XTransformFile transformFile) throws ExecutionException {
        verifyAllIds(transformFile);
        linkAllCodeSectionComponents();

        Map<Integer, StringBuilder> sourceContents = FileReaderUtils.getSourceFileListContents(transformFile.getSources());
        final XFileEntryList templateFiles = transformFile.getTemplateFiles();
        for (XFileEntry file : templateFiles.getFileItem()) {
            file.setFileName(transformLoader.getSettingsFolder() + file.getFileName());
        }
        Map<Integer, StringBuilder> templateContents = FileReaderUtils.getFileListContents(templateFiles);
        Map<Integer, StringBuilder> targetOutput = new LinkedHashMap<Integer, StringBuilder>();

        for (XCodeConversionFileType entry : codeConversionsLoader.getCodeConversionTypes()) {
            StringBuilder source = sourceContents.get(entry.getSourceFileId());
            for (XCodeSection codeSection : entry.getCodeSections().getCodeSection()) {
                log.debug("Processing code section " + codeSection.getTitle());
                XFileContentsType fileContentsType = getFileContentsType(transformFile, entry.getSourceFileId());
                Converter converter = new Converter(translationsLoader);
                converter.convert(source, fileContentsType, codeSection, templateContents, targetOutput);
            }
        }

        // Insert the class name from the file name
        // Clean up the placeholders
        for (Map.Entry<Integer, StringBuilder> target : targetOutput.entrySet()) {
            int id = target.getKey();
            String targetFileName = findFileEntry(transformFile.getTargets().getFileItem(), id).getFileName();
            int classNameIndex;
            while ((classNameIndex = target.getValue().indexOf(CLASS_NAME)) != -1) {
                target.getValue().replace(classNameIndex, classNameIndex + CLASS_NAME.length(),
                                         ConverterUtils.stripPathForClassName(targetFileName));
            }
            // remove the contents marker as it is no longer required
            int contentsIndex = target.getValue().indexOf(TemplateUtils.CONTENTS_MARKER);
            if (contentsIndex != -1) {
                target.getValue().replace(contentsIndex, contentsIndex + TemplateUtils.CONTENTS_MARKER.length(), "");
            }
            FileWriteUtils.writeContents(targetFileName, ConverterUtils.formatCode(target.getValue()),
                                         transformFile.isOverwriteTargets());
        }
    }

    /**
     * This checks the IDs used in the settings and config files.
     * Includes the following:
     * 1. The source files
     * 2. The target files
     * 3. The templates
     * 4. The translation entries
     * 5. The code sections
     *
     * @param transformFile The settings file
     * @throws ExecutionException
     *          Thrown if an ID is not valid or declared
     */
    private void verifyAllIds(XTransformFile transformFile)
    throws ExecutionException, TranslationIdException {

        for (XFileEntry fileEntry : transformFile.getTargets().getFileItem()) {
            if (fileEntry.getId() <= 0) {
                throw new ExecutionException(new Throwable(INVALID_TARGET_ID + fileEntry.getId()));
            }
        }
        for (XFileEntry fileEntry : transformFile.getTemplateFiles().getFileItem()) {
            if (fileEntry.getId() <= 0) {
                throw new ExecutionException(new Throwable(INVALID_TEMPLATE_ID + fileEntry.getId()));
            }
        }
        Set<Integer> allCodeSections = new LinkedHashSet<Integer>();
        for (XCodeSection codeSection : codeConversionsLoader.getCodeSectionComponents()) {
            CodeSectionUtils.verifyCodeSection(codeSection);
            addCodeSectionRecursiveIds(allCodeSections, codeSection);
        }
        for (XCodeConversionFileType entry : codeConversionsLoader.getCodeConversionTypes()) {
            for (XCodeSection codeSection : entry.getCodeSections().getCodeSection()) {
                CodeSectionUtils.verifyCodeSection(codeSection);
                addCodeSectionRecursiveIds(allCodeSections, codeSection);
            }
        }
        for (XCodeConversionFileType entry : codeConversionsLoader.getCodeConversionTypes()) {
            if (findFileEntry(transformFile.getSources().getFileItem(), entry.getSourceFileId()) == null) {
                throw new ExecutionException(new Throwable(INVALID_SOURCE_ID + entry.getSourceFileId()));
            }
            for (XCodeSection codeSection : entry.getCodeSections().getCodeSection()) {
                CodeSectionUtils.verifyCodeSection(codeSection);
                checkRecursiveCodeSections(transformFile, codeSection, allCodeSections);
            }
        }
    }

    private void checkRecursiveCodeSections(XTransformFile transformFile,
                                           XCodeSection codeSection,
                                           Set<Integer> allCodeSections) throws ExecutionException {

        if (findFileEntry(transformFile.getTargets().getFileItem(), codeSection.getTargetFileId()) == null) {
            throw new ExecutionException(new Throwable(INVALID_TARGET_ID + codeSection.getTargetFileId()));
        }
        if (codeSection.getTemplateId() != null) {
            if (findFileEntry(transformFile.getTemplateFiles().getFileItem(), codeSection.getTemplateId()) == null) {
                throw new ExecutionException(new Throwable(INVALID_TEMPLATE_ID + codeSection.getTemplateId()));
            }
        }
        if (!allCodeSections.containsAll(codeSection.getCodeSectionComponents().getId())) {
            Collection<Integer> unknownIDs = new LinkedList<Integer>(codeSection.getCodeSectionComponents().getId());
            unknownIDs.removeAll(allCodeSections);
            String idString = "";
            for (int id : unknownIDs) {
                idString += id + " ";
            }
            throw new ExecutionException(new Throwable(INVALID_CODE_SECTION_ID + idString));
        }
        // this will throw an exception if any of the IDs are invalid
        translationsLoader.getTranslationEntries(codeSection.getTranslations().getId());
        if (codeSection.getCodeSections().getCodeSection() != null) {
            for (XCodeSection child : codeSection.getCodeSections().getCodeSection()) {
                CodeSectionUtils.verifyCodeSection(child);
                checkRecursiveCodeSections(transformFile, child, allCodeSections);
            }
        }
    }

    private void addCodeSectionRecursiveIds(Set<Integer> allIds, XCodeSection codeSection) {
        if (!allIds.add(codeSection.getId())) {
            throw new ExecutionException(new Throwable(DUPLICATE_CODE_SECTION_ID + codeSection.getId()));
        }
        if (codeSection.getCodeSections().getCodeSection() != null) {
            for (XCodeSection child : codeSection.getCodeSections().getCodeSection()) {
                CodeSectionUtils.verifyCodeSection(child);
                addCodeSectionRecursiveIds(allIds, child);
            }
        }
    }

    private XFileContentsType getFileContentsType(XTransformFile transformFile, int id)
    throws ExecutionException {
        XFileSourceEntry fileSourceEntry = (XFileSourceEntry) findFileEntry(transformFile.getSources().getFileItem(), id);
        if (fileSourceEntry != null) {
            return fileSourceEntry.getContentsType();
        }
        throw new ExecutionException(new Throwable(INVALID_SOURCE_ID + id));
    }

    private XFileEntry findFileEntry(Collection<? extends XFileEntry> fileEntries, int id) {
        for (XFileEntry fileEntry : fileEntries) {
            if (fileEntry.getId() == id) {
                return fileEntry;
            }
        }
        return null;
    }

    /**
     * Adds all the stateless code section components to the code sections via
     * their referenced IDs.
     */
    public void linkAllCodeSectionComponents() {
        for (XCodeConversionFileType fileType : codeConversionsLoader.getCodeConversionTypes()) {
            for (XCodeSection codeSection : fileType.getCodeSections().getCodeSection()) {
                processCodeSectionComponents(codeSection, new LinkedHashSet<Integer>());
            }
        }
    }

    private void processCodeSectionComponents(XCodeSection codeSection, Set<Integer> circularCheck) {
        XCodeSection csComponent;
        for (Integer id : codeSection.getCodeSectionComponents().getId()) {
            Set<Integer> branchState = new LinkedHashSet<Integer>(circularCheck);
            checkCircular(id, circularCheck, codeSection.getTitle());
            if ((csComponent = getCodeSectionComponent(id, circularCheck)) != null) {
                // just linking, as it is stateless
                codeSection.getCodeSections().getCodeSection().add(csComponent);
            }
            circularCheck.clear();
            circularCheck.addAll(branchState);
        }
        if (codeSection.getCodeSections().getCodeSection() != null) {
            for (XCodeSection child : codeSection.getCodeSections().getCodeSection()) {
                processCodeSectionComponents(child, circularCheck);
            }
            if (codeSection.getCodeSections().getCodeSection().size() == 0) {
                circularCheck.clear();
            }
        } else {
            circularCheck.clear();
        }
    }

    /*
         * We only look at the first level, but all code sections can be put at the first
         * level and then used as children by other code sections to build hierarchy.
         */
    private XCodeSection getCodeSectionComponent(int id, Set<Integer> circularCheck) {
        for (XCodeSection codeSection : codeConversionsLoader.getCodeSectionComponents()) {
            if (codeSection.getId() == id) {
                processComponent(codeSection, circularCheck);
                return codeSection;
            }
        }
        return null;
    }

    /*
        we make sure all the components are linked for the component IDs declared in the
        code sections.
         */
    private void processComponent(XCodeSection codeSection, Set<Integer> circularCheck) {
        for (XCodeSection child : codeSection.getCodeSections().getCodeSection()) {
            processCodeSectionComponents(child, circularCheck);
        }
        for (XCodeSection child : codeSection.getCodeSections().getCodeSection()) {
            processComponent(child, circularCheck);
        }
    }

    private void checkCircular(int id, Set<Integer> circularCheck, String title) {
        if (!circularCheck.add(id)) {
            String idString = "";
            for (int id1 : circularCheck) {
                idString += id1 + " ";
            }
            throw new ExecutionException(new Throwable(CYCLIC_CODE_SECTION_ID + idString +
                                                       " from code section " + title));
        }
    }

}

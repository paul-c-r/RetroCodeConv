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

package org.retro.code.converter.xml;

import org.apache.log4j.Logger;
import org.retro.code.converter.exception.TranslationIdException;
import org.retro.code.converter.exception.XMLMarshalException;
import org.retro.code.converter.utils.LogUtil;
import org.retro.code.converter.xml.v1.types.XFileNameEntryList;
import org.retro.code.converter.xml.v1.types.XTranslateEntry;
import org.retro.code.converter.xml.v1.types.XTranslationFileType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The class responsible for loading all translations.
 */
public class TranslationsLoader {
    private static final Logger logger = Logger.getLogger(TranslationsLoader.class);
    public static final String INVALID_TRANSLATION_ID = "Translation not found for id(s): ";

    private final List<XTranslationFileType> translationTypes = new LinkedList<XTranslationFileType>();
    private TransformLoader transformLoader;

    public TranslationsLoader() {
    }

    public void setTransformLoader(TransformLoader transformLoader) {
        this.transformLoader = transformLoader;
    }

    /**
     * Loads the translation files specified in the settings xml file.
     *
     * @param fileNames A list of file names
     * @throws XMLMarshalException A wrapped marshal error.
     */
    public void load(XFileNameEntryList fileNames) throws XMLMarshalException {
        for (String name : fileNames.getFileName()) {
            final XTranslationFileType translationFileType = (XTranslationFileType) XMLUtils.unmarshal(transformLoader.getSettingsFolder() +
                                                                                                       name, XTranslationFileType.class);
            translationTypes.add(translationFileType);
            LogUtil.debug(logger, "Loaded translation file " + transformLoader.getSettingsFolder() + name);
            for (XTranslateEntry entry : translationFileType.getTranslations().getTranslateItem()) {
                LogUtil.trace(logger, "Loaded translation ID=" + entry.getId() + " : " + entry.getFromRegex());
            }
        }
    }

    /**
     * This function looks up the IDs specified and checks to see if they are
     * declared in the translation files.
     * <p/>
     * Note: The actual IDs in the translation files need to be unique across the files.
     *
     * @param ids The IDs specified in the configured code section
     * @return The list of translation entries to be used in this code section.
     *         The IDs may span across many files.
     * @throws TranslationIdException if the ID is not declared in any of the files.
     */
    public Collection<XTranslateEntry> getTranslationEntries(Collection<Integer> ids)
    throws TranslationIdException {
        Collection<XTranslateEntry> translateEntries = new LinkedList<XTranslateEntry>();
        if (ids != null) {
            List<Integer> allIDs = new LinkedList<Integer>();
            for (XTranslationFileType translationFileType : translationTypes) {
                for (XTranslateEntry translateEntry : translationFileType.getTranslations().getTranslateItem()) {
                    allIDs.add(translateEntry.getId());
                    // set defaults on toString and processOnce
                    if (translateEntry.getToString() == null) {
                        translateEntry.setToString("");
                    }
                    if (translateEntry.isProcessOnce() == null) {
                        translateEntry.setProcessOnce(true);
                    }
                }
            }
            if (!allIDs.containsAll(ids)) {
                Collection<Integer> unknownIDs = new LinkedList<Integer>(ids);
                unknownIDs.removeAll(allIDs);
                String idString = "";
                for (int id : unknownIDs) {
                    idString += id + " ";
                }
                throw new TranslationIdException(new Throwable(INVALID_TRANSLATION_ID + idString));
            }
            for (int id  : ids) {
                for (XTranslationFileType translationFileType : translationTypes) {
                    for (XTranslateEntry translateEntry : translationFileType.getTranslations().getTranslateItem()) {
                        if (translateEntry.getId() == id) {
                            translateEntries.add(translateEntry);
                            break;
                        }
                    }
                }
            }
        }
        return translateEntries;
    }

}

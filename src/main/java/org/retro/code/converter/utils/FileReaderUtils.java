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

import org.retro.code.converter.exception.FileException;
import org.retro.code.converter.xml.v1.types.XFileEntry;
import org.retro.code.converter.xml.v1.types.XFileEntryList;
import org.retro.code.converter.xml.v1.types.XFileSourceEntryList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilities for reading files
 */
public class FileReaderUtils {

    /**
     * Reads all the files in the supplied file list and then stores them in
     * a map for retrieval.
     *
     * @param fileEntryList The list to load.
     * @return The map of IDs and file contents.
     * @throws FileException
     */
    public static Map<Integer, StringBuilder> getFileListContents(XFileEntryList fileEntryList) throws FileException {
        return getFileContentMap(fileEntryList.getFileItem());
    }

    /**
     * Reads all the source files in the supplied file list and then stores them in
     * a map for retrieval.
     *
     * @param fileEntryList The list to load.
     * @return The map of IDs and file contents.
     * @throws FileException
     */
    public static Map<Integer, StringBuilder> getSourceFileListContents(XFileSourceEntryList fileEntryList)
    throws FileException {
        return getFileContentMap(fileEntryList.getFileItem());
    }

    private static Map<Integer, StringBuilder> getFileContentMap(Collection<? extends XFileEntry> fileEntryList) {
        Map<Integer, StringBuilder> map = new LinkedHashMap<Integer, StringBuilder>();
        for (XFileEntry fileEntry : fileEntryList) {
            map.put(fileEntry.getId(), readFile(fileEntry.getFileName()));
        }
        return map;
    }

    private static StringBuilder readFile(String fileName) throws FileException {
        if (fileName == null) {
            throw new FileException(new IOException());
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader br;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((sCurrentLine = br.readLine()) != null) {
                builder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            throw new FileException(e);
        }
        try {
            br.close();
        } catch (IOException ex) {
            throw new FileException(ex);
        }
        return builder;
    }

}

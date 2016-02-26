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

import org.apache.log4j.Logger;
import org.retro.code.converter.exception.FileException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilities for writing target output files.
 */
public class FileWriteUtils {

    private static final Logger logger = Logger.getLogger(FileWriteUtils.class);

    /**
     * Writes the contents to a file, creating the path if required.
     *
     * @param fileName          The file path and name to write to.
     * @param contents          The contents to write to.
     * @param overwriteContents Overwrites the contents of the file if it already exists.
     *                          If this is false and the file exists, then a FileException is thrown.
     * @throws FileException When an IOException is thrown or there is an overwrite issue.
     */
    public static void writeContents(String fileName, StringBuilder contents, boolean overwriteContents)
    throws FileException {
        try {
            if (fileName.lastIndexOf("/") > 0) {
                File path = new File(fileName.substring(0, fileName.lastIndexOf("/")));
                if (!path.mkdirs()) {
                    logger.info("Folder " + path + " already exists. No need to create.");
                }
            }
            File file = new File(fileName);
            if (!overwriteContents && file.exists()) {
                throw new FileException(new Exception());
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter;
            bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.write(contents.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

}

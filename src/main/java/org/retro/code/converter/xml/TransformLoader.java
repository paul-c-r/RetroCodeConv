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

import org.retro.code.converter.aspects.ExecutorAspect;
import org.retro.code.converter.exception.XMLMarshalException;
import org.retro.code.converter.utils.LogUtil;
import org.retro.code.converter.xml.v1.types.XTransformFile;

import org.apache.log4j.Logger;


/**
 * This class is responsible for loading the XML settings for a conversion.
 */
public class TransformLoader {

    public static final  String SETTINGS_XML   = "transform.xml";
    private static final Logger logger         = Logger.getLogger(TransformLoader.class);
    private              String settingsFolder = "";

    /**
     * This sets the path for the settings file.
     * The same path is used for all the configuration files, in other words,
     * the conversion and translation files specified in the settings files are
     * read from this path.
     * This is set by the arguments passed to the application.
     *
     * @param pathName The path to set.
     */
    public void setSettingsFolder(String pathName) {
        String path = "";
        if (pathName != null) {
            path = pathName;
            path = path.replaceAll("\\\\", "/");
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (!path.endsWith("/")) {
                path += "/";
            }
        }
        settingsFolder = path;
        LogUtil.debug(logger, "Settings path: " + path);
    }

    /**
     * Loads the settings XML file and returns the settings.
     * The settings are then used by the ExecutorAspect to load the XML
     * conversion and translation files.
     * <p/>
     * Reference {@link ExecutorAspect}
     *
     * @return The XML settings structure
     * @throws XMLMarshalException
     */
    public XTransformFile load() {
        LogUtil.debug(logger, "Unmarshaling settingsFolder " + SETTINGS_XML);
        XTransformFile transformFile = (XTransformFile) XMLUtils.unmarshal(settingsFolder +
                                                                                    SETTINGS_XML,
                                                                                   XTransformFile.class);
        LogUtil.debug(logger, "Unmarshaling complete");
        return transformFile;
    }

    /**
     * The settings folder set above.
     *
     * @return The linux version of the path with forward slashes.
     */
    public String getSettingsFolder() {
        return settingsFolder;
    }
}

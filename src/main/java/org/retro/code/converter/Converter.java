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

package org.retro.code.converter;

import org.retro.code.converter.xml.TransformLoader;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The entry point for the application.
 */
public class Converter {
    /**
     * This is the main function for the application.
     * The only argument it takes is the configuration directory.
     * All the configuration files declared in the settings files are loaded from this directory,
     * with the exception of the source and target files, which are loaded from the current
     * or absolute directories.
     *
     * @param args The application arguments.
     */
    private static final Logger logger = Logger.getLogger(Converter.class);

    public static void main(String[] args) {
        String pathToSetting = ".";
        if (args.length != 1) {
            logger.warn("Usage: java -jar retro-code-conv.jar Converter path/to/transform.xml");
            logger.warn("Trying current directory ...");
        } else {
            pathToSetting = args[0];
        }
        ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("application-context.xml");
        TransformLoader transformLoader = (TransformLoader) applicationContext.getBean("settingsLoader");
        transformLoader.setSettingsFolder(pathToSetting);
        transformLoader.load();
    }

}

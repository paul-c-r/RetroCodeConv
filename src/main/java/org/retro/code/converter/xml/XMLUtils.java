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

import org.retro.code.converter.exception.XMLMarshalException;

import javax.xml.bind.*;
import java.io.File;
import java.net.URL;

/**
 * This contains the utilities for unmarshaling (reading) the XML files.
 */
public class XMLUtils {

    /**
     * This is the unmarshal function for all the XML files used for configuration.
     * <p/>
     * <p>References {@link MarshalException}</p>
     *
     * @param path       The complete path and file name of the XML file to unmarshal.
     * @param typeObject The type of object to read into from the XML file.
     * @return The marshaled object, which will have all the field
     *         values as specified in the XML file.
     * @throws XMLMarshalException This is a wrapper for the MarshalException.
     */
    public static Object unmarshal(String path, Class typeObject) throws XMLMarshalException {

        try {
            if (path != null) {
                path = path.replaceAll("\\\\", "/");
            } else {
                throw new XMLMarshalException(new Throwable("Null Path"));
            }
            Unmarshaller unmarchaller;
            unmarchaller = getUnmarshaller(typeObject);

            try {
                Object doc;
                if (path.startsWith("http:") ||
                    path.startsWith("https:") ||
                    path.startsWith("shttp:") ||
                    path.startsWith("ldap:") ||
                    path.startsWith("ldaps:") ||
                    path.startsWith("telnet:") ||
                    path.startsWith("xmpp:") ||
                    path.startsWith("jar:") ||
                    path.startsWith("urn:") ||
                    path.startsWith("chrome:") ||
                    path.startsWith("cvs:") ||
                    path.startsWith("svn:") ||
                    path.startsWith("file:") ||
                    path.startsWith("ssh:") ||
                    path.startsWith("ftps:") ||
                    path.startsWith("ftp:")) {
                    doc = unmarchaller.unmarshal(new URL(path));
                } else {
                    if (path.startsWith("/") || (path.indexOf(":/") == 1)) {
                        doc = unmarchaller.unmarshal(new File(path));
                    } else {
                        doc = unmarchaller.unmarshal(new File(getCurrentDirectory() + path));
                    }
                }
                if (doc instanceof JAXBElement) {
                    JAXBElement jax = (JAXBElement) doc;
                    if (jax.getValue() != null) {
                        return jax.getValue();
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
                throw new MarshalException("unmarshal", e);
            }
        } catch (Exception e) {
            throw new XMLMarshalException(e);
        }
        return null;
    }

    private static JAXBContext getJAXBContext(Class classType) throws MarshalException {
        try {
            String packageName = classType.getPackage().getName();
            return JAXBContext.newInstance(packageName);
        } catch (Exception je) {
            try {
                return JAXBContext.newInstance(classType);
            } catch (JAXBException e) {
                throw new MarshalException("getJAXBContext", e);
            }
        }
    }

    private static Unmarshaller getUnmarshaller(Class classType) throws MarshalException {
        JAXBContext jc;
        try {
            jc = getJAXBContext(classType);
            return jc.createUnmarshaller();
        } catch (JAXBException e) {
            throw new MarshalException("getUnmarshaller", e);
        }
    }

    private static String getCurrentDirectory() throws MarshalException {
        File dir = new File(".");
        try {
            String dirStr = dir.getCanonicalPath().replaceAll("\\\\", "/");
            if (!dirStr.endsWith("/")) {
                dirStr += "/";
            }
            return dirStr;
        } catch (Exception e) {
            throw new MarshalException("getCurrentDirectory", e);
        }
    }

}

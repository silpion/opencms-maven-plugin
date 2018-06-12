package com.mediaworx.mojo.opencms;

/*-
 * #%L
 * OpenCms Maven Plugin
 * %%
 * Copyright (C) 2017 - 2018 Silpion IT-Solutions GmbH (https://www.silpion.de/)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import com.mediaworx.opencms.moduleutils.manifestgenerator.OpenCmsModuleManifestGenerator;
import com.mediaworx.opencms.moduleutils.manifestgenerator.exceptions.OpenCmsMetaXmlParseException;
import com.mediaworx.xmlutils.XmlHelper;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author schrader
 */
public class ModuleManifest {

    // TODO use: com.mediaworx.opencms.moduleutils.manifestgenerator.OpenCmsModuleManifestGenerator
    private static final String[] CDATA_NODES = new String[]{"nicename", "description", "authorname", "authoremail", "value"};

    // TODO use: com.mediaworx.opencms.moduleutils.manifestgenerator.OpenCmsModuleManifestGenerator
    private static final String FILES_NODE_XPATH = "/export/files";

    private Log log;

    private XmlHelper xmlHelper;
    private Document manifest;

    private File location;

    public ModuleManifest(File src) throws OpenCmsMetaXmlParseException {

        location = src;

        try {
            xmlHelper = new XmlHelper();
            manifest = xmlHelper.parseFile(src);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new OpenCmsMetaXmlParseException(e.getMessage(), e);
        }

    }

    public ModuleManifest setLog(Log log) {
        this.log = log;
        return this;
    }

    public void addResource(ModuleResource resource) throws OpenCmsMetaXmlParseException {
        String name = "/templates/file_" + resource.getType() + ".xml";
        InputStream input = getClass().getResourceAsStream(name);
        if (input == null) {
            log.warn("Skip adding resource '" + resource.getFile().getAbsolutePath() + "' to module, no template found: '" + name + "'!");
            return;
        }

        try {
            Node filesNode = xmlHelper.getSingleNodeForXPath(manifest, FILES_NODE_XPATH);
            Node fileNode = read(input, resource);
            xmlHelper.appendNode(filesNode, fileNode);
        } catch (XPathExpressionException e) {
            throw new OpenCmsMetaXmlParseException(e.getMessage(), e);
        }
    }

    public void write(OutputStream out) throws IOException {
        String manifestString = xmlHelper.getXmlStringFromDocument(manifest, CDATA_NODES);
        out.write(manifestString.getBytes(Charset.forName(XmlHelper.DEFAULT_ENCODING)));
    }

    private Document read(InputStream input, ModuleResource resource) {
        try {
            Method method = XmlHelper.class.getDeclaredMethod("getNonValidatingDocumentBuilder");
            method.setAccessible(true);
            DocumentBuilder builder = (DocumentBuilder) method.invoke(xmlHelper);

            String content = initialize(input, resource);
            return builder.parse(new InputSource(new StringReader(content)));
        } catch (IOException | NoSuchMethodException | IllegalAccessException | SAXException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String initialize(InputStream input, ModuleResource resource) throws IOException {
        String content = IOUtils.toString(input, XmlHelper.DEFAULT_ENCODING);
        long lastModified = resource.getFile().lastModified();

        Map<String, String> replacements = new HashMap<>();
        replacements.put(OpenCmsModuleManifestGenerator.META_VAR_SOURCE, resource.getVfsPath(location.getParent()));
        replacements.put(OpenCmsModuleManifestGenerator.META_VAR_DESTINATION, resource.getVfsPath(location.getParent()));
        replacements.put(OpenCmsModuleManifestGenerator.META_VAR_UUIDSTRUCTURE, generateUUID());
        replacements.put(OpenCmsModuleManifestGenerator.META_VAR_UUIDRESOURCE, generateUUID());
        replacements.put(OpenCmsModuleManifestGenerator.META_VAR_DATELASTMODIFIED, formatDate(lastModified));
        replacements.put(OpenCmsModuleManifestGenerator.META_VAR_DATECREATED, formatDate(lastModified));

        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            content = content.replaceAll(Pattern.quote(replacement.getKey()),
                    Matcher.quoteReplacement(replacement.getValue()));
        }

        return content;
    }

    private String generateUUID() {
        try {
            Method method = OpenCmsModuleManifestGenerator.class.getDeclaredMethod("generateUUID");
            method.setAccessible(true);
            return (String) method.invoke(OpenCmsModuleManifestGenerator.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String formatDate(long date) {
        try {
            Method method = OpenCmsModuleManifestGenerator.class.getDeclaredMethod("formatDate", long.class);
            method.setAccessible(true);
            return (String) method.invoke(OpenCmsModuleManifestGenerator.class, date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

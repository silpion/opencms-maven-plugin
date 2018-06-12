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


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

/**
 * @author schrader
 */
public class ModuleResource implements Comparable<ModuleResource> {

    private String type;

    private File file;

    public ModuleResource(String type, File file) {
        this.type = type;
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public File getFile() {
        return file;
    }

    public String getVfsPath(String reference) {
        String thisPath = file.getAbsolutePath();
        String s = StringUtils.removeStart(thisPath, reference + File.separator);
        return s.replaceAll("\\\\", "/"); // windows crap
    }

    @Override
    public int compareTo(ModuleResource o) {
        return file.getAbsolutePath().compareTo(o.getFile().getAbsolutePath());
    }

    public static class Jar extends Binary {
        public Jar(File file) {
            super(file);
        }
    }

    public static class Binary extends ModuleResource {
        public Binary(File file) {
            super("binary", file);
        }
    }

    public static class Plain extends ModuleResource {
        public Plain(File file) {
            super("plain", file);
        }
    }

    public static class Image extends ModuleResource {
        public Image(File file) {
            super("image", file);
        }
    }

    public static class Folder extends ModuleResource {
        public Folder(File file) {
            super("folder", file);
        }
    }

    public static ModuleResource ofFile(File file) {
        ModuleResource result = null;
        String lowCaseExt = file.getName();
        int dot = lowCaseExt.lastIndexOf('.');
        if (dot != -1) {
            lowCaseExt = lowCaseExt.substring(dot + 1);
        }

        if ("jpg".equals(lowCaseExt)
                || "jpeg".equals(lowCaseExt)
                || "svg".equals(lowCaseExt)
                || "png".equals(lowCaseExt)
                || "gif".equals(lowCaseExt)) {
            result = new Image(file);
        }

        return (result != null) ? result : new Plain(file);
    }

    public static ModuleResource ofFolder(File file) {
        return new Folder(file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleResource that = (ModuleResource) o;
        return Objects.equals(file.getAbsolutePath(), that.file.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}

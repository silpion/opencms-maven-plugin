package com.mediaworx.mojo.opencms;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

/**
 * @author schrader
 */
public class ModuleResource {

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

    public static class Jar extends ModuleResource {

        public Jar(File file) {
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

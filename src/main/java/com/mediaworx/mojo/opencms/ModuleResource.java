package com.mediaworx.mojo.opencms;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

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
}

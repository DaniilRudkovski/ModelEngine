/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.ticxo.modelengine.api.utils;

import com.google.common.io.ByteStreams;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.bukkit.plugin.java.JavaPlugin;

public class TFile {
    public static final String SEP = System.getProperty("file.separator");

    public static File copyResource(JavaPlugin plugin, File file, String path) {
        if (!file.exists()) {
            try {
                FileOutputStream writer = new FileOutputStream(file);
                InputStream reader = plugin.getResource(path);
                if (reader != null) {
                    ByteStreams.copy((InputStream)reader, (OutputStream)writer);
                }
                ((OutputStream)writer).close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File copyResource(JavaPlugin plugin, File origin, String resourceOrigin, String path) {
        return TFile.copyResource(plugin, TFile.createFile(origin, path), resourceOrigin + "/" + path);
    }

    public static String createPath(String ... path) {
        if (path.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(path[0]);
        for (int i = 1; i < path.length; ++i) {
            builder.append(SEP).append(path[i]);
        }
        return builder.toString();
    }

    public static File createDirectory(File parent, String ... path) {
        String compactPath = TFile.createPath(path);
        File file = new File(parent, compactPath);
        if (!file.exists() && !file.mkdirs()) {
            TLogger.log("Failed to create directory: " + compactPath);
        }
        return file;
    }

    public static File createFile(File parent, String ... path) {
        String compactPath = TFile.createPath(path);
        File file = new File(parent, compactPath);
        if (!(file.getParentFile().exists() || file.getParentFile().mkdirs() || file.getParentFile().exists())) {
            TLogger.log("Failed to create file: " + compactPath);
        }
        return file;
    }

    public static File createFileOrEmpty(File parent, String ... path) {
        String compactPath = TFile.createPath(path);
        File file = new File(parent, compactPath);
        try {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() || !file.exists() && !file.createNewFile()) {
                TLogger.log("Failed to create file: " + compactPath);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File createFile(File parent, String scope, ResourceLocation location, String extension) {
        File file = new File(parent.getPath() + SEP + location.getNamespace() + SEP + scope, location.getPath() + "." + extension);
        if (!(file.getParentFile().exists() || file.getParentFile().mkdirs() || file.getParentFile().exists())) {
            TLogger.log("Failed to create file: " + file.getPath());
        }
        return file;
    }

    public static void recreateFile(File file) {
        try {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() || !file.exists() && !file.createNewFile()) {
                TLogger.log("Failed to create file: " + file.getName());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage toImage(String data) {
        String[] d = data.split(",");
        if (d.length > 1) {
            return TFile.rawToImage(d[1]);
        }
        return TFile.rawToImage(data);
    }

    public static BufferedImage rawToImage(String data) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(data);
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String removeExtension(String path) {
        int id = path.lastIndexOf(".");
        if (id == -1) {
            return path;
        }
        return path.substring(0, id);
    }

    public static boolean isExtension(String path, String extensions) {
        int id = path.lastIndexOf(".");
        if (id == -1) {
            return false;
        }
        return path.substring(id + 1).equalsIgnoreCase(extensions);
    }

    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        int length;
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    TFile.zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static boolean delete(File file) {
        if (!file.isDirectory()) {
            return file.delete();
        }
        File[] list = file.listFiles();
        if (list == null) {
            return file.delete();
        }
        boolean success = true;
        for (File f : list) {
            success &= TFile.delete(f);
        }
        return success &= file.delete();
    }
}


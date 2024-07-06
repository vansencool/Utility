package dev.vansen.utility.resource;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import dev.vansen.utility.plugin.PluginUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceUtils {

    public static void saveYmlFiles() {
        Utility plugin = PluginHolder.getPluginInstance();

        try {
            String jarPath = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            ZipFile zipFile = new ZipFile(jarPath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".yml") || entryName.endsWith(".yaml")) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    File outFile = new File(PluginUtils.pluginFolder(), entryName);
                    copy(inputStream, outFile);
                }
            }
            zipFile.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save resources.", e);
        }
    }

    private static void copy(@NotNull InputStream inputStream, @NotNull File file) throws IOException {
        try (inputStream; OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}
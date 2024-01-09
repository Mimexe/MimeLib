package fr.mime.mimelib;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeLib {
    @NotNull
    public static PluginManager getPM() {
        return Bukkit.getPluginManager();
    }

    public static InstallType getPluginInstallType(String pluginName) {
        if(isPluginEnabled(pluginName)) {
            return InstallType.ENABLED;
        } else if (isPluginInstalled(pluginName)) {
            return InstallType.DISABLED;
        } else {
            return InstallType.NOT_INSTALLED;
        }
    }

    public static boolean isPluginEnabled(String pluginName) {
        return getPM().isPluginEnabled(pluginName);
    }

    public static boolean isPluginEnabled(Plugin plugin) {
        return getPM().isPluginEnabled(plugin);
    }

    public static String getMinecraftVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static @NotNull String getServerType() {
        Pattern pattern = Pattern.compile("git-(\\w+)-\\d+ \\(MC: \\d+\\.\\d+\\.\\d+\\)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());

        if (matcher.matches()) {
            String serverType = matcher.group(1);
            return serverType.substring(0, 1).toUpperCase() + serverType.substring(1).toLowerCase();
        } else {
            MimeLibPlugin.getInstance().getLogger().warning("getServerType(): Unable to get server type");
            return "Unknown";
        }
    }

    public static boolean isPluginInstalled(String pluginName) {
        return getPM().getPlugin(pluginName) != null;
    }

    public static @NotNull String getMimeLibVersion() {
        return MimeLibPlugin.getInstance().getDescription().getVersion();
    }

    public static @NotNull String getPluginString(@NotNull Plugin plugin) {
        PluginDescriptionFile pdf = plugin.getDescription();
        String authors;
        if(pdf.getAuthors().size() > 1) {
            StringBuilder authorsBuilder = new StringBuilder();
            for(String author : pdf.getAuthors()) {
                authorsBuilder.append(author).append(", ");
            }
            authors = authorsBuilder.toString();
        } else {
            authors = pdf.getAuthors().get(0);
        }
        return pdf.getName() + " v" + pdf.getVersion() + " by " + authors+" running on " + MimeLib.getServerType() + " " +MimeLib.getMinecraftVersion();
    }

    public static boolean isPaper() {
        return getServerType().equalsIgnoreCase("Paper");
    }

    public static boolean isSpigot() {
        return getServerType().equalsIgnoreCase("Spigot");
    }

    public static boolean isBukkit() {
        return getServerType().equalsIgnoreCase("Bukkit");
    }

    public static void downloadFile(String url, File file) throws IOException {
        FileUtils.copyURLToFile(new URL(url), file);
    }

    public static void downloadFile(String url, String path) throws IOException {
        downloadFile(url, new File(path));
    }

    public enum InstallType {
        ENABLED,
        DISABLED,
        NOT_INSTALLED;

        @Override
        public @NotNull String toString() {
            return super.toString().toLowerCase().replace("_", " ");
        }

        public InstallType fromString(@NotNull String string) {
            return InstallType.valueOf(string.toUpperCase().replace(" ", "_"));
        }
    }
}

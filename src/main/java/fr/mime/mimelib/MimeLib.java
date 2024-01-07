package fr.mime.mimelib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class MimeLib {
    static {
        // check if used on a minecraft server
        try {
            Class.forName("org.bukkit.Bukkit");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MimeLib is only usable on a minecraft server");
        }
    }

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

    public static boolean isPluginInstalled(String pluginName) {
        return getPM().getPlugin(pluginName) != null;
    }

    public static String getMimeLibVersion() {
        return MimeLibPlugin.getInstance().getDescription().getVersion();
    }

    public static String getPluginString(Plugin plugin) {
        PluginDescriptionFile pdf = plugin.getDescription();
        StringBuilder authors = new StringBuilder();
        for(String author : pdf.getAuthors()) {
            authors.append(author).append(", ");
        }
        return pdf.getName() + " v" + pdf.getVersion() + " by " + authors+" running on "+Bukkit.getBukkitVersion();
    }

    public enum InstallType {
        ENABLED,
        DISABLED,
        NOT_INSTALLED
    }
}

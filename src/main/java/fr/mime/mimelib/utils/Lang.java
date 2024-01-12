package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static HashMap<String, String> placeholders = new HashMap<>();
    private static HashMap<String, String> defaultsValues = new HashMap<>();

    static {
        reloadPlaceholders();
        defaultsValues.put("command.version", "%prefix% %stringversion%");
        defaultsValues.put("command.reload", "%prefix% &aMimeLib reloaded");
        defaultsValues.put("command.update.disabled", "%prefix% &cUpdate checker is disabled");
        defaultsValues.put("command.update.available", "%prefix% &aNew version available: &f%update_version%&a. &aUse &e/mimelib update download &ato update");
        defaultsValues.put("command.update.checking", "%prefix% &aChecking for updates...");
        defaultsValues.put("command.update.latest", "%prefix% &aYou are using the latest version");
        defaultsValues.put("command.update.error", "%prefix% &cAn error occurred while checking for updates");
        defaultsValues.put("command.download.start", "%prefix% &aDownloading MimeLib %update_version%...");
        defaultsValues.put("command.download.success", "%prefix% &aMimeLib %update_version% downloaded successfully. Restart your server to apply changes.");
        defaultsValues.put("command.download.error", "%prefix% &cAn error occurred while downloading MimeLib %update_version%");
        defaultsValues.put("command.download.restart", "%prefix% &cRestarting server in 5 seconds...");
        defaultsValues.put("command.help.header", "&6&m---------------------&r &6MimeLib &7%version% &6&m---------------------&r");
        defaultsValues.put("command.help.footer", "&6&m-----------------------------------------------------&r");
        defaultsValues.put("command.help.commands", "&6/mimelib version &7- &fShows MimeLib version\n" +
                "&6/mimelib update check &7- &fChecks for updates\n" +
                "&6/mimelib update download &7- &fUpdates MimeLib\n" +
                "&6/mimelib reload &7- &fReloads MimeLib\n" +
                "&6/mimelib help &7- &fShows this help page");
    }

    public static void checkDefaults() {
        for (Map.Entry<String, String> entry : defaultsValues.entrySet()) {
            String key = entry.getKey();
            String defaultValue = entry.getValue();

            if (!MimeLibPlugin.getInstance().getLangConfig().isSet(key)) {
                MimeLibPlugin.getInstance().getLangConfig().set(key, defaultValue);
                MimeLibPlugin.getInstance().getLogger().warning("Missing key " + key + " in lang.yml, adding default value");
            }
        }

        try {
            MimeLibPlugin.getInstance().getLangConfig().save(MimeLibPlugin.getInstance().getLangConfigFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reloadPlaceholders() {
        placeholders.clear();
        placeholders.put("prefix", MimeLibPlugin.getInstance().getPrefix());
        placeholders.put("version", MimeLib.getMimeLibVersion());
        placeholders.put("update_version", MimeLibPlugin.getInstance().getUpdateVersion());
        placeholders.put("stringversion", "MimeLib v" + MimeLib.getMimeLibVersion() + " by Mime");
    }

    public static @NotNull String get(String key, boolean withoutPlaceholders) {
        String text = MimeLibPlugin.getInstance().getLangConfig().getString(key);
        if (text == null) {
            return "§cError: §e" + key + "§c not found in lang.yml";
        }
        if (!withoutPlaceholders) {
            text = replaceAllPlaceholders(text);
        }
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    public static @NotNull String get(String key) {
        return get(key, false);
    }

    public static String replaceAllPlaceholders(String text) {
        reloadPlaceholders();
        for (String placeholder : placeholders.keySet()) {
            text = text.replace("%" + placeholder + "%", placeholders.get(placeholder));
        }
        return text;
    }
}

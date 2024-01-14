package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static HashMap<String, String> placeholders = new HashMap<>();
    private static HashMap<String, String> defaultsValues = new HashMap<>();

    static {
        reloadPlaceholders();
        for (String key : MimeLibPlugin.getInstance().getLangConfig().getDefaults().getKeys(true)) {
            if(MimeLibPlugin.getInstance().getLangConfig().isConfigurationSection(key)) continue;
            Object value = MimeLibPlugin.getInstance().getLangConfig().getDefaults().get(key);
            if (value instanceof String) {
                defaultsValues.put(key, (String) value);
            } else {
                MimeLibPlugin.getInstance().getLogger().warning("Invalid value for key " + key + " in lang-en.yml");
            }
        }
    }

    public static void checkDefaults() {
        for (Map.Entry<String, String> entry : defaultsValues.entrySet()) {
            String key = entry.getKey();
            String defaultValue = entry.getValue();

            if (!MimeLibPlugin.getInstance().getLangConfig().isSet(key)) {
                MimeLibPlugin.getInstance().getLangConfig().set(key, defaultValue);
                MimeLibPlugin.getInstance().getLogger().warning("Missing key " + key + " in lang-en.yml, adding default value");
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
            return "§cError: §e" + key + "§c not found in lang-en.yml";
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

    public static String getCurrentLang() {
        return MimeLibPlugin.getInstance().getLangConfig().getString("locale");
    }
}

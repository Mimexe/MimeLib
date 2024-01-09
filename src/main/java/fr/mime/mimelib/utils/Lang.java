package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class Lang {
    public static HashMap<String, String> placeholders = new HashMap<>();
    static {
        reloadPlaceholders();
    }
    private static void reloadPlaceholders() {
        placeholders.clear();
        placeholders.put("prefix", MimeLibPlugin.getInstance().getPrefix());
        placeholders.put("version", MimeLib.getMimeLibVersion());
        placeholders.put("update_version", MimeLibPlugin.getInstance().getUpdateVersion());
        placeholders.put("stringversion", "MimeLib v"+MimeLib.getMimeLibVersion()+" by Mime");
    }
    public static String get(String key, boolean withoutPlaceholders) {
        String text = MimeLibPlugin.getInstance().getLangConfig().getString(key);
        if(text == null) {
            return "§cError: §e"+key+"§c not found in lang.yml";
        }
        if(!withoutPlaceholders) {
            text = replaceAllPlaceholders(text);
        }
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    public static String get(String key) {
        return get(key, false);
    }

    public static String replaceAllPlaceholders(String text) {
        reloadPlaceholders();
        for(String placeholder : placeholders.keySet()) {
            text = text.replace("%"+placeholder+"%", placeholders.get(placeholder));
        }
        return text;
    }
}

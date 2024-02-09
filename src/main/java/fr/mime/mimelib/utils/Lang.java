package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Class for managing the language
 */
public class Lang {
    /**
     * The placeholders
     */
    private static HashMap<String, String> placeholders = new HashMap<>();

    static {
        reloadPlaceholders();
    }

    /**
     * Reload the placeholders
     */
    private static void reloadPlaceholders() {
        placeholders.clear();
        placeholders.put("prefix", MimeLibPlugin.getInstance().getPrefix());
        placeholders.put("version", MimeLib.getMimeLibVersion());
        placeholders.put("update_version", MimeLibPlugin.getInstance().getUpdateVersion());
        placeholders.put("stringversion", "MimeLib v" + MimeLib.getMimeLibVersion() + " by Mime");
    }

    /**
     * Get the text of the key
     * @param key the key of the text
     * @param withoutPlaceholders if the placeholders should be replaced
     * @return the text of the key
     */
    public static @NotNull String get(String key, boolean withoutPlaceholders) {
        return get(key, null, withoutPlaceholders);
    }

    /**
     * Get the text of the key
     * @param key the key of the text
     * @param player the player
     * @param withoutPlaceholders if the placeholders should be replaced
     * @return the text of the key
     */
    public static @NotNull String get(String key, Player player, boolean withoutPlaceholders) {
        String text = MimeLibPlugin.getInstance().getLangConfig().getString(key);
        if (text == null) {
            return "§cError: §e" + key + "§c not found in lang-"+getCurrentLang()+".yml";
        }
        if (!withoutPlaceholders) {
            text = replaceAllPlaceholders(text, player);
        }
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    /**
     * Get the text of the key
     * @param key the key of the text
     * @return the text of the key
     */
    public static @NotNull String get(String key) {
        return get(key, false);
    }

    /**
     * Get the text of the key
     * @param key the key of the text
     * @param player the player
     * @return the text of the key
     */
    public static @NotNull String get(String key, Player player) {
        return get(key, player, false);
    }

    /**
     * Replace all the placeholders in the text
     * @param text the text
     * @param player the player
     * @return the text with the placeholders replaced
     */
    public static String replaceAllPlaceholders(String text, Player player) {
        reloadPlaceholders();
        for (String placeholder : placeholders.keySet()) {
            text = text.replace("{" + placeholder + "}", placeholders.get(placeholder));
        }
        if(MimeLibPlugin.getInstance().getHooks().getPlaceholderAPI().isEnabled()) {
            text = MimeLibPlugin.getInstance().getHooks().getPlaceholderAPI().setPlaceholders(player, text);
        }
        return text;
    }

    /**
     * Get the current language
     * @return the current language
     */
    public static String getCurrentLang() {
        return MimeLibPlugin.getInstance().getConfig().getString("locale");
    }

    /**
     * Format the language
     * @param str the language
     * @return the formatted language
     */
    public static @NotNull String formatLang(@NotNull String str){
        if (str.equalsIgnoreCase("en")) {
            return "English";
        } else if (str.equalsIgnoreCase("fr")) {
            return "French";
        } else {
            return "English";
        }
    }
}

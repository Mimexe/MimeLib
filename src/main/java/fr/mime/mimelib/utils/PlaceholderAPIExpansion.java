package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The expansion for PlaceholderAPI
 */
public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    /**
     * Get the identifier of the hook
     * @return the identifier of the hook
     */
    @Override
    public @NotNull String getIdentifier() {
        return "mimelib";
    }

    /**
     * Get the author of the hook
     * @return the author of the hook
     */
    @Override
    public @NotNull String getAuthor() {
        return "Mime4X";
    }

    /**
     * Get the version of the hook
     * @return the version of the hook
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    /**
     * Persist the hook
     * @return if the hook is persisted
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Method called when a placeholder is requested
     * @param player the player
     * @param params the parameters
     * @return the result of the placeholder
     */
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("version")) {
            return MimeLib.getMimeLibVersion();
        }
        if(params.equalsIgnoreCase("update")) {
            return MimeLibPlugin.getInstance().isUpdateAvailable() ? "§aAvailable" : "§cNot available";
        }
        if (params.equalsIgnoreCase("lang")) {
            return Lang.formatLang(Lang.getCurrentLang());
        }
        return null;
    }
}

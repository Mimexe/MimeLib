package fr.mime.mimelib.hooks;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import fr.mime.mimelib.utils.Hooks;
import fr.mime.mimelib.utils.PlaceholderAPIExpansion;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * The hook for PlaceholderAPI
 * @see Hook
 * @see MimeLibPlugin
 */
public class PlaceholderAPIHook extends Hook {
    /**
     * The hooks
     */
    private final Hooks hooks;

    /**
     * Constructor for the hook
     */
    public PlaceholderAPIHook() {
        super("PlaceholderAPI");
        hooks = MimeLibPlugin.getInstance().getHooks();
    }

    /**
     * Method called when the hook is enabled
     */
    @Override
    public void onEnable() {
        if(!MimeLib.isPluginEnabled(getName())) {
            hooks.log("PlaceholderAPI not found, disabling hook");
            MimeLibPlugin.getInstance().getLogger().warning("PlaceholderAPI is not installed ! Some features will not work");
            hooks.disableHook(getName());
            return;
        }
        hooks.log("[PlaceholderAPI] Registering placeholders");
        new PlaceholderAPIExpansion().register();
        hooks.log("Hooked into PlaceholderAPI");
    }

    /**
     * Method called when the hook is disabled
     */
    @Override
    public void onDisable() {
        hooks.log("Unhooked from PlaceholderAPI");
    }

    /**
     * Replace the placeholders in the string
     * @param p the player
     * @param string the string
     * @return the string with the placeholders replaced
     */
    public String setPlaceholders(Player p, String string) {
        if(!isEnabled()) {
            return string;
        }
        return PlaceholderAPI.setPlaceholders(p, string);
    }
}

package fr.mime.mimelib.hooks;

import com.rylinaux.plugman.PlugMan;
import com.rylinaux.plugman.util.PluginUtil;
import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;
import fr.mime.mimelib.utils.Hooks;

/**
 * The hook for PlugManX
 * @see Hook
 */
public class PlugManXHook extends Hook {
    /**
     * The hooks
     */
    private final Hooks hooks;

    /**
     * Constructor for the hook
     */
    public PlugManXHook() {
        super("PlugManX");
        hooks = MimeLibPlugin.getInstance().getHooks();
    }

    /**
     * Method called when the hook is enabled
     */
    @Override
    public void onEnable() {
        if(!MimeLib.isPluginEnabled(getName())) {
            hooks.log("PlugManX not found, disabling hook");
            MimeLibPlugin.getInstance().getLogger().warning("PlugManX is not installed ! Some features will not work");
            hooks.disableHook(getName());
            return;
        }
        hooks.log("Hooked into PlugManX");
    }

    /**
     * Method called when the hook is disabled
     */
    @Override
    public void onDisable() {
        hooks.log("Unhooked from PlugManX");
    }

    /**
     * Get the hook
     * @return the hook
     */
    public PluginUtil get() {
        if(!isEnabled()) {
            return null;
        }
        return PlugMan.getInstance().getPluginUtil();
    }
}

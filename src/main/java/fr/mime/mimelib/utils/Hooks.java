package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLibPlugin;
import fr.mime.mimelib.hooks.Hook;
import fr.mime.mimelib.hooks.PlaceholderAPIHook;
import fr.mime.mimelib.hooks.PlugManXHook;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Class for managing hooks
 */
public class Hooks {
    /**
     * The plugin instance
     */
    private final MimeLibPlugin plugin = MimeLibPlugin.getInstance();
    /**
     * The logger of the plugin
     */
    private final Logger logger = plugin.getLogger();
    /**
     * The enabled hooks
     */
    private HashMap<String, Hook> enabledHooks = new HashMap<>();
    /**
     * The hooks
     */
    private HashMap<String, Class<? extends Hook>> hooks = new HashMap<>();

    /**
     * Constructor for the hooks
     */
    public Hooks() {
        hooks.put("PlaceholderAPI", PlaceholderAPIHook.class);
        hooks.put("PlugManX", PlugManXHook.class);
        log("Loaded "+hooks.size()+" hook");
    }

    /**
     * Enable all the hooks
     */
    public void enableHooks() {
        for (String hook : hooks.keySet()) {
            enableHook(hook);
        }
    }

    /**
     * Disable all the hooks
     */
    public void disableHooks() {
        for (String hook : hooks.keySet()) {
            disableHook(hook);
        }
    }

    /**
     * Enable a hook
     * @param name the name of the hook
     */
    public void enableHook(@NotNull String name) {
        log("Enabling " + name);
        if(!hooks.containsKey(name)) {
            log("Hook "+name+" not found");
            return;
        }
        if(enabledHooks.containsKey(name)) {
            log("Hook "+name+" is already enabled");
            return;
        }
        try {
            Hook hook = hooks.get(name).getDeclaredConstructor().newInstance();
            hook.setEnabled(true);
            enabledHooks.put(name, hook);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log("Error while enabling hook "+name);
        }
    }

    /**
     * Disable a hook
     * @param name the name of the hook
     */
    public void disableHook(@NotNull String name) {
        log("Disabling " + name);
        if (!hooks.containsKey(name)) {
            log("Hook " + name + " not found");
            return;
        }
        if(!enabledHooks.containsKey(name)) {
            log("Hook "+name+" is not enabled");
            return;
        }
        enabledHooks.get(name).setEnabled(false);
        enabledHooks.remove(name);
    }

    /**
     * Get a hook
     * @param name the name of the hook
     * @return the hook
     */
    public Hook getHook(String name) {
        if(!hooks.containsKey(name)) {
            log("Hook "+name+" not found");
            return null;
        }
        if(!enabledHooks.containsKey(name)) {
            log("Hook "+name+" is not enabled");
            return null;
        }
        return enabledHooks.get(name);
    }

    /**
     * Get the PlaceholderAPI hook
     * @return the PlaceholderAPI hook
     */
    public PlaceholderAPIHook getPlaceholderAPI() {
        if(!hooks.containsKey("PlaceholderAPI")) {
            log("Hook PlaceholderAPI not found");
            return null;
        }
        if(!enabledHooks.containsKey("PlaceholderAPI")) {
            log("Hook PlaceholderAPI is not enabled");
            return null;
        }
        return (PlaceholderAPIHook) getHook("PlaceholderAPI");
    }

    /**
     * Get the PlugManX hook
     * @return the PlugManX hook
     */
    public PlugManXHook getPlugmanHook() {
        if(!hooks.containsKey("PlugManX")) {
            log("Hook PlugManX not found");
            return null;
        }
        if(!enabledHooks.containsKey("PlugManX")) {
            log("Hook PlugManX is not enabled");
            return null;
        }
        return (PlugManXHook) getHook("PlugManX");
    }

    /**
     * Log a message
     * @param message the message to log
     */
    public void log(String message) {
        logger.info("[Hooks] " + message);
    }
}

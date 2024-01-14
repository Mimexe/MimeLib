package fr.mime.mimelib.utils;

import fr.mime.mimelib.MimeLibPlugin;
import fr.mime.mimelib.hooks.Hook;

import java.util.HashMap;
import java.util.logging.Logger;

public class Hooks {
    private final MimeLibPlugin plugin = MimeLibPlugin.getInstance();
    private final Logger logger = plugin.getLogger();
    private final HashMap<String, Hook> hooks = new HashMap<>();
    public Hooks() {
//        hooks.put("PlaceholderAPI", new Place)
    }

    public void enableHooks() {
        for(Hook hook : hooks.values()) {
            log("Enabling " + hook.getName());
            enableHook(hook.getName());
        }
    }

    public void disableHooks() {
        for(Hook hook : hooks.values()) {
            log("Disabling " + hook.getName());
            enableHook(hook.getName());
        }
    }

    public void enableHook(String name) {
        if(!hooks.containsKey(name)) {
            log("Hook " + name + " not found");
            return;
        }
        log("Enabling " + name);
        hooks.get(name).setEnabled(true);
    }

    public void disableHook(String name) {
        if(!hooks.containsKey(name)) {
            log("Hook " + name + " not found");
            return;
        }
        log("Disabling " + name);
        hooks.get(name).setEnabled(false);
    }

    public Hook getHook(String name) {
        return hooks.get(name);
    }

    private void log(String message) {
        logger.info("[Hooks] " + message);
    }
}

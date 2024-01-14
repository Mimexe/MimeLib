package fr.mime.mimelib.hooks;

import fr.mime.mimelib.MimeLib;
import fr.mime.mimelib.MimeLibPlugin;

import java.util.logging.Logger;

public abstract class Hook {
    private final String name;
    private boolean enabled = false;
    public Hook(String name) {
        this.name = name;
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public void setEnabled(boolean enabled) {
        if(enabled) {
            if(!MimeLib.isPluginEnabled(getName())) {
                MimeLibPlugin.getInstance().getLogger().warning("Hook " + getName() + " is not installed. Disabling...");
                MimeLibPlugin.getInstance().getHooks().disableHook(this.getName());
                return;
            }
            onEnable();
            this.enabled = true;
        } else {
            onDisable();
            this.enabled = false;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

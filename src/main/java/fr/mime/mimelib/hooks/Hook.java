package fr.mime.mimelib.hooks;

/**
 * Abstract class for creating a hook
 */
public abstract class Hook {
    /**
     * The name of the hook
     */
    private final String name;
    /**
     * If the hook is enabled
     */
    private boolean enabled = false;

    /**
     * Constructor for the hook
     * @param name the name of the hook
     */
    public Hook(String name) {
        this.name = name;
    }

    /**
     * Method called when the hook is enabled
     */
    public abstract void onEnable();

    /**
     * Method called when the hook is disabled
     */
    public abstract void onDisable();

    /**
     * Enable or disable the hook
     * @param enabled if the hook should be enabled
     */
    public void setEnabled(boolean enabled) {
        if(enabled) {
            onEnable();
            this.enabled = true;
        } else {
            onDisable();
            this.enabled = false;
        }
    }

    /**
     * Get the name of the hook
     * @return the name of the hook
     */
    public String getName() {
        return name;
    }

    /**
     * Get if the hook is enabled
     * @return if the hook is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}

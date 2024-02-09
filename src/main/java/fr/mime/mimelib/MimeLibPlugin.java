package fr.mime.mimelib;

import com.github.zafarkhaja.semver.Version;
import fr.mime.mimelib.listeners.PlayerListener;
import fr.mime.mimelib.menu.MenuListener;
import fr.mime.mimelib.utils.Hooks;
import fr.mime.mimelib.utils.Lang;
import fr.mime.mimelib.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class of the plugin
 * @see JavaPlugin
 * @see MimeLib
 * @see MimeLibCommand
 */
public final class MimeLibPlugin extends JavaPlugin {
    /**
     * The instance of the plugin
     */
    private static MimeLibPlugin instance;
    /**
     * If the update is downloaded
     */
    private boolean isUpdateDownloaded = false;
    /**
     * If an update is available
     */
    private boolean isUpdateAvailable = false;
    /**
     * The version of the update
     */
    private String updateVersion = "";
    /**
     * The language configuration
     */
    private FileConfiguration langConfig;
    /**
     * If the update is checking
     */
    private boolean checkingUpdate = false;
    /**
     * The hooks of the plugin
     */
    private Hooks hooks;

    /**
     * The main method of the plugin
     * @see JavaPlugin#onLoad()
     */
    @Override
    public void onLoad() {
        instance = this;
        hooks = new Hooks();
        if (MimeLib.isBukkit()) {
            getLogger().warning("MimeLib is not compatible with Bukkit, please use Spigot or Paper");
        }
        File pluginsUpdatesDir = new File("." + File.separator + "plugins" + File.separator + "_updates_");
        if (pluginsUpdatesDir.exists()) {
            if (pluginsUpdatesDir.delete()) {
                getLogger().info("Old updates folder deleted");
            } else {
                getLogger().warning("Unable to delete old updates folder");
            }
        }
    }

    /**
     * Check for updates
     */
    void checkUpdates() {
        getLogger().info("Checking for updates...");
        checkingUpdate = true;
        new UpdateChecker(this, 114383).getVersion(version -> {
            Version currentVersion = Version.parse(MimeLib.getMimeLibVersion());
            Version newVersion = Version.parse(version);
            if (newVersion.isHigherThan(currentVersion)) {
                getLogger().info("New version available: " + version + " (currently running " + MimeLib.getMimeLibVersion() + ")");
                getLogger().info("Download it on https://www.spigotmc.org/resources/mimelib.114383/");
                isUpdateAvailable = true;
                updateVersion = version;
            } else if(newVersion.isEquivalentTo(currentVersion)) {
                getLogger().info("MimeLib is up to date!");
            } else if (currentVersion.isHigherThan(newVersion)) {
                getLogger().warning("Current version is higher than the latest, are you in the future ?");
                getLogger().warning("Probably a developer version or a mistake");
            }
            checkingUpdate = false;
        });
    }

    /**
     * Check for plugins using MimeLib
     */
    private void checkPlugins() {
        for (Plugin plugin : MimeLib.getPM().getPlugins()) {
            if(plugin.getDescription().getDepend().contains(this.getName())) {
                getLogger().info("Plugin " + plugin.getName() + " is using MimeLib as dependency");
            }
            if(plugin.getDescription().getSoftDepend().contains(this.getName())) {
                getLogger().info("Plugin " + plugin.getName() + " is using MimeLib as soft-dependency");
            }
        }
    }

    /**
     * Print the MimeLib text
     */
    private void printText() {
        getLogger().info("        _              _  _  _    ");
        getLogger().info(" _ __  (_) _ __   ___ | |(_)| |__ ");
        getLogger().info("| '  \\ | || '  \\ / -_)| || ||  _ \\");
        getLogger().info("|_|_|_||_||_|_|_|\\___||_||_||____/");
    }

    /**
     * Register the commands
     */
    private void registerCommands() {
        MimeLib.registerCommand(this, "mimelib", new MimeLibCommand());
    }

    /**
     * Register the listeners
     */
    private void registerListeners() {
        MimeLib.getPM().registerEvents(new PlayerListener(), this);
        MimeLib.getPM().registerEvents(new MenuListener(), this);
    }

    /**
     * Check the default values of the config
     */
    public void checkConfigDefaults() {
        Configuration defaults = getConfig().getDefaults();
        HashMap<String, Object> defaultsValues = new HashMap<>();
        if (defaults != null) {
            for (String key : defaults.getKeys(true)) {
                if(getConfig().isConfigurationSection(key)) continue;
                Object value = defaults.get(key);
                defaultsValues.put(key, value);
            }
        }
        for (Map.Entry<String, Object> entry : defaultsValues.entrySet()) {
            String key = entry.getKey();
            Object defaultValue = entry.getValue();

            if (!getConfig().isSet(key)) {
                getConfig().set(key, defaultValue);
                MimeLibPlugin.getInstance().getLogger().warning("Missing key " + key + " in config.yml, adding default value");
            }
        }

        saveConfig();
    }

    /**
     * Load the metrics
     */
    private void loadMetrics() {
        int pluginId = 20744;
        Metrics metrics = new Metrics(this, pluginId);

    }

    /**
     * The main method of the plugin
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("--------MIMELIB--------");
        printText();
        getLogger().info("Loading MimeLib...");
        getLogger().info("Enabling hooks...");
        getHooks().enableHooks();
        getLogger().info("Loading config and lang...");
        saveDefaultConfig();
        Map<String, String> lang = createLangConfig();
        if(lang == null) {
            getLogger().severe("Lang file not loaded.");
            return;
        }
        getLogger().info("Using locale " + lang.get("locale") + " (" + lang.get("file") + ")");
        getLogger().info("Checking for missing keys in config.yml...");
        checkConfigDefaults();
        if (getConfig().getBoolean("updatechecker.enabled") && getConfig().getBoolean("updatechecker.auto-check"))
            checkUpdates();
        getLogger().info("Registering commands and listeners...");
        registerCommands();
        registerListeners();
        checkPlugins();
        getLogger().info("Loading metrics...");
        loadMetrics();
        getLogger().info(MimeLib.getPluginString(this));
        if (getConfig().getBoolean("updatechecker.auto-download") && getConfig().getBoolean("updatechecker.enabled")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                if (!isUpdateAvailable) {
                    getLogger().info("AutoDownload: No updates available");
                    return;
                }
                getLogger().info("AutoDownload: Downloading updates...");
                try {
                    String returnCode = downloadUpdates();
                    if (returnCode.equals("SUCCESS")) {
                        getLogger().info("AutoDownload: MimeLib has been updated! Please restart your server.");
                    } else if (returnCode.equals("NO_UPDATES")) {
                        getLogger().info("AutoDownload: No updates available");
                    }
                } catch (IOException e) {
                    getLogger().warning("Unable to download updates: " + e.getMessage());
                }
            }, 60L);
        }
        if (MimeLib.isBukkit()) {
            getLogger().warning("MimeLib is not recommended on Bukkit, please use Spigot or Paper");
        }
        if (devmode()) {
            getLogger().warning("MimeLib is running in developer mode");
        }
        getLogger().info("MimeLib is now enabled");
        getLogger().info("--------MIMELIB--------");
    }

    /**
     * The main method of the plugin
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("--------MIMELIB--------");
        if (isUpdateDownloaded) {
            getLogger().info("Detected updates, deleting old version and moving new one...");
            File pluginsDir = new File("." + File.separator + "plugins");
            File pluginsDirUpdates = new File(pluginsDir, "_updates_");
            File mimeLibFileUpdates = new File(pluginsDirUpdates, "MimeLib.jar");
            File newMimeLibFile = new File(pluginsDir, "MimeLib-" + updateVersion + ".jar");
            if (mimeLibFileUpdates.exists()) {
                if (mimeLibFileUpdates.renameTo(newMimeLibFile)) {
                    getLogger().info("New version moved");
                } else {
                    getLogger().warning("Unable to move new version");
                }
            } else {
                getLogger().warning("Unable to move new version: file not found");
            }
            // Gets all files in plugins folder starting with MimeLib
            File[] mimeLibFiles = pluginsDir.listFiles((dir, name) -> name.startsWith("MimeLib"));
            if (mimeLibFiles != null) {
                for (File mimeLibFile1 : mimeLibFiles) {
                    if (!mimeLibFile1.getName().equalsIgnoreCase(newMimeLibFile.getName())) {
                        if (mimeLibFile1.delete()) {
                            getLogger().info("Old version deleted");
                        } else {
                            getLogger().warning("Unable to delete old version");
                        }
                    } else {
                        getLogger().info("Old version not deleted: new version (" + mimeLibFile1.getName() + ")");
                    }
                }
            } else {
                getLogger().warning("Unable to delete old version: file not found");
            }
            getLogger().info("Successfully updated MimeLib!");
        }
        getLogger().info("Disabling hooks...");
        getHooks().disableHooks();
        getLogger().info("MimeLib is now disabled");
        getLogger().info("--------MIMELIB--------");
    }

    /**
     * Get the instance of the plugin
     * @return the instance of the plugin
     */
    public static MimeLibPlugin getInstance() {
        return instance;
    }

    /**
     * Download the updates
     * @return the status of the download
     * @throws IOException if an error occurred
     */
    public @NotNull String downloadUpdates() throws IOException {
        if (this.isUpdateAvailable) {
            getLogger().info("Downloading updates...");
            File pluginsDir = new File("." + File.separator + "plugins" + File.separator + "_updates_");
            getLogger().info("Downloading to " + new File(pluginsDir, "MimeLib.jar").getAbsolutePath());
            MimeLib.downloadFile("https://api.spiget.org/v2/resources/114383/download", new File(pluginsDir, "MimeLib.jar").getAbsolutePath());
            getLogger().info("Please restart your server to apply updates");
            isUpdateDownloaded = true;
            return "SUCCESS";
        } else {
            getLogger().info("No updates available");
            return "NO_UPDATES";
        }
    }

    /**
     * Create the language configuration
     * @return the language configuration
     */
    private @Nullable Map<String, String> createLangConfig() {
        langConfig = new YamlConfiguration();
        String file = "lang/lang-"+Lang.getCurrentLang()+".yml";
        InputStream stream = getResource(file);
        if(stream == null) {
            getLogger().severe("Unable to load lang file "+file);
            getLogger().severe("Does it exist ?");
            getLogger().severe("Disabling...");
            MimeLib.disable(this);
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            langConfig.load(reader);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<>();
        map.put("file", file);
        map.put("locale", Lang.formatLang(Lang.getCurrentLang()));
        return map;
    }

    /**
     * Is the update downloaded?
     * @return if the update is downloaded
     */
    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    /**
     * Get the language configuration
     * @return the language configuration
     */
    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    /**
     * Get the version of the update
     * @return the version of the update
     */
    public String getUpdateVersion() {
        return updateVersion;
    }

    /**
     * Is the update checking?
     * @return if the update is checking
     */
    public boolean isCheckingUpdate() {
        return checkingUpdate;
    }

    /**
     * Get the prefix of the plugin
     * @return the prefix of the plugin
     */
    @Contract(pure = true)
    public @NotNull String getPrefix() {
        return "§6MimeLib §7-§f";
    }

    /**
     * Check if devmode is enabled
     * @return if devmode is enabled
     */
    public boolean devmode() {
        return getConfig().getBoolean("devmode");
    }

    /**
     * Check if devmode is enabled
     * @param sender the sender of the command
     * @return if devmode is enabled
     */
    public boolean devmode(CommandSender sender) {
        return getConfig().getBoolean("devmode") && sender.hasPermission("mimelib.devmode");
    }

    /**
     * Get the hooks of the plugin
     * @return the hooks of the plugin
     */
    public Hooks getHooks() {
        return hooks;
    }

    /**
     * Simulate an update
     */
    public void simulateUpdate() {
        if(!devmode()) {
            getLogger().warning("Unable to simulate update: devmode is not enabled");
            return;
        }
        isUpdateAvailable = true;
        updateVersion = "2.7.2 (SIMULATION)";
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(MimeLibPlugin.getInstance().isUpdateAvailable() && p.hasPermission("mimelib.update.notify")) {
                p.sendMessage(Lang.get("command.update.available"));
            }
        }
    }
}

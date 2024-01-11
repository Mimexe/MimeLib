package fr.mime.mimelib;

import fr.mime.mimelib.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class MimeLibPlugin extends JavaPlugin {
    private static MimeLibPlugin instance;
    private boolean isUpdateDownloaded = false;
    private boolean isUpdateAvailable = false;
    private String updateVersion = "";
    private File langConfigFile;
    private FileConfiguration langConfig;
    private boolean checkingUpdate = false;

    @Override
    public void onLoad() {
        instance = this;
        File pluginsUpdatesDir = new File("." + File.separator + "plugins"+File.separator+"_updates_");
        if(pluginsUpdatesDir.exists()) {
            if(pluginsUpdatesDir.delete()) {
                getLogger().info("Old updates folder deleted");
            } else {
                getLogger().warning("Unable to delete old updates folder");
            }
        }
    }

    boolean checkUpdates(){
        getLogger().info("Checking for updates...");
        checkingUpdate = true;
        new UpdateChecker(this, 114383).getVersion(version -> {
            if (!MimeLib.getMimeLibVersion().equalsIgnoreCase(version)) {
                getLogger().info("New version available: " + version+" (currently running "+MimeLib.getMimeLibVersion()+")");
                getLogger().info("Download it on https://www.spigotmc.org/resources/mimelib.114383/");
                isUpdateAvailable = true;
                updateVersion = version;
                checkingUpdate = false;
            } else {
                getLogger().info("MimeLib is up to date!");
                checkingUpdate = false;
            }
        });
        return false;
    }

    private void checkPlugins() {
        
    }

    private void printText() {
        getLogger().info("        _              _  _  _    ");
        getLogger().info(" _ __  (_) _ __   ___ | |(_)| |__ ");
        getLogger().info("| '  \\ | || '  \\ / -_)| || ||  _ \\");
        getLogger().info("|_|_|_||_||_|_|_|\\___||_||_||____/");
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("mimelib")).setExecutor(new MimeLibCommand());
    }

    private void registerListeners() {
        MimeLib.getPM().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("--------MIMELIB--------");
        printText();
        saveDefaultConfig();
        createLangConfig();
        if(getConfig().getBoolean("updatechecker.enabled") && getConfig().getBoolean("updatechecker.auto-check")) checkUpdates();
        registerCommands();
        registerListeners();
        getLogger().info(MimeLib.getPluginString(this));
        if(getConfig().getBoolean("updatechecker.auto-download") && getConfig().getBoolean("updatechecker.enabled")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                if(!isUpdateAvailable) {
                    getLogger().info("AutoDownload: No updates available");
                    return;
                }
                getLogger().info("AutoDownload: Downloading updates...");
                try {
                    String returnCode = downloadUpdates();
                    if(returnCode.equals("SUCCESS")){
                        getLogger().info("AutoDownload: MimeLib has been updated! Please restart your server.");
                    } else if (returnCode.equals("NO_UPDATES")) {
                        getLogger().info("AutoDownload: No updates available");
                    }
                } catch (IOException e) {
                    getLogger().warning("Unable to download updates: "+e.getMessage());
                }
            }, 60L);
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6MimeLib &7- &eMimeLib v"+MimeLib.getMimeLibVersion()+" is now enabled"));
        getLogger().info("--------MIMELIB--------");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("--------MIMELIB--------");
        if(isUpdateDownloaded) {
            getLogger().info("Detected updates, deleting old version and moving new one...");
            File pluginsDir = new File("." + File.separator + "plugins");
            File pluginsDirUpdates = new File(pluginsDir, "_updates_");
            File mimeLibFileUpdates = new File(pluginsDirUpdates, "MimeLib.jar");
            File newMimeLibFile = new File(pluginsDir, "MimeLib-"+updateVersion+".jar");
            if(mimeLibFileUpdates.exists()) {
                if(mimeLibFileUpdates.renameTo(newMimeLibFile)) {
                    getLogger().info("New version moved");
                } else {
                    getLogger().warning("Unable to move new version");
                }
            } else {
                getLogger().warning("Unable to move new version: file not found");
            }
            // Gets all files in plugins folder starting with MimeLib
            File[] mimeLibFiles = pluginsDir.listFiles((dir, name) -> name.startsWith("MimeLib"));
            if(mimeLibFiles != null) {
                for(File mimeLibFile1 : mimeLibFiles) {
                    if(!mimeLibFile1.getName().equalsIgnoreCase(newMimeLibFile.getName())) {
                        if(mimeLibFile1.delete()) {
                            getLogger().info("Old version deleted");
                        } else {
                            getLogger().warning("Unable to delete old version");
                        }
                    } else {
                        getLogger().info("Old version not deleted: new version ("+mimeLibFile1.getName()+")");
                    }
                }
            } else {
                getLogger().warning("Unable to delete old version: file not found");
            }
            getLogger().info("Successfully updated MimeLib!");
        }
        getLogger().info("MimeLib is now disabled");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6MimeLib &7- &eMimeLib v"+MimeLib.getMimeLibVersion()+" is now disabled"));
        getLogger().info("--------MIMELIB--------");
    }

    public static MimeLibPlugin getInstance() {
        return instance;
    }


    public @NotNull String downloadUpdates() throws IOException {
        if(this.isUpdateAvailable) {
            getLogger().info("Downloading updates...");
            File pluginsDir = new File("." + File.separator + "plugins"+File.separator+"_updates_");
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

    private void createLangConfig() {
        langConfigFile = new File(getDataFolder(), "lang.yml");
        if (!langConfigFile.exists()) {
            langConfigFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }

        langConfig = new YamlConfiguration();
        try {
            langConfig.load(langConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadLangConfig() {
        langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public String getUpdateVersion() {
        return updateVersion;
    }

    public boolean isCheckingUpdate() {
        return checkingUpdate;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&6MimeLib &7-&f");
    }
}

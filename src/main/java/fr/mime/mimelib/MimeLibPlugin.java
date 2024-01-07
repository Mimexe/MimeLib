package fr.mime.mimelib;

import static fr.mime.mimelib.MimeLib.InstallType;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public final class MimeLibPlugin extends JavaPlugin {
    private final ArrayList<String> plugins = new ArrayList<>();
    private static MimeLibPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
        plugins.add("Commandes");
    }

    private void checkPlugins() {
        HashMap<String, InstallType> isInstalled = new HashMap<>();
        for(String plugin : plugins) {
            isInstalled.put(plugin, MimeLib.getPluginInstallType(plugin));
        }
        getLogger().info("Supported plugins:");
        for(String plugin : plugins) {
            if(isInstalled.get(plugin) == InstallType.ENABLED) {
                getLogger().info(" - " + plugin + " : ENABLED");
            } else if (isInstalled.get(plugin) == InstallType.DISABLED) {
                getLogger().info(" - " + plugin + " : DISABLED");
            } else if (isInstalled.get(plugin) == InstallType.NOT_INSTALLED) {
                getLogger().info(" - " + plugin + " : NOT INSTALLED");
            } else {
                getLogger().info(" - " + plugin + " : UNKNOWN");
            }
        }
    }

    private void checkUpdates(){
        getLogger().info("Checking for updates...");
        new UpdateChecker(this, 1000).getVersion(version -> {
            if (!MimeLib.getMimeLibVersion().equalsIgnoreCase(version)) {
                getLogger().info("New version available: " + version+" (currently running "+MimeLib.getMimeLibVersion()+")");
                getLogger().info("Download it on https://www.spigotmc.org/resources/mimelib.1000/");
            } else {
                getLogger().info("MimeLib is up to date!");
            }
        });
    }

    private void printText() {
        getLogger().info("        _              _  _  _    ");
        getLogger().info(" _ __  (_) _ __   ___ | |(_)| |__ ");
        getLogger().info("| '  \\ | || '  \\ / -_)| || ||  _ \\");
        getLogger().info("|_|_|_||_||_|_|_|\\___||_||_||____/");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("--------MIMELIB--------");
        printText();
        checkPlugins();
        checkUpdates();
        getLogger().info(MimeLib.getPluginString(this));
        getLogger().info("--------MIMELIB--------");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MimeLibPlugin getInstance() {
        return instance;
    }


}

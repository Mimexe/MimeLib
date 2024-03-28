package fr.mime.mimelib;

import fr.mime.mimelib.menu.InventoryMenu;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
     * MimeLib is a library for Spigot/Paper plugins
     * It provides useful methods for plugin development
     * @version 1.0.0
     * @author Mime4X
 */
public class MimeLib {
    /**
        * Get the console sender
        * @return The console sender
        * @see Bukkit#getConsoleSender()
        * @since 1.0.1
     */
    public static @NotNull ConsoleCommandSender console() {
        return Bukkit.getConsoleSender();
    }

    /**
     * Create a head menu
     * @param openPlayer The player who will open the menu
     * @param players The players to display in the menu
     * @param menuName The name of the menu
     * @param slots The number of slots in the menu
     * @param nameTemplate The template for the player's name; use {player} to replace with the player's name
     * @return The head menu
     * @since 1.0.1
     */
    public static @NotNull InventoryMenu createHeadMenu(Player openPlayer, @NotNull Collection<? extends Player> players, String menuName, int slots, String nameTemplate) {
        InventoryMenu menu = new InventoryMenu(openPlayer, false, menuName, slots);
        int i = 0;
        for(Player player : players) {
            ItemStack head = getHead(player);
            String name = nameTemplate.replace("{player}", player.getName());
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if(meta == null) continue;
            meta.setDisplayName(name);
            head.setItemMeta(meta);
            menu.setItem(i, head);
            i++;
        }
        return menu;
    }

    /**
     * Create a head menu
     * @param openPlayer The player who will open the menu
     * @param players The players to display in the menu
     * @param menuName The name of the menu
     * @param slots The number of slots in the menu
     * @param nameTemplate The template for the player's name; use {player} to replace with the player's name
     * @param clickConsumer The consumer for the menu
     * @return The head menu
     * @since 1.0.1
     */
    public static @NotNull InventoryMenu createHeadMenu(Player openPlayer, @NotNull Collection<? extends Player> players, String menuName, int slots, String nameTemplate, Consumer<InventoryClickEvent> clickConsumer) {
        InventoryMenu menu = createHeadMenu(openPlayer, players, menuName, slots, nameTemplate);
        menu.setMenuHandler(clickConsumer);
        return menu;
    }
    
    /**
         * Disables a plugin
         * @param plugin The plugin to disable
         * @see PluginManager#disablePlugin(Plugin)
         * @since 1.0.0
     */
    public static void disable(Plugin plugin) {
        getPM().disablePlugin(plugin);
    }

    /**
         * Download a file from a URL
         * @param url The URL to download from
         * @param file The file to download to
         * @throws IOException If an I/O error occurs
         * @since 1.0.0
     */
    public static void downloadFile(String url, File file) throws IOException {
        FileUtils.copyURLToFile(new URL(url), file);
    }

    /**
        * Alias for {@link #downloadFile(String, File)}
        * @param url The URL to download from
        * @param path The path to download to
        * @throws IOException If an I/O error occurs
     */
    public static void downloadFile(String url, String path) throws IOException {
        downloadFile(url, new File(path));
    }

    /**
     * Get a player's head
     * @param player The player to get the head of
     * @return The player's head
     * @since 1.0.1
     */
    public static @NotNull ItemStack getHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if(meta == null) return head;
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        return head;
    }

    /**
        * Get the version of MimeLib, 
        * e.g., 1.0.0
        * @return The version of MimeLib
        * @since 1.0.0
        * @see PluginDescriptionFile#getVersion()
     */
    public static @NotNull String getMimeLibVersion() {
        return MimeLibPlugin.getInstance().getDescription().getVersion();
    }

    /**
        * Get the version of Minecraft,
        * e.g., 1.16.5
        * @return The version of Minecraft
        * @since 1.0.0
     */
    public static String getMinecraftVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    /**
     * Get the players without a player
     * @param p The player to exclude
     * @return The players without the player
     * @since 1.0.1
     */
    public static @NotNull Collection<? extends Player> getPlayersWithout(@NotNull Player p) {
        return getPlayersWithout(p.getUniqueId());
    }

    /**
     * Get the players without a UUID
     * @param uuid The UUID to exclude
     * @return The players without the UUID
     * @since 1.0.1
     */
    public static @NotNull Collection<? extends Player> getPlayersWithout(UUID uuid) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        players.removeIf(player -> player.getUniqueId().equals(uuid));
        return players;
    }

    /**
     * Get the players with a specific permission
     * @param perm The permission to check
     * @return The players with the permission
     * @since 1.0.1
     */
    public static @NotNull Collection<? extends Player> getPlayersWithPerm(@NotNull Permission perm) {
        return getPlayersWithPerm(perm.getName());
    }

    /**
     * Get the players with a specific permission
     * @param perm The permission to check
     * @return The players with the permission
     * @since 1.0.1
     */
    public static @NotNull Collection<? extends Player> getPlayersWithPerm(String perm) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        players.removeIf(player -> !player.hasPermission(perm));
        return players;
    }

    /**
        * Get if a plugin is installed/enabled/disabled
        * e.g., MimeLib.InstallType.ENABLED
        * @param pluginName The name of the plugin
        * @return The server type
        * @since 1.0.0
        * @see #getServerType()
     */
    public static InstallType getPluginInstallType(String pluginName) {
        if(isPluginEnabled(pluginName)) {
            return InstallType.ENABLED;
        } else if (isPluginInstalled(pluginName)) {
            return InstallType.DISABLED;
        } else {
            return InstallType.NOT_INSTALLED;
        }
    }

    /**
        * Get a plugin's formatted string
        * e.g., "MimeLib v1.0.0 by Mime4X running on Paper 1.20.1"
        * @param plugin The plugin
        * @return The plugin's formatted string
        * @since 1.0.0
     */
    public static @NotNull String getPluginString(@NotNull Plugin plugin) {
        PluginDescriptionFile pdf = plugin.getDescription();
        String authors;
        if(pdf.getAuthors().size() > 1) {
            StringBuilder authorsBuilder = new StringBuilder();
            for(String author : pdf.getAuthors()) {
                authorsBuilder.append(author).append(", ");
            }
            authors = authorsBuilder.toString();
        } else {
            authors = pdf.getAuthors().get(0);
        }
        return pdf.getName() + " v" + pdf.getVersion() + " by " + authors+" running on " + MimeLib.getServerType() + " " +MimeLib.getMinecraftVersion();
    }

    /**
        * Get the plugin manager
        * @return The plugin manager
        * @since 1.0.0
     */
    @NotNull
    public static PluginManager getPM() {
        return Bukkit.getPluginManager();
    }

    /**
        * Get the server type
        * e.g., Bukkit
        * @return The server type
        * @since 1.0.0
     */
    public static @NotNull String getServerType() {
        Pattern pattern = Pattern.compile("git-(\\w+)-\\d+ \\(MC: \\d+\\.\\d+\\.\\d+\\)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());

        if (matcher.matches()) {
            String serverType = matcher.group(1);
            return serverType.substring(0, 1).toUpperCase() + serverType.substring(1).toLowerCase();
        } else {
            MimeLibPlugin.getInstance().getLogger().warning("getServerType(): Unable to get server type");
            return "Unknown";
        }
    }

    /**
        * Get if the server is running Bukkit
        * @return If the server is running Bukkit
        * @since 1.0.0
     */
    public static boolean isBukkit() {
        return getServerType().equalsIgnoreCase("Bukkit");
    }

    /**
        * Get if the server is running Paper
        * @return If the server is running Paper
        * @since 1.0.0
     */
    public static boolean isPaper() {
        return getServerType().equalsIgnoreCase("Paper");
    }

    /**
        * Get if a plugin is enabled
        * @param pluginName The name of the plugin
        * @return If the plugin is enabled
        * @since 1.0.0
     */
    public static boolean isPluginEnabled(String pluginName) {
        return getPM().isPluginEnabled(pluginName);
    }

    /**
        * Get if a plugin is enabled
        * @param plugin The plugin
        * @return If the plugin is enabled
        * @since 1.0.0
     */
    public static boolean isPluginEnabled(Plugin plugin) {
        return getPM().isPluginEnabled(plugin);
    }

    /**
        * Get if a plugin is installed
        * @param pluginName The name of the plugin
        * @return If the plugin is installed
        * @since 1.0.0
     */
    public static boolean isPluginInstalled(String pluginName) {
        return getPM().getPlugin(pluginName) != null;
    }

    /**
        * Get if the server is running Spigot
        * @return If the server is running Spigot
        * @since 1.0.0
        * @see #getServerType()
     */
    public static boolean isSpigot() {
        return getServerType().equalsIgnoreCase("Spigot");
    }

    /**
        * Register a command
        * @param plugin The plugin to register the command to
        * @param name The name of the command
        * @param executor The command executor
        * @param completer The tab completer
        * @return If the command was registered
        * @since 1.0.0
     */
    public static boolean registerCommand(@NotNull JavaPlugin plugin, String name, CommandExecutor executor, TabCompleter completer) {
        PluginCommand command = plugin.getCommand(name);
        if(command == null) {
            return false;
        }
        command.setExecutor(executor);
        if(completer != null) command.setTabCompleter(completer);
        return true;
    }

    /**
        * Register a command
        * @param plugin The plugin to register the command to
        * @param name The name of the command
        * @param executor The command executor
        * @return If the command was registered
        * @since 1.0.0
     */
    public static boolean registerCommand(JavaPlugin plugin, String name, CommandExecutor executor) {
        return registerCommand(plugin, name, executor, null);
    }

    /**
        * Register a command
        * @param name The name of the command
        * @param executor The command executor
        * @return If the command was registered
        * @since 1.0.0
     */
    public static boolean registerCommand(String name, CommandExecutor executor) {
        return registerCommand(name, executor, null);
    }

    /**
        * Register a command
        * @param name The name of the command
        * @param executor The command executor
        * @param completer The tab completer
        * @return If the command was registered
        * @since 1.0.0
     */
    public static boolean registerCommand(String name, CommandExecutor executor, TabCompleter completer) {
        JavaPlugin plugin = null;
        int correspondance = 0;
        for (Plugin pl : getPM().getPlugins()) {
            if(pl.getDescription().getCommands().containsKey(name)) {
                plugin = (JavaPlugin) pl;
                correspondance++;
            }
        }
        if (correspondance == 0) {
            return false;
        } else if (correspondance > 1) {
            throw new RuntimeException("Multiple plugins have a command named " + name+", please use MimeLib#registerCommand(JavaPlugin, String, CommandExecutor, TabCompleter) instead");
        }
        return registerCommand(plugin, name, executor, completer);
    }

    /**
        * Register an event
        * @param listener The listener to register
        * @param plugin The plugin to register the listener to
        * @return If the event was registered
        * @since 1.0.1
     */
    public static boolean registerEvent(Listener listener, Plugin plugin) {
        getPM().registerEvents(listener, plugin);
        return true;
    }

    /**
        * The type of installation
        * @since 1.0.0
        * @see #getPluginInstallType(String)
     */
    public enum InstallType {
        /**
         * The plugin is enabled
         */
        ENABLED,
        /**
         * The plugin is disabled
         */
        DISABLED,
        /**
         * The plugin is not installed
         */
        NOT_INSTALLED;

        /**
         * Get the string representation of the type
         * @return The string representation of the type
         */
        @Override
        public @NotNull String toString() {
            return super.toString().toLowerCase().replace("_", " ");
        }

        /**
         * Get the type from a string
         * @param string The string to get the type from
         * @return The type from the string
         */
        public InstallType fromString(@NotNull String string) {
            return InstallType.valueOf(string.toUpperCase().replace(" ", "_"));
        }
    }
}

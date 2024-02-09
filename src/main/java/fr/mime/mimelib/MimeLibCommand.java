package fr.mime.mimelib;

import fr.mime.mimelib.hooks.PlugManXHook;
import fr.mime.mimelib.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The command for the plugin
 */
public class MimeLibCommand implements CommandExecutor, TabCompleter {

    /**
     * The command for the plugin
     * @param sender the sender of the command
     * @param command the command
     * @param s the label of the command
     * @param args the arguments of the command
     * @return if the command is successfully executed
     */
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] args) {
        if (args.length == 0) {
            help(sender);
            return false;
        }
        if (args[0].equalsIgnoreCase("help")) {
            help(sender);
            return true;
        }
        if(MimeLibPlugin.getInstance().devmode(sender)) {
            if (args[0].equalsIgnoreCase("forceupdate")) {
                sender.sendMessage("§c§lWARNING: §4Forcing update...");
                return runUpdate(sender, true);
            }
            if(args[0].equalsIgnoreCase("testlang")) {
                if(args.length == 2 && args[1].equalsIgnoreCase("simulate_update")) {
                    sender.sendMessage("§cSimulating update...");
                    MimeLibPlugin.getInstance().simulateUpdate();
                }
                if(!(sender instanceof Player p)) {
                    sender.sendMessage("§cNot a player, placeholderapi for player will not work");
                    sender.sendMessage(Lang.get("test"));
                    return true;
                }
                sender.sendMessage(Lang.get("test", p));
                return true;
            }
            if(args[0].equalsIgnoreCase("fullReload")) {
                sender.sendMessage("§c§lWARNING: §4Full reload...");
                PlugManXHook hook = MimeLibPlugin.getInstance().getHooks().getPlugmanHook();
                if(!hook.isEnabled()) {
                    sender.sendMessage("§c§lCannot full reload, PlugManX is required.");
                    return false;
                }
                hook.get().reload(MimeLibPlugin.getInstance());
                return true;
            }
        } else {
            help(sender);
        }
        if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(Lang.get("command.version"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            MimeLibPlugin.getInstance().reloadConfig();
            MimeLibPlugin.getInstance().checkConfigDefaults();
            sender.sendMessage(Lang.get("command.reload"));
            return true;
        }
        if (args[0].equalsIgnoreCase("update")) {
            if(!MimeLibPlugin.getInstance().getConfig().getBoolean("updatechecker.enabled")) {
                sender.sendMessage(Lang.get("command.update.disabled"));
                return false;
            }
            if (args.length == 1) {
                help(sender);
                return false;
            }
            if (args[1].equalsIgnoreCase("check")) {
                sender.sendMessage(Lang.get("command.update.checking"));
                Thread checkUpdatesThread = new Thread(() -> {
                    MimeLibPlugin.getInstance().checkUpdates();
                    while (MimeLibPlugin.getInstance().isCheckingUpdate()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (MimeLibPlugin.getInstance().isUpdateAvailable()) {
                        sender.sendMessage(Lang.get("command.update.available"));
                    } else {
                        sender.sendMessage(Lang.get("command.update.latest"));
                    }
                });
                checkUpdatesThread.start();
                return true;
            }
            if (args[1].equalsIgnoreCase("download")) {
                return runUpdate(sender, false);
            }
            help(sender);
            return false;
        }
        return true;
    }

    /**
     * Run the update
     * @param sender the sender of the command
     * @param force if the update should be forced
     * @return if the update is successful
     */
    private boolean runUpdate(@Nonnull CommandSender sender, boolean force) {
        sender.sendMessage(Lang.get("command.download.start"));

        try {
            if(force) {
                MimeLibPlugin.getInstance().downloadUpdates();
                updateSuccess(sender);
                return true;
            }

            String returnCode = MimeLibPlugin.getInstance().downloadUpdates();
            if(returnCode.equalsIgnoreCase("SUCCESS")) {
                updateSuccess(sender);
            } else if (returnCode.equalsIgnoreCase("NO_UPDATES")) {
                sender.sendMessage(Lang.get("command.update.latest"));
            } else {
                sender.sendMessage(Lang.get("command.download.error"));
                return false;
            }
        } catch (IOException e) {
            MimeLibPlugin.getInstance().getLogger().warning("Unable to download updates: "+e.getMessage());
            sender.sendMessage(Lang.get("command.download.error"));
            return false;
        }
        return true;
    }

    /**
     * The update is successful
     * @param sender the sender of the command
     */
    private void updateSuccess(@Nonnull CommandSender sender) {
        sender.sendMessage(Lang.get("command.download.success"));
        if(MimeLibPlugin.getInstance().getConfig().getBoolean("updatechecker.auto-restart") && sender instanceof ConsoleCommandSender) {
            Bukkit.broadcastMessage(Lang.get("command.download.restart"));
            Bukkit.getScheduler().runTaskLater(MimeLibPlugin.getInstance(), () -> {
                Bukkit.broadcastMessage("§c§lRestarting!!!");
                Bukkit.getScheduler().runTaskLater(MimeLibPlugin.getInstance(), () -> {
                    String cmd = MimeLibPlugin.getInstance().getConfig().getString("updatechecker.restart-command");
                    if(cmd == null) {
                        cmd = "restart";
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }, 20L);
            }, 20L * 5);
        }
    }

    /**
     * Show the help
     * @param sender the sender of the command
     */
    private void help(@NotNull CommandSender sender) {
        sender.sendMessage(Lang.get("command.help.header"));
        sender.sendMessage(Lang.get("command.help.commands"));
        if(MimeLibPlugin.getInstance().devmode(sender)) sender.sendMessage(Lang.get("command.help.commands_devmode"));
        sender.sendMessage(Lang.get("command.help.footer"));
    }

    /**
     * The tab completer for the command
     * @param sender the sender of the command
     * @param command the command
     * @param label the label of the command
     * @param args the arguments of the command
     * @return the list of tab completions
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> tab = new ArrayList<>();
        if(args.length == 1) {
            tab.add("help");
            tab.add("version");
            if(sender.hasPermission("mimelib.reload")) tab.add("reload");
            if(sender.hasPermission("mimelib.update.check") || sender.hasPermission("mimelib.update.download") || sender.hasPermission("mimelib.update.notify")) tab.add("update");
            if(MimeLibPlugin.getInstance().devmode(sender)) {
                tab.add("forceupdate");
                tab.add("testlang");
                tab.add("fullReload");
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("update")) {
                if(sender.hasPermission("mimelib.update.check")) tab.add("check");
                if(sender.hasPermission("mimelib.update.download")) tab.add("download");
            }
            if (args[0].equalsIgnoreCase("testlang")) {
                tab.add("simulate_update");
            }
        }
        return tab;
    }
}

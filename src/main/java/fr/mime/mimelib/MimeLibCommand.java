package fr.mime.mimelib;

import fr.mime.mimelib.test.TestMenu;
import fr.mime.mimelib.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MimeLibCommand implements CommandExecutor {

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
        if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(Lang.get("command.version"));
            return true;
        }
        if(args[0].equalsIgnoreCase("test")) {
            if(!(sender instanceof Player p)) {
                sender.sendMessage("§cYou must be a player to use this command");
                return false;
            }
            new TestMenu(p).open();
            return true;
        }
//        if(args[0].equalsIgnoreCase("forceupdate")) {
//            sender.sendMessage("§c§lWARNING: §4Forcing update...");
//            return runUpdate(sender);
//        }
        if (args[0].equalsIgnoreCase("reload")) {
            MimeLibPlugin.getInstance().reloadConfig();
            MimeLibPlugin.getInstance().reloadLangConfig();
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
                            Thread.sleep(100);
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
                return runUpdate(sender);
            }
            help(sender);
            return false;
        }
        return true;
    }

    private boolean runUpdate(@Nonnull CommandSender sender) {
        sender.sendMessage(Lang.get("command.download.start"));
        try {
            MimeLibPlugin.getInstance().downloadUpdates();
            sender.sendMessage(Lang.get("command.download.success"));
            if(MimeLibPlugin.getInstance().getConfig().getBoolean("updatechecker.auto-restart")) {
                Bukkit.broadcastMessage(Lang.get("command.download.restart"));
                Bukkit.getScheduler().runTaskLater(MimeLibPlugin.getInstance(), () -> {
                    Bukkit.broadcastMessage("§c§lRestarting!!!");
                    Bukkit.getScheduler().runTaskLater(MimeLibPlugin.getInstance(), () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    }, 20L);
                }, 20L * 5);
            }
        } catch (IOException e) {
            MimeLibPlugin.getInstance().getLogger().warning("Unable to download updates: "+e.getMessage());
            sender.sendMessage(Lang.get("command.download.error"));
            return false;
        }
        return true;
    }

    private void help(@NotNull CommandSender sender) {
        sender.sendMessage(Lang.get("command.help.header"));
        sender.sendMessage(Lang.get("command.help.commands"));
        sender.sendMessage(Lang.get("command.help.footer"));
    }
}

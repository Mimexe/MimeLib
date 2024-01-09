package fr.mime.mimelib;

import fr.mime.mimelib.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
                sender.sendMessage(Lang.get("command.download.start"));
                try {
                    String returnCode = MimeLibPlugin.getInstance().downloadUpdates();
                    sender.sendMessage(returnCode);
                    if(returnCode.equals("SUCCESS")){
                        sender.sendMessage(Lang.get("command.download.success"));
                    } else if (returnCode.equals("NO_UPDATES")) {
                        sender.sendMessage(Lang.get("command.update.latest"));
                    }
                } catch (IOException e) {
                    MimeLibPlugin.getInstance().getLogger().warning("Unable to download updates: "+e.getMessage());
                    sender.sendMessage(Lang.get("command.download.error"));
                    return false;
                }
                return true;
            }
            help(sender);
            return false;
        }
        return true;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(Lang.get("command.help.header"));
        sender.sendMessage(Lang.get("command.help.commands"));
        sender.sendMessage(Lang.get("command.help.footer"));
    }
}

package fr.mime.mimelib.listeners;

import fr.mime.mimelib.MimeLibPlugin;
import fr.mime.mimelib.utils.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(MimeLibPlugin.getInstance().isUpdateAvailable()) {
            p.sendMessage(Lang.get("command.update.available"));
        }
    }
}

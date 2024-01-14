package fr.mime.mimelib.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(@NotNull InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof InventoryMenu menu) {
            if(!menu.isDontCancel()) {
                e.setCancelled(true);
            }
            if (e.getCurrentItem() == null) {
                return;
            }
            menu.handleMenu(e);
        }
    }
}

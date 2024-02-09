package fr.mime.mimelib.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Class for managing the menu listener
 */
public class MenuListener implements Listener {
    /**
     * Handle the menu click
     * @param e the event
     */
    @EventHandler
    public void onMenuClick(@NotNull InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof AbstractInventoryMenu menu) {
            if(!menu.isDontCancel()) {
                e.setCancelled(true);
            }
            if (e.getCurrentItem() == null) {
                return;
            }
            menu.handleMenu(e);
        } else if (holder instanceof InventoryMenu menu) {
            if(!menu.isDontCancel()) {
                e.setCancelled(true);
            }
            if (e.getCurrentItem() == null) {
                return;
            }
            menu.consumer.accept(e);
        }
    }
}

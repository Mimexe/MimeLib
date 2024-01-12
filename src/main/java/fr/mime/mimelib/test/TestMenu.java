package fr.mime.mimelib.test;

import fr.mime.mimelib.menu.InventoryMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TestMenu extends InventoryMenu {

    public TestMenu(Player p) {
        super(p);
    }

    @Override
    public String getMenuName() {
        return "§cTest Menu";
    }

    @Override
    public int getSlots() {
        return Slots.LINE;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(checkItem(e, "§cTest Item")) {
            p.sendMessage("§cTest Item Clicked");
        } else if (checkItem(e, "§aOther test item")) {
            p.sendMessage("§aOther Test Item Clicked");
        }
    }

    @Override
    public void setMenuItems() {
        setItem(3, makeItem(Material.DIAMOND, "§cTest Item", "§7This is a test item"));
        setItem(4, makeItem(Material.DIAMOND_BLOCK, "§9Take this", "§7You can take this item"));
        setItem(5, makeItem(Material.EMERALD, "§aOther test item", "§7This is another test item"));
        freeSlot(4);
        this.fillRemaining(Material.RED_STAINED_GLASS_PANE);
    }
}

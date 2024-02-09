package fr.mime.mimelib.menu;

import fr.mime.mimelib.MimeLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Abstract class for creating an inventory menu
 * @see InventoryMenu
 * @see MimeLib#createHeadMenu(Player, Collection, String, int, String)
 */
public abstract class AbstractInventoryMenu implements InventoryHolder {
    /**
     * The inventory of the menu
     */
    protected Inventory inventory;
    /**
     * The player who opened the menu
     */
    protected Player player;
    /**
     * If the menu should cancel the click event
     */
    protected boolean dontCancel = false;

    /**
     * Constructor for the menu
     * @param p the player who opened the menu
     */
    public AbstractInventoryMenu(Player p) {
        this.player = p;
    }

    /**
     * Constructor for the menu
     * @param p the player who opened the menu
     * @param dontCancel if the menu should cancel the click event
     */
    public AbstractInventoryMenu(Player p, boolean dontCancel) {
        this.player = p;
        this.dontCancel = dontCancel;
    }

    /**
     * Get the name of the menu
     * @return the name of the menu
     */
    public abstract String getMenuName();

    /**
     * Get the number of slots of the menu
     * @return the number of slots of the menu
     */
    public abstract int getSlots();

    /**
     * Handle the click event of the menu
     * @param e the click event
     */
    public abstract void handleMenu(InventoryClickEvent e);

    /**
     * Set the items of the menu
     */
    public abstract void setMenuItems();

    /**
     * Open the menu
     */
    public void open() {
        this.inventory = Bukkit.createInventory(this, this.getSlots(), this.getMenuName());
        this.setMenuItems();
        this.player.openInventory(this.inventory);
    }


    /**
     * Get the inventory of the menu
     * @return the inventory of the menu
     */
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Set an item in the inventory
     * @param slot the slot of the item
     * @param item the item to set
     */
    public void setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    /**
     * Create an item with this utility method
     * @param material the material of the item
     * @param displayName the name of the item
     * @param lore the lore of the item
     * @return the item
     */
    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Fill the remaining slots of the inventory with a specific item
     * @param material the material of the item
     */
    public void fillRemaining(Material material) {
        for (int i = 0; i < this.getSlots(); i++) {
            if (this.inventory.getItem(i) == null || Objects.requireNonNull(this.inventory.getItem(i)).getType().equals(Material.AIR)) {
                this.inventory.setItem(i, new ItemStack(material));
            }
        }
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param item the item to check
     * @param name the name of the item
     * @return if the item is the same as the one in the inventory
     */
    public boolean checkItem(@NotNull ItemStack item, String name) {
        return Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(name);
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param item the item to check
     * @param name the name of the item
     * @param lore the lore of the item
     * @return if the item is the same as the one in the inventory
     */
    public boolean checkItem(@NotNull ItemStack item, String name, String... lore) {
        return check(name, item, lore);
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param item the item to check
     * @param lore the lore of the item
     * @return if the item is the same as the one in the inventory
     */
    public boolean checkItem(@NotNull ItemStack item, String @NotNull ... lore) {
        return checkLore(item, lore);
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param e the click event
     * @param name the name of the item
     * @return if the item is the same as the one in the inventory
     */
    public boolean checkItem(@NotNull InventoryClickEvent e, String name) {
        ItemStack item = e.getCurrentItem();
        assert item != null;
        return Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(name);
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param e the click event
     * @param name the name of the item
     * @param lore the lore of the item
     * @return if the item is the same as the one in the inventory
     */
    public boolean checkItem(@NotNull InventoryClickEvent e, String name, String... lore) {
        ItemStack item = e.getCurrentItem();
        assert item != null;
        return check(name, item, lore);
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param name the name of the item
     * @param item the item to check
     * @param lore the lore of the item
     * @return if the item is the same as the one in the inventory
     */
    private boolean check(String name, @NotNull ItemStack item, String[] lore) {
        if(!Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(name)) {
            return false;
        }
        ArrayList<String> itemLore = new ArrayList<>(Objects.requireNonNull(item.getItemMeta().getLore()));
        for(String s : lore) {
            if(!itemLore.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param e the click event
     * @param lore the lore of the item
     * @return if the item is the same as the one in the inventory
     */
    public boolean checkItem(@NotNull InventoryClickEvent e, String @NotNull ... lore) {
        ItemStack item = e.getCurrentItem();
        return checkLore(item, lore);
    }

    /**
     * Check if the item is the same as the one in the inventory
     * @param item the item to check
     * @param lore the lore of the item
     * @return if the item is the same as the one in the inventory
     */
    private boolean checkLore(@NotNull ItemStack item, String @NotNull [] lore) {
        ArrayList<String> itemLore = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore()));
        for(String s : lore) {
            if(!itemLore.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if this menu should cancel the click event
     * @return if this menu should cancel the click event
     */
    public boolean isDontCancel() {
        return dontCancel;
    }

    /**
     * Predefined slots for inventory sizes
     * @see AbstractInventoryMenu#getSlots()
     * @see InventoryMenu
     */
    public static class Slots {
        /**
         * One line inventory
         */
        public static final int LINE = 9;
        /**
         * Three lines inventory
         */
        public static final int CHEST = 27;
        /**
         * Six lines inventory
         */
        public static final int DOUBLE_CHEST = 54;
    }
}

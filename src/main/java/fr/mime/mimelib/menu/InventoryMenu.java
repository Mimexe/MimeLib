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
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class for creating an inventory menu
 * @see AbstractInventoryMenu
 * @see MimeLib#createHeadMenu(Player, java.util.Collection, String, int, String)
 */
public class InventoryMenu implements InventoryHolder {
    /**
     * The inventory of the menu
     */
    protected Inventory inventory;
    /**
     * The player who opens the menu
     */
    protected Player player;
    /**
     * If the menu should cancel the click event
     */
    protected boolean dontCancel = false;
    /**
     * The name of the menu
     */
    protected String menuName = null;
    /**
     * The slots of the menu
     */
    protected int slots = 0;
    /**
     * The consumer of the menu
     */
    protected Consumer<InventoryClickEvent> consumer = null;
    /**
     * The items of the menu
     */
    protected HashMap<Integer, ItemStack> items = new HashMap<>();

    /**
     * Constructor for the menu
     * @param p the player who opens the menu
     */
    public InventoryMenu(Player p) {
        new InventoryMenu(p, false);
    }

    /**
     * Constructor for the menu
     * @param p the player who opens the menu
     * @param dontCancel if the menu should cancel the click event
     */
    public InventoryMenu(Player p, boolean dontCancel) {
        this.player = p;
        this.dontCancel = dontCancel;
    }

    /**
     * Constructor for the menu
     * @param p the player who opens the menu
     * @param dontCancel if the menu should cancel the click event
     * @param menuName the name of the menu
     */
    public InventoryMenu(Player p, boolean dontCancel, String menuName) {
        this.player = p;
        this.dontCancel = dontCancel;
        this.menuName = menuName;
    }

    /**
     * Constructor for the menu
     * @param p the player who opens the menu
     * @param dontCancel if the menu should cancel the click event
     * @param menuName the name of the menu
     * @param slots the slots of the menu
     */
    public InventoryMenu(Player p, boolean dontCancel, String menuName, int slots) {
        this.player = p;
        this.dontCancel = dontCancel;
        this.menuName = menuName;
        this.slots = slots;
    }

    /**
     * Set the name of the menu
     * @param menuName the name of the menu
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * Set the slots of the menu
     * @param slots the slots of the menu
     */
    public void setSlots(int slots) {
        this.slots = slots;
    }

    /**
     * Set the consumer of the menu
     * @param consumer the consumer of the menu
     */
    public void setMenuHandler(Consumer<InventoryClickEvent> consumer) {
        this.consumer = consumer;
    }

    /**
     * Open the menu
     */
    public void open() {
        if(this.menuName == null) {
            throw new NullPointerException("Menu name is null");
        }
        if(this.slots == 0) {
            throw new NullPointerException("Slots is 0");
        }
        this.inventory = Bukkit.createInventory(this, this.slots, this.menuName);
        if(!this.items.isEmpty()) {
            for(Integer slot : this.items.keySet()) {
                ItemStack item = this.items.get(slot);
                if(item == null) continue;
                this.inventory.setItem(slot, item);
            }
        }
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
     * @param item the item
     */
    public void setItem(int slot, ItemStack item) {
        if(this.inventory == null) {
            this.items.remove(slot);
            this.items.put(slot, item);
            return;
        }
        this.inventory.setItem(slot, item);
    }

    /**
     * Set the items of the menu
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
        for (int i = 0; i < this.slots; i++) {
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
     * Is the menu canceling the click event?
     * @return if the menu is canceling the click event
     */
    public boolean isDontCancel() {
        return dontCancel;
    }

    /**
     * Predefined slots for inventories
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

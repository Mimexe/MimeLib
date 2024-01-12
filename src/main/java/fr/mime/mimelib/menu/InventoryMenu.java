package fr.mime.mimelib.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public abstract class InventoryMenu implements InventoryHolder {
    protected Inventory inventory;
    protected Player player;
    protected boolean dontCancel = false;
    protected ArrayList<Integer> freeSlots = new ArrayList<>();

    public InventoryMenu(Player p) {
        this.player = p;
    }

    public InventoryMenu(Player p, boolean dontCancel) {
        this.player = p;
        this.dontCancel = dontCancel;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open() {
        this.inventory = Bukkit.createInventory(this, this.getSlots(), this.getMenuName());
        this.setMenuItems();
        this.player.openInventory(this.inventory);
    }

    public void freeSlot(int slot) {
        this.freeSlots.add(slot);
    }
    public void blockSlot(int slot) {
        this.freeSlots.remove(slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public void fillRemaining(Material material) {
        for (int i = 0; i < this.getSlots(); i++) {
            if (this.inventory.getItem(i) == null || Objects.requireNonNull(this.inventory.getItem(i)).getType().equals(Material.AIR)) {
                this.inventory.setItem(i, new ItemStack(material));
            }
        }
    }

    public boolean checkItem(@NotNull ItemStack item, String name) {
        return Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(name);
    }

    public boolean checkItem(@NotNull ItemStack item, String name, String... lore) {
        return check(name, item, lore);
    }

    public boolean checkItem(@NotNull ItemStack item, String @NotNull ... lore) {
        return checkLore(item, lore);
    }

    public boolean checkItem(@NotNull InventoryClickEvent e, String name) {
        ItemStack item = e.getCurrentItem();
        assert item != null;
        return Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(name);
    }

    public boolean checkItem(@NotNull InventoryClickEvent e, String name, String... lore) {
        ItemStack item = e.getCurrentItem();
        assert item != null;
        return check(name, item, lore);
    }

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

    public boolean checkItem(@NotNull InventoryClickEvent e, String @NotNull ... lore) {
        ItemStack item = e.getCurrentItem();
        return checkLore(item, lore);
    }

    private boolean checkLore(@NotNull ItemStack item, String @NotNull [] lore) {
        ArrayList<String> itemLore = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore()));
        for(String s : lore) {
            if(!itemLore.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDontCancel() {
        return dontCancel;
    }

    public ArrayList<Integer> getFreeSlots() {
        return freeSlots;
    }

    public static class Slots {
        public static final int LINE = 9;
        public static final int CHEST = 27;
        public static final int DOUBLE_CHEST = 54;
    }
}

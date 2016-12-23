/*
 * Copyright (C) 2013-2016 XHawk87
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.xhawk87.Coinage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author XHawk87
 */
public class Denomination implements Comparable<Denomination> {

    private Currency parent;
    private ConfigurationSection data;

    public Denomination(Currency parent, ConfigurationSection data) {
        this.parent = parent;
        this.data = data;
        String alias = getAlias();
        if (alias.contains("&")) {
            alias = alias.replace('&', ChatColor.COLOR_CHAR);
            data.set("alias", alias);
        }
        String print = getPrint();
        if (print.contains("&")) {
            print = print.replace('&', ChatColor.COLOR_CHAR);
            data.set("print", print);
        }
    }

    /**
     * Deletes this denomination. This will cause all coins with this
     * denomination to no longer be considered legal tender, however it will not
     * remove them from the game
     */
    public void delete() {
        parent.deleteDenomination(this);
    }

    /**
     * Get the currency this denomination is in
     *
     * @return The currency
     */
    public Currency getCurrency() {
        return parent;
    }

    /**
     * Get the name of this currency used in commands
     *
     * @return The name of this denomination
     */
    public String getName() {
        return data.getName();
    }

    /**
     * Get the alias of this denomination used to set the display name for the
     * item.
     *
     * @return The alias of this denomination
     */
    public final String getAlias() {
        return data.getString("alias");
    }

    /**
     * Gets the print of this denomination used after the currency alias in the
     * lore for the item
     *
     * @return The print on this denomination
     */
    public final String getPrint() {
        return data.getString("print");
    }

    /**
     * Gets the value of this denomination in whole units of the currency
     *
     * @return The value of this denomination
     */
    public int getValue() {
        return data.getInt("value");
    }

    /**
     * Gets the ID of the item used for this denomination
     *
     * @return The item ID for this denomination
     */
    public int getItemId() {
        return data.getInt("item-id");
    }

    /**
     * Gets the data value of the item used for this denomination
     *
     * @return The item data for this denomination
     */
    public short getItemData() {
        return (short) data.getInt("item-data", 0);
    }

    @Override
    public String toString() {
        return getAlias() + ChatColor.RESET + ": " + parent.getAlias() + getPrint() + ChatColor.RESET;
    }

    /**
     * Creates a stack of a given number of coins on this denomination.
     *
     * The number will be capped to the maximum stack size for the material.
     *
     * @param amount The number of coins
     * @return The stack of coins
     */
    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(getItemId(), amount, getItemData());
        int maxStackSize = item.getMaxStackSize();
        if (maxStackSize != -1 && item.getAmount() > maxStackSize) {
            item.setAmount(maxStackSize);
        }
        mint(item);
        return item;
    }

    /**
     * Mints an existing item with printing of this denomination
     *
     * @param item The item to mint
     * @return The minted coin
     */
    public boolean mint(ItemStack item) {
        if (item.getTypeId() != getItemId()
                || item.getDurability() != getItemData()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName() || meta.hasLore()) {
            return false;
        }
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        meta.setDisplayName(getAlias());
        List<String> lore = new ArrayList<>();
        lore.add(getCurrency().getAlias() + getPrint());
        meta.setLore(lore);
        item.setItemMeta(meta);

        return true;
    }

    /**
     * Checks if a given item is a coin of this denomination
     *
     * @param item The item to check
     * @return True if it matches, otherwise false
     */
    public boolean matches(ItemStack item) {
        if (item.getTypeId() != getItemId()
                || item.getDurability() != getItemData()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName() || !meta.hasLore() || meta.getLore().size() != 1) {
            return false;
        }
        String print = meta.getLore().get(0);

        return meta.getDisplayName().equals(getAlias()) && print.equals(getCurrency().getAlias() + getPrint());
    }

    /**
     * Returns the maximum stack size of the material of this coin if known
     *
     * @return The maximum stack size or -1 if unknown
     */
    public int getMaxStackSize() {
        Material material = Material.getMaterial(getItemId());
        if (material != null) {
            return material.getMaxStackSize();
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Denomination) {
            Denomination other = (Denomination) obj;
            return this.getName().equals(other.getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getName());
    }

    @Override
    public int compareTo(Denomination other) {
        return this.getName().compareTo(other.getName());
    }
}

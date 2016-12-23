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
package me.xhawk87.Coinage.listeners;

import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.moneybags.MoneyBag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author XHawk87
 */
public class MoneyBagListener implements Listener {

    private Coinage plugin;

    public void registerEvents(Coinage plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onOpenMoneyBag(BlockPlaceEvent event) {
        MoneyBag moneybag = plugin.getMoneyBag(event.getItemInHand());
        if (moneybag != null) {
            event.getPlayer().openInventory(moneybag.getInventory());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onOpenMoneyBag(PlayerInteractEvent event) {
        if (event.hasItem()) {
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    MoneyBag moneybag = plugin.getMoneyBag(event.getItem());
                    if (moneybag != null) {
                        event.getPlayer().openInventory(moneybag.getInventory());
                        event.setCancelled(true);
                    }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUpdateMoneyBag(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof MoneyBag) {
            final MoneyBag moneybag = (MoneyBag) event.getView().getTopInventory().getHolder();
            final Inventory out = event.getView().getBottomInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    moneybag.checkCoins(out);
                }
            }.runTask(plugin);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void noCraftingWithMoneyBags(CraftItemEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getTypeId() == 0) {
                continue;
            }

            if (plugin.getMoneyBag(item) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }
}

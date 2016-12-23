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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author XHawk87
 */
public class CoinListener implements Listener {

    private Coinage plugin;

    public void registerEvents(Coinage plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void noCraftingWithCoins(CraftItemEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null) {
                continue;
            }

            if (plugin.getDenominationOfCoin(item) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void noUsingCoins(PlayerInteractEvent event) {
        if (event.hasItem() && plugin.getDenominationOfCoin(event.getItem()) != null) {
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void noUsingCoins(BlockBreakEvent event) {
        ItemStack held = event.getPlayer().getItemInHand();
        if (held != null && plugin.getDenominationOfCoin(held) != null) {
            event.setCancelled(true);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

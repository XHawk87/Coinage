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
package me.xhawk87.Coinage.commands;

import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.Currency;
import me.xhawk87.Coinage.Denomination;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author XHawk87
 */
public class CoinValueCommand extends CoinCommand {
    
    private Coinage plugin;
    
    public CoinValueCommand(Coinage plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getPermission() {
        return "coinage.commands.coinvalue";
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 2) {
            return false;
        }
        
        Player player;
        Currency currency = null;
        if (args.length == 0) {
            player = (Player) sender;
        } else {
            String lastArg = args[args.length - 1];
            currency = plugin.getCurrency(lastArg);
            if (currency != null) {
                if (args.length == 2) {
                    sender.sendMessage("There is no currency with id " + lastArg);
                    return true;
                } else {
                    player = plugin.getServer().getPlayer(lastArg);
                    if (player == null) {
                        sender.sendMessage("There is no player or currency matching " + lastArg);
                        return true;
                    }
                }
            } else if (args.length == 2) {
                String firstArg = args[0];
                player = plugin.getServer().getPlayer(lastArg);
                if (player == null) {
                    sender.sendMessage("There is no player matching " + firstArg);
                    return true;
                }
            } else {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage("Console must specify a player");
                    return true;
                }
            }
        }
        
        if (currency == null) {
            ItemStack held = player.getItemInHand();
            Denomination denomination = plugin.getDenominationOfCoin(held);
            if (denomination != null) {
                currency = denomination.getCurrency();
                int totalValue = held.getAmount() * denomination.getValue();
                player.sendMessage("You are holding " + totalValue + " in " + currency.toString() + " in your hand");
                return true;
            }
            currency = plugin.getDefaultCurrency();
        }
        
        int totalValue = currency.getCoinCount(player.getInventory());
        player.sendMessage("You have " + totalValue + " in " + currency.toString() + " in your inventory");
        return true;
    }
}

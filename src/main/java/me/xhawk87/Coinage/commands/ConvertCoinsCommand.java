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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author XHawk87
 */
public class ConvertCoinsCommand extends CoinCommand {

    private Coinage plugin;

    public ConvertCoinsCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/ConvertCoins ([player]) [from currency] [amount] [to currency] [amount]. Converts coins from one currency to another in the specified amounts. This effectively removes the specified amount of the first currency and gives the specified amount in the second currency to the given player";
    }

    @Override
    public String getPermission() {
        return "coinage.commands.convertcoins";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 4 || args.length > 5) {
            return false;
        }

        int index = 0;
        Player player;
        Currency from;
        int fromAmount;
        Currency to;
        int toAmount;
        if (args.length == 5) {
            String playerName = args[index++];
            player = plugin.getServer().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage("There is no player matching " + playerName);
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

        String fromCurrencyName = args[index++];
        String fromAmountString = args[index++];
        String toCurrencyName = args[index++];
        String toAmountString = args[index++];

        from = plugin.getCurrency(fromCurrencyName);
        if (from == null) {
            sender.sendMessage("There is no currency with id " + fromCurrencyName);
            return true;
        }

        try {
            fromAmount = Integer.parseInt(fromAmountString);
            if (fromAmount < 1) {
                sender.sendMessage("from amount must be positive: " + fromAmountString);
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("from amount was not a valid number: " + fromAmountString);
            return true;
        }

        to = plugin.getCurrency(toCurrencyName);
        if (to == null) {
            sender.sendMessage("There is no currency with id " + toCurrencyName);
            return true;
        }

        try {
            toAmount = Integer.parseInt(toAmountString);
            if (toAmount < 1) {
                sender.sendMessage("to amount must be positive: " + toAmountString);
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("from amount was not a valid number: " + toAmountString);
            return true;
        }

        if (!from.spend(player.getInventory(), fromAmount)) {
            if (sender != player) {
                sender.sendMessage(player.getDisplayName() + " does not have enough " + from.toString() + " to afford this transaction");
            }
            player.sendMessage("You do not have enough " + from.toString() + " to afford this transaction");
            return true;
        }

        if (!to.give(player.getInventory(), toAmount)) {
            sender.sendMessage("An error occurred. Please see console for details");
            return true;
        }
        if (sender != player) {
            sender.sendMessage(fromAmount + " " + from.toString() + " was converted into " + toAmount + " " + to.toString() + " for " + player.getDisplayName());
        }
        player.sendMessage(fromAmount + " " + from.toString() + " was converted into " + toAmount + " " + to.toString());
        return true;
    }
}

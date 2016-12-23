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
public class GiveCurrency extends CoinCommand {

    private Coinage plugin;

    public GiveCurrency(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/GiveCurrency [value] - Gives the specified value in the default currency to the player issuing the command\n"
                + "/GiveCurrency [currency] [value] - Gives the specified value in the given currency to the player issuing the command\n"
                + "/GiveCurrency [player] [amount] - Gives the specified value in the default currency to the given player\n"
                + "/GiveCurrency [player] [currency] [value] - Gives the specified value in the given currency to the given player";
    }

    @Override
    public String getPermission() {
        return "coinage.commands.givecoins";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 3) {
            return false;
        }

        int index = 0;
        Player player = null;
        Currency currency;

        if (args.length >= 2) {
            String firstArg = args[index++];
            if (args.length == 3) {
                player = plugin.getServer().getPlayer(firstArg);
                if (player == null) {
                    sender.sendMessage("There is no player matching " + firstArg);
                    return true;
                }
                String currencyName = args[index++];
                currency = plugin.getCurrency(currencyName);
                if (currency == null) {
                    sender.sendMessage("There is no currency with name " + currencyName);
                    return true;
                }
            } else {
                currency = plugin.getCurrency(firstArg);
                if (currency == null) {
                    player = plugin.getServer().getPlayer(firstArg);
                    if (player == null) {
                        sender.sendMessage("There is no matching player or currency with name " + firstArg);
                        return true;
                    }
                    currency = plugin.getDefaultCurrency();
                }
            }
        } else {
            currency = plugin.getDefaultCurrency();
        }

        if (player == null) {
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("The console must specify a player");
                return true;
            }
        }

        int totalValue;
        String valueString = args[index++];
        try {
            totalValue = Integer.parseInt(valueString);
            if (totalValue < 1) {
                sender.sendMessage("The value must be a positive number: " + valueString);
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("The value must be a valid number: " + valueString);
            return true;
        }

        if (currency.give(player, totalValue)) {
            sender.sendMessage(totalValue + " in " + currency.toString() + " was given to " + player.getDisplayName());
        } else {
            sender.sendMessage("Failed to give " + totalValue + " in " + currency.toString() + " to " + player.getDisplayName() + " as the correct amount could not be made out. Perhaps there should be a single-unit denomination so this type of thing cannot happen?");
        }
        return true;
    }
}

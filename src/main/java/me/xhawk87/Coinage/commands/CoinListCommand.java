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

import java.util.List;
import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.Currency;
import me.xhawk87.Coinage.Denomination;
import org.bukkit.command.CommandSender;

/**
 *
 * @author XHawk87
 */
public class CoinListCommand extends CoinCommand {

    private Coinage plugin;

    public CoinListCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "coinage.commads.coinlist";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return false;
        }

        Currency currency;
        if (args.length == 1) {
            String currencyName = args[0];
            currency = plugin.getCurrency(currencyName);
            if (currency == null) {
                sender.sendMessage("There is no currency with id " + currencyName);
                return true;
            }
        } else {
            List<Currency> currencies = plugin.getAllCurrencies();
            if (currencies.isEmpty()) {
                sender.sendMessage("There are no currencies registered. Perhaps you should create one?");
                return true;
            } else if (currencies.size() == 1) {
                currency = currencies.get(0);
            } else {
                sender.sendMessage("There are " + currencies.size() + " registered currencies: ");
                for (Currency c : currencies) {
                    sender.sendMessage("    " + c.getName() + ": " + c.toString());
                }
                sender.sendMessage("Type /CoinList [currency id] for a list of denominations");
                return true;
            }
        }

        List<Denomination> denominations = currency.getAllDenominations();
        if (denominations.isEmpty()) {
            sender.sendMessage(currency.toString() + " has no denominations yet. Perhaps you should create some?");
        } else {
            boolean hasSingleUnit = false;
            sender.sendMessage(currency.toString() + " has " + denominations.size() + " denominations: ");
            for (Denomination denomination : denominations) {
                sender.sendMessage("    " + denomination.getName() + ": " + denomination.toString());
                if (denomination.getValue() == 1) {
                    hasSingleUnit = true;
                }
            }
            if (!hasSingleUnit) {
                sender.sendMessage("This contains no denomination worth a single unit of the currency. Perhaps you should create one?");
            }
        }
        return true;
    }
}

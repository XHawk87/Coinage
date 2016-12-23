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

/**
 *
 * @author XHawk87
 */
public class SetDefaultCurrencyCommand extends CoinCommand {

    private Coinage plugin;

    public SetDefaultCurrencyCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/SetDefaultCurrency [name]. Sets the default currency to use with Coinage commands. If no currency is specified this is one used. The name must be the id of the currency not the display alias.";
    }

    @Override
    public String getPermission() {
        return "coinage.commands.setdefaultcurrency";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        String name = args[0];
        Currency currency = plugin.getCurrency(name);
        if (currency == null) {
            sender.sendMessage("There is no currency with id " + name);
            return true;
        }
        plugin.setDefaultCurrency(currency);
        sender.sendMessage("The default currency is now " + currency.toString());
        return true;
    }
}

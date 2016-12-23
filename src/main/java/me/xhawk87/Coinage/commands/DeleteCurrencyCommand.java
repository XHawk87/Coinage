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
public class DeleteCurrencyCommand extends CoinCommand {

    private Coinage plugin;

    public DeleteCurrencyCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "coinage.commands.deletecurrency";
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/DeleteCurrency [name] - Deletes the currency with the given name. This will cause all denominations of this currency to be lost and any coins of these denominations will cease being legal tender, however it will not remove them from the game. The name is the name of the currency, not its display alias";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        String name = args[0];
        Currency currency = plugin.getCurrency(name);
        if (currency != null) {
            sender.sendMessage("The " + currency.toString() + " currency was deleted and all denomiations purged");
            currency.delete();
        } else {
            sender.sendMessage("There is no registered currency with id " + name);
        }
        return true;
    }
}

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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author XHawk87
 */
public class CreateCurrencyCommand extends CoinCommand {

    private Coinage plugin;

    public CreateCurrencyCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "coinage.commands.createcurrency";
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/CreateCurrency [name] [alias]. This creates a new currency "
                + "that has its own denominations and is separate from every "
                + "other currency. The name must be unique and cannot contain "
                + "spaces, it is used in commands to refer to this currency. "
                + "The alias may contain spaces and colour codes (using the "
                + ChatColor.COLOR_CHAR + " or &) but must also be unique, and "
                + "is displayed in the lore of every coin of this currency.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String[] fargs = formatArgs(args);

        if (fargs.length != 2) {
            return false;
        }

        String name = fargs[0];
        String alias = fargs[1].replace('&', ChatColor.COLOR_CHAR);
        Currency currency = plugin.createCurrency(name, alias);
        if (currency != null) {
            sender.sendMessage("Created " + currency.toString() + " with id " + currency.getName());
        } else {
            sender.sendMessage("The name and alias must be unique");
        }
        return true;
    }
}

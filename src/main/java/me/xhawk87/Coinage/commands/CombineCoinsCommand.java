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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author XHawk87
 */
public class CombineCoinsCommand extends CoinCommand {

    private Coinage plugin;

    public CombineCoinsCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "coinage.commands.combinecoins";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return false;
        }

        Player player;
        if (args.length == 1) {
            String playerName = args[0];
            player = plugin.getServer().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage("There is no player matching " + playerName);
                return true;
            }
        } else {
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("The console must specify a player");
                return true;
            }
        }

        List<Currency> currencies = plugin.getAllCurrencies();
        if (!currencies.isEmpty()) {
            for (Currency currency : currencies) {
                currency.combine(player.getInventory());
                sender.sendMessage("All " + currency.getName() + " coins were stacked into their highest denominations");
            }
        } else {
            sender.sendMessage("There are no currencies yet. Maybe you should create one?");
        }
        return true;
    }
}

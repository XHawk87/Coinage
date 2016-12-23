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
import org.bukkit.command.CommandSender;

/**
 *
 * @author XHawk87
 */
public class CoinReloadCommand extends CoinCommand {

    private Coinage plugin;

    public CoinReloadCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "coinage.commands.coinreload";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) {
            return false;
        }
        
        plugin.reloadConfig();
        sender.sendMessage("Configuration reloaded from file");
        return true;
    }
}

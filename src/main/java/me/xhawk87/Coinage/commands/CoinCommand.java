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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author XHawk87
 */
public abstract class CoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission = getPermission();
        if (permission != null && !sender.hasPermission(getPermission())) {
            sender.sendMessage("You do not have permission to use this command");
            return true;
        }

        if (args.length == 0) {
            String helpMessage = getHelpMessage(sender);
            if (helpMessage != null && !helpMessage.isEmpty()) {
                sender.sendMessage(helpMessage);
                return true;
            }
        }

        return execute(sender, args);
    }

    public String getPermission() {
        return null;
    }

    public String getHelpMessage(CommandSender sender) {
        return null;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public String[] formatArgs(String[] args) {
        List<String> formatted = new ArrayList<>();
        StringBuilder spacedArg = new StringBuilder();
        for (String arg : args) {
            if (spacedArg.length() == 0) {
                if (arg.startsWith("'")) {
                    spacedArg.append(arg.substring(1));
                } else {
                    formatted.add(arg);
                }
            } else {
                if (arg.endsWith("'")) {
                    spacedArg.append(" ").append(arg.substring(0, arg.length() - 2));
                    formatted.add(spacedArg.toString());
                    spacedArg = new StringBuilder();
                } else {
                    spacedArg.append(" ").append(arg);
                }
            }
        }
        if (spacedArg.length() != 0) {
            formatted.add(spacedArg.toString());
        }
        return formatted.toArray(new String[formatted.size()]);
    }
}

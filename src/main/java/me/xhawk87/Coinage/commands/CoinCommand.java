/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

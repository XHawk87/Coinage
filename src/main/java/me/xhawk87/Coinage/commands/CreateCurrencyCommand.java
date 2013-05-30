/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

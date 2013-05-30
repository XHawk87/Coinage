/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

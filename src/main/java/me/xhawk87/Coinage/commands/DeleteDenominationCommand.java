/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.xhawk87.Coinage.commands;

import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.Currency;
import me.xhawk87.Coinage.Denomination;
import org.bukkit.command.CommandSender;

/**
 *
 * @author XHawk87
 */
public class DeleteDenominationCommand extends CoinCommand {

    private Coinage plugin;

    public DeleteDenominationCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/DeleteDenomination ([currency]) [denomination]. Deletes the denomination with the given name from the given or default currency. This denomiation will cease being legal tender but will not be removed from the game";
    }

    @Override
    public String getPermission() {
        return "coinage.commands.deletedenomination";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return false;
        }

        int index = 0;
        Currency currency;
        if (args.length == 2) {
            String currencyName = args[index++];
            currency = plugin.getCurrency(currencyName);
            if (currency == null) {
                sender.sendMessage("There is no currency with id " + currencyName);
                return true;
            }
        } else {
            currency = plugin.getDefaultCurrency();
        }

        String denomName = args[index++];
        Denomination denomination = currency.getDenominationByName(denomName);
        if (denomination != null) {
            sender.sendMessage(denomination.toString() + " was deleted");
            denomination.delete();
        } else {
            sender.sendMessage(currency.toString() + " has no " + denomName + " denomination");
        }
        return true;
    }
}

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
public class SetVaultCurrencyCommand extends CoinCommand {

    private Coinage plugin;

    public SetVaultCurrencyCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/SetVaultCurrency [name|none] (-s). Sets the default currency to use for Vault transactions, or 'none' to not use Coinage for Vault. The name must be the id of the currency not the display alias. The -s option will execute this command silently if successful";
    }

    @Override
    public String getPermission() {
        return "coinage.commands.setvaultcurrency";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return false;
        }

        boolean silent = false;
        if (args.length == 2) {
            if (args[1].equals("-s")) {
                silent = true;
            } else {
                sender.sendMessage("Unrecognised option (" + args[1] + "). Expected -s");
                return false;
            }
        }

        String name = args[0];
        Currency currency = plugin.getCurrency(name);
        if (currency == null) {
            sender.sendMessage("There is no currency with id " + name);
            return true;
        }
        Currency original = plugin.getVaultCurrency();
        if (plugin.setVaultCurrency(currency)) {
            if (original == null) {
                sender.sendMessage(currency.toString() + " is now the default currency used in Vault transactions. A restart may be required for all other plugins to notice the change");
            } else if (!silent) {
                sender.sendMessage(currency.toString() + " replaced " + original.toString() + " as the Vault currency with immediate effect");
            }
        } else {
            sender.sendMessage("You must have Vault installed to set its default currency");
        }
        return true;
    }
}

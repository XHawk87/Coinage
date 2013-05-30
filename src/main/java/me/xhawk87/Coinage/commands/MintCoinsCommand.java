/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.xhawk87.Coinage.commands;

import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.Currency;
import me.xhawk87.Coinage.Denomination;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author XHawk87
 */
public class MintCoinsCommand extends CoinCommand {

    private Coinage plugin;

    public MintCoinsCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        if (sender instanceof Player) {
            return "/MintCoins [denomination] - Turns the items in the your hand into the given denomination of coin in the default currency\n"
                    + "/MintCoins [currency] [denomination] - Turns the items in your hand into the given denomination of the specified currency\n"
                    + "/MintCoins [player] [denomination] - Turns the items in the given player's hand into the specified denomation in the default currency\n"
                    + "/MintCoins [player) [currency] [denomination] - Turns the items in the given player's hand into the specified denomination of the given currency\n"
                    + "\nThe item type and data must match the denomination";
        } else {
            return "/MintCoins [player] [denomination] - Turns the items in the given player's hand into the specified denomation in the default currency\n"
                    + "/MintCoins [player) [currency] [denomination] - Turns the items in the given player's hand into the specified denomination of the given currency\n"
                    + "\nThe item type and data must match the denomination";
        }
    }

    @Override
    public String getPermission() {
        return "coinage.commands.mintcoins";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 3) {
            return false;
        }

        int index = 0;
        Player player = null;
        Currency currency;
        Denomination denomination;
        if (args.length >= 2) {
            String firstArg = args[index++];
            if (args.length == 3) {
                player = plugin.getServer().getPlayer(firstArg);
                if (player == null) {
                    sender.sendMessage("There is no player matching " + firstArg);
                    return true;
                }
                String currencyName = args[index++];
                currency = plugin.getCurrency(currencyName);
                if (currency == null) {
                    sender.sendMessage("There is no currency with name " + currencyName);
                    return true;
                }
            } else {
                currency = plugin.getCurrency(firstArg);
                if (currency == null) {
                    player = plugin.getServer().getPlayer(firstArg);
                    if (player == null) {
                        sender.sendMessage("There is no matching player or currency with name " + firstArg);
                        return true;
                    }
                }
            }
        } else {
            currency = plugin.getDefaultCurrency();
        }

        if (player == null) {
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("The console must specify a player");
                return true;
            }
        }

        String denomName = args[index++];
        denomination = currency.getDenominationByName(denomName);
        if (denomination == null) {
            sender.sendMessage(currency.toString() + " has no " + denomName + " denomination");
            return true;
        }

        ItemStack held = player.getItemInHand();
        if (denomination.mint(held)) {
            if (sender != player) {
                sender.sendMessage("The held item was minted as " + denomination.toString());
            }
        } else {
            sender.sendMessage("The held item cannot be minted with this denomination");
        }
        return true;
    }
}

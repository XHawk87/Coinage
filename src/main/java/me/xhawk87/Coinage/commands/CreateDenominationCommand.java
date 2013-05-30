/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.xhawk87.Coinage.commands;

import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.Currency;
import me.xhawk87.Coinage.Denomination;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author XHawk87
 */
public class CreateDenominationCommand extends CoinCommand {

    private Coinage plugin;

    public CreateDenominationCommand(Coinage plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return "/CreateDenomination ([currency]) [denomination] [alias] [value] ([item ID:data value]) [print]. Creates a new denomination of the given or default currency, with the given display alias, of the given unit value of the currency, with the given or held item type and data, and with the given print in the lore. The name and alias must be unique within the currency. The alias and print may contain colour codes and spaces but in which case they must be surrounded by 'single quotes'";
    }

    @Override
    public String getPermission() {
        return "coinage.commands.createdenomination";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String[] fargs = formatArgs(args);
        if (fargs.length < 4 || fargs.length > 6) {
            return false;
        }

        // Get the currency
        int index = 0;
        Currency currency;
        if (fargs.length == 6) {
            String currencyName = fargs[index++];
            currency = plugin.getCurrency(currencyName);
            if (currency == null) {
                sender.sendMessage("There is no currency with id " + currencyName);
                return true;
            }
        } else {
            currency = plugin.getDefaultCurrency();
        }

        // Get the name and alias
        String denomName = fargs[index++];
        String alias = fargs[index++].replace('&', ChatColor.COLOR_CHAR);

        // Get the value
        int value;
        String valueString = fargs[index++];
        try {
            value = Integer.parseInt(valueString);
            if (value < 1) {
                sender.sendMessage("All coins must have a positive value");
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(valueString + " is not a valid number");
            return true;
        }

        // Get the item id and data
        int itemId;
        short itemData = 0;
        if (fargs.length >= 5) {
            String itemString = fargs[index++];
            String[] parts = itemString.split(":");
            if (parts.length > 2) {
                sender.sendMessage("Invalid item:data code " + itemString);
                return true;
            }
            String itemPart = parts[0];
            String dataPart = null;
            if (parts.length == 2) {
                dataPart = parts[1];
            }
            try {
                itemId = Integer.parseInt(itemPart);
            } catch (NumberFormatException ex) {
                sender.sendMessage("The itemId is not a valid number: " + itemPart);
                return true;
            }
            if (dataPart != null) {
                try {
                    itemData = Short.parseShort(dataPart);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("The itemData is not a valid number: " + dataPart);
                    return true;
                }
            }
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack item = player.getItemInHand();
                if (item == null || item.getTypeId() == 0) {
                    sender.sendMessage("You must specify an item:data or hold the desired item in your hand");
                    return true;
                }
                itemId = item.getTypeId();
                itemData = item.getDurability();
            } else {
                sender.sendMessage("The console must specify the item:data to use");
                return true;
            }
        }

        // Get the print
        String print = fargs[index++].replace('&', ChatColor.COLOR_CHAR);

        Denomination denomination = currency.createDenomination(denomName, alias, print, value, itemId, itemData);
        if (denomination != null) {
            sender.sendMessage("Created denomation " + denomination.toString());
            if (sender instanceof Player) {
                Player player = (Player) sender;
                PlayerInventory inv = player.getInventory();
                if (inv.firstEmpty() != -1) {
                    ItemStack coin = denomination.create(1);
                    ItemMeta meta = coin.getItemMeta();
                    meta.getLore().add("Prototype");
                    coin.setItemMeta(meta);
                    inv.addItem(coin);
                    player.sendMessage("A prototype for the new coin has been added to your inventory");
                }
            }
        } else {
            sender.sendMessage("A denomination of " + currency.toString() + " already exists with this name or alias");
        }
        return true;
    }
}

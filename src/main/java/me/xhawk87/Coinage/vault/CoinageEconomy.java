/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.xhawk87.Coinage.vault;

import java.util.ArrayList;
import java.util.List;
import me.xhawk87.Coinage.Coinage;
import me.xhawk87.Coinage.Currency;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 *
 * @author XHawk87
 */
public class CoinageEconomy implements Economy {

    private Coinage plugin;
    private Currency currency;
    private boolean enabled;

    public CoinageEconomy(Coinage plugin, Currency currency) {
        this.plugin = plugin;
        this.currency = currency;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return currency.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return amount + " in " + currency.getAlias();
    }

    @Override
    public String currencyNamePlural() {
        return currency.getAlias();
    }

    @Override
    public String currencyNameSingular() {
        return currency.getAlias();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public double getBalance(String playerName) {
        Player player = plugin.getServer().getPlayerExact(playerName);
        if (player != null) {
            return currency.getCoinCount(player.getInventory());
        }
        return 0.0;
    }

    @Override
    public double getBalance(String playerName, String world) {
        Player player = plugin.getServer().getPlayerExact(playerName);
        if (player != null) {
            return currency.getCoinCount(player.getInventory());
        }
        return 0.0;
    }

    @Override
    public boolean has(String playerName, double amount) {
        Player player = plugin.getServer().getPlayerExact(playerName);
        if (player != null) {
            return currency.getCoinCount(player.getInventory()) >= amount;
        }
        return false;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        Player player = plugin.getServer().getPlayerExact(playerName);
        if (player != null) {
            return currency.getCoinCount(player.getInventory()) >= amount;
        }
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = plugin.getServer().getPlayerExact(playerName);
        if (player != null) {
            if (currency.spend(player.getInventory(), (int) amount)) {
                return new EconomyResponse(amount, currency.getCoinCount(player.getInventory()), EconomyResponse.ResponseType.SUCCESS, player.getDisplayName() + " spent " + (int) amount + " " + currency.getAlias());
            } else {
                return new EconomyResponse(0, currency.getCoinCount(player.getInventory()), EconomyResponse.ResponseType.FAILURE, player.getDisplayName() + " does not have enough " + currency.getAlias());
            }
        }
        if (plugin.addPendingWithdrawal(playerName, currency, (long) amount)) {
            return new EconomyResponse(amount, plugin.getPendingBalance(playerName, currency), EconomyResponse.ResponseType.SUCCESS, (int) amount + " " + currency.getAlias() + " was transferred from " + playerName + "'s offline balance");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, playerName + " is not online and did not have enough " + currency.getAlias() + " in their account");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = plugin.getServer().getPlayerExact(playerName);
        if (player != null) {
            if (currency.give(player.getInventory(), (int) amount)) {
                return new EconomyResponse(amount, currency.getCoinCount(player.getInventory()), EconomyResponse.ResponseType.SUCCESS, player.getDisplayName() + " received " + (int) amount + " " + currency.getAlias());
            } else {
                return new EconomyResponse(0, currency.getCoinCount(player.getInventory()), EconomyResponse.ResponseType.FAILURE, "An error occurred which prevented this transaction from taking place");
            }
        }
        if (plugin.addPendingDeposit(playerName, currency, (long) amount)) {
            return new EconomyResponse(amount, plugin.getPendingBalance(playerName, currency), EconomyResponse.ResponseType.SUCCESS, (int) amount + " " + currency.getAlias() + " was transferred to " + playerName + "'s offline balance");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, playerName + " is not online");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

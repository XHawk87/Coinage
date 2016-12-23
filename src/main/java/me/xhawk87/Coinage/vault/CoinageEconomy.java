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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author XHawk87
 */
public class CoinageEconomy implements Economy {

    private final Coinage plugin;
    private Currency currency;
    private boolean enabled;

    public CoinageEconomy(Coinage plugin, Currency currency) {
        this.plugin = plugin;
        this.currency = currency;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            setCurrency(null);
        }
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
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

    private boolean hasAccount(Player player) {
        return player != null;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(plugin.getServer().getPlayerExact(playerName));
    }

    @Override
    public boolean hasAccount(String playerName, String ignore) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer playerRef) {
        return hasAccount(playerRef.getPlayer());
    }

    @Override
    public boolean hasAccount(OfflinePlayer playerRef, String ignore) {
        return hasAccount(playerRef);
    }

    private double getBalance(Player player) {
        if (player != null) {
            return currency.getCoinCount(player.getInventory());
        }
        return 0.0;
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(plugin.getServer().getPlayerExact(playerName));
    }

    @Override
    public double getBalance(String playerName, String ignore) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer playerRef) {
        return getBalance(playerRef.getPlayer());
    }

    @Override
    public double getBalance(OfflinePlayer playerRef, String ignore) {
        return getBalance(playerRef);
    }

    private boolean has(Player player, double amount) {
        if (player != null) {
            return currency.getCoinCount(player.getInventory()) >= amount;
        }
        return false;
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(plugin.getServer().getPlayerExact(playerName), amount);
    }

    @Override
    public boolean has(String playerName, String ignore, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer playerRef, double amount) {
        return has(playerRef.getPlayer(), amount);
    }

    @Override
    public boolean has(OfflinePlayer playerRef, String ignore, double amount) {
        return has(playerRef, amount);
    }

    private EconomyResponse withdrawPlayer(Player player, String playerName, double amount) {
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
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(plugin.getServer().getPlayerExact(playerName), playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String ignore, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer playerRef, double amount) {
        return withdrawPlayer(playerRef.getPlayer(), playerRef.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer playerRef, String ignore, double amount) {
        return withdrawPlayer(playerRef, amount);
    }

    private EconomyResponse depositPlayer(Player player, String playerName, double amount) {
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
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(plugin.getServer().getPlayerExact(playerName), playerName, amount);

    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer playerRef, double amount) {
        return depositPlayer(playerRef.getPlayer(), playerRef.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer playerRef, String ignore, double amount) {
        return depositPlayer(playerRef, amount);
    }

    private EconomyResponse createBank(String bankName, Player player) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse createBank(String bankName, String playerName) {
        return createBank(bankName, plugin.getServer().getPlayerExact(playerName));
    }

    @Override
    public EconomyResponse createBank(String bankName, OfflinePlayer playerRef) {
        return createBank(bankName, playerRef.getPlayer());
    }

    @Override
    public EconomyResponse deleteBank(String bankName) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankBalance(String bankName) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankHas(String bankName, double amount) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankWithdraw(String bankName, double amount) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse bankDeposit(String bankName, double amount) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    private EconomyResponse isBankOwner(String bankName, Player player) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse isBankOwner(String bankName, String playerName) {
        return isBankOwner(bankName, plugin.getServer().getPlayerExact(playerName));
    }

    @Override
    public EconomyResponse isBankOwner(String bankName, OfflinePlayer playerRef) {
        return isBankOwner(bankName, playerRef.getPlayer());
    }

    private EconomyResponse isBankMember(String bankName, Player player) {
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Coinage does not include bank support");
    }

    @Override
    public EconomyResponse isBankMember(String bankName, String playerName) {
        return isBankMember(bankName, plugin.getServer().getPlayerExact(playerName));
    }

    @Override
    public EconomyResponse isBankMember(String bankName, OfflinePlayer playerRef) {
        return isBankMember(bankName, playerRef.getPlayer());
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    private boolean createPlayerAccount(Player player) {
        return player != null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(plugin.getServer().getPlayerExact(playerName));
    }

    @Override
    public boolean createPlayerAccount(String playerName, String ignore) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer playerRef) {
        return createPlayerAccount(playerRef.getPlayer());
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer playerRef, String ignore) {
        return createPlayerAccount(playerRef);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.xhawk87.Coinage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.xhawk87.Coinage.commands.CoinListCommand;
import me.xhawk87.Coinage.commands.CoinReloadCommand;
import me.xhawk87.Coinage.commands.CoinValueCommand;
import me.xhawk87.Coinage.commands.CombineCoinsCommand;
import me.xhawk87.Coinage.commands.ConvertCoinsCommand;
import me.xhawk87.Coinage.commands.CreateCurrencyCommand;
import me.xhawk87.Coinage.commands.CreateDenominationCommand;
import me.xhawk87.Coinage.commands.DeleteCurrencyCommand;
import me.xhawk87.Coinage.commands.DeleteDenominationCommand;
import me.xhawk87.Coinage.commands.GiveCoinsCommand;
import me.xhawk87.Coinage.commands.GiveCurrency;
import me.xhawk87.Coinage.commands.MintCoinsCommand;
import me.xhawk87.Coinage.commands.SetDefaultCurrencyCommand;
import me.xhawk87.Coinage.commands.SpendCoinsCommand;
import me.xhawk87.Coinage.commands.SplitCoinsCommand;
import me.xhawk87.Coinage.listeners.CoinListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author XHawk87
 */
public class Coinage extends JavaPlugin {

    private Map<String, Currency> currencies = new HashMap<>();

    @Override
    /**
     * This method was not meant for you. Using it puts you at serious risk of a
     * slap around the face.
     */
    public void onEnable() {
        saveDefaultConfig();

        // Load currencies
        ConfigurationSection currencySection = getConfig().getConfigurationSection("currencies");
        for (String key : currencySection.getKeys(false)) {
            Currency currency = new Currency(this, currencySection.getConfigurationSection(key));
            currencies.put(key, currency);
        }
        saveConfig();

        // Register commands
        getCommand("CoinList").setExecutor(new CoinListCommand(this));
        getCommand("CoinReload").setExecutor(new CoinReloadCommand(this));
        getCommand("CoinValue").setExecutor(new CoinValueCommand(this));
        getCommand("CombineCoins").setExecutor(new CombineCoinsCommand(this));
        getCommand("ConvertCoins").setExecutor(new ConvertCoinsCommand(this));
        getCommand("CreateCurrency").setExecutor(new CreateCurrencyCommand(this));
        getCommand("CreateDenomination").setExecutor(new CreateDenominationCommand(this));
        getCommand("DeleteCurrency").setExecutor(new DeleteCurrencyCommand(this));
        getCommand("DeleteDenomination").setExecutor(new DeleteDenominationCommand(this));
        getCommand("GiveCoins").setExecutor(new GiveCoinsCommand(this));
        getCommand("GiveCurrency").setExecutor(new GiveCurrency(this));
        getCommand("MintCoins").setExecutor(new MintCoinsCommand(this));
        getCommand("SetDefaultCurrency").setExecutor(new SetDefaultCurrencyCommand(this));
        getCommand("SpendCoins").setExecutor(new SpendCoinsCommand(this));
        getCommand("SplitCoins").setExecutor(new SplitCoinsCommand(this));

        // Register events
        new CoinListener().registerEvents(this);
    }

    /**
     * Gets the default currency to be used in commands
     *
     * @return The default currency
     */
    public Currency getDefaultCurrency() {
        String name = getConfig().getString("default");
        if (name == null) {
            if (!currencies.isEmpty()) {
                Currency currency = currencies.values().iterator().next();
                setDefaultCurrency(currency);
                return currency;
            } else {
                getLogger().warning("You must create at least one currency!");
                return null;
            }
        }
        return currencies.get(name);
    }

    /**
     * Sets the default currency to be used in commands by its name.
     *
     * @param name The name of the currency
     * @return True if the default was set, False if the currency did not exist
     */
    public boolean setDefaultCurrency(String name) {
        Currency currency = getCurrency(name);
        if (currency == null) {
            return false;
        }
        setDefaultCurrency(currency);
        return true;
    }

    /**
     * Sets the default currency to be used in commands.
     *
     * @param currency The currency to use as default
     */
    public void setDefaultCurrency(Currency currency) {
        getConfig().set("default", currency.getName());
        saveConfig();
    }

    /**
     * <p>Creates a new currency with the given name and alias.</p>
     *
     * <p>The name must not contain spaces and is used to reference the currency
     * in Coinage commands.</p>
     *
     * <p>The alias may contain spaces and colour codes and is displayed in the
     * item lore before the denomination.</p>
     *
     * @param name The currency name
     * @param alias The currency alias
     * @return The currency created or null if a currency with this name already
     * exists
     */
    public Currency createCurrency(String name, String alias) {
        if (currencies.containsKey(name)) {
            return null;
        }

        ConfigurationSection currencySection = getConfig().createSection("currencies." + name);
        currencySection.set("alias", alias);
        Currency currency = new Currency(this, currencySection);
        currencies.put(name, currency);
        saveConfig();
        return currency;
    }

    /**
     * Deletes an existing currency by its name. This will cause all
     * denominations of this currency to cease being considered legal tender
     *
     * @param name The currency name
     * @return True if successfully deleted, False if it did not exist
     */
    public boolean deleteCurrency(String name) {
        if (!currencies.containsKey(name)) {
            return false;
        }

        currencies.remove(name);
        getConfig().set("currencies." + name, null);
        saveConfig();
        return true;
    }

    /**
     * Deletes an existing currency. This will cause all denominations of this
     * currency to cease being considered legal tender
     *
     * @param currency The currency to delete
     */
    public void deleteCurrency(Currency currency) {
        String name = currency.getName();
        currencies.remove(name);
        getConfig().set("currencies." + name, null);
        saveConfig();
    }

    /**
     * Gets a currency by its name. This will return null if the currency does
     * not exist
     *
     * @param name The name of the currency
     * @return The currency, or null if it does not exist
     */
    public Currency getCurrency(String name) {
        return currencies.get(name);
    }

    /**
     * Gets a currency by the lore on a coin. This will return null if there is
     * no matching currency
     *
     * @param lore The lore of the coin
     * @return The currency, or null if there was no match
     */
    public Currency getCurrencyByLore(String lore) {
        for (Currency currency : currencies.values()) {
            if (currency.matches(lore)) {
                return currency;
            }
        }
        return null;
    }

    /**
     * Returns a list of all currencies. Modifying the list will not affect the
     * registered currencies.
     *
     * @return A list of all currencies
     */
    public List<Currency> getAllCurrencies() {
        return new ArrayList<>(currencies.values());
    }

    /**
     * Find the denomination for a given coin
     *
     * @param item The item to check
     * @return The denomination of the coin or null if it is not a coin
     */
    public Denomination getDenominationOfCoin(ItemStack item) {
        if (item == null || item.getTypeId() == 0) {
            return null;
        }
        if (!item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (!meta.hasDisplayName() || !meta.hasLore()) {
            return null;
        }

        if (meta.getLore().size() != 1) {
            return null;
        }

        String lore = meta.getLore().get(0);
        Currency currency = getCurrencyByLore(lore);
        if (currency == null) {
            return null;
        }
        return currency.getDenominationByLore(lore);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        currencies.clear();
        ConfigurationSection currencySection = getConfig().getConfigurationSection("currencies");
        for (String key : currencySection.getKeys(false)) {
            Currency currency = new Currency(this, currencySection.getConfigurationSection(key));
            currencies.put(key, currency);
        }
        saveConfig();
    }
}

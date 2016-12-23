/*
 * Copyright (C) 2013-2016 XHawk87
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.xhawk87.Coinage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import me.xhawk87.Coinage.commands.SetVaultCurrencyCommand;
import me.xhawk87.Coinage.commands.SpendCoinsCommand;
import me.xhawk87.Coinage.commands.SplitCoinsCommand;
import me.xhawk87.Coinage.listeners.CoinListener;
import me.xhawk87.Coinage.listeners.MoneyBagListener;
import me.xhawk87.Coinage.moneybags.MoneyBag;
import me.xhawk87.Coinage.moneybags.YamlFileFilter;
import me.xhawk87.Coinage.vault.CoinageEconomy;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author XHawk87
 */
public class Coinage extends JavaPlugin {

    private Map<String, Currency> currencies = new HashMap<>();
    private Map<String, MoneyBag> moneybags = new HashMap<>();
    private List<Recipe> moneyBagTypes = new ArrayList<>();
    private Map<String, PendingTransfers> pendingTransfers = new HashMap<>();
    private CoinageEconomy vaultEconomy;

    @Override
    /**
     * This method was not meant for you. Using it puts you at serious risk of a
     * slap around the face.
     */
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        // Load data
        loadMoneyBags();
        loadOfflineAccounts();

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
        getCommand("SetVaultCurrency").setExecutor(new SetVaultCurrencyCommand(this));
        getCommand("SpendCoins").setExecutor(new SpendCoinsCommand(this));
        getCommand("SplitCoins").setExecutor(new SplitCoinsCommand(this));

        // Register events
        new CoinListener().registerEvents(this);
        new MoneyBagListener().registerEvents(this);
    }

    @Override
    public void onDisable() {
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
        return currencies.get(name.toLowerCase());
    }

    /**
     * Gets the default currency to be used in commands
     *
     * @return The default currency, or null if no Coinage currency is used for
     * Vault
     */
    public Currency getVaultCurrency() {
        String name = getConfig().getString("vault-currency");
        if (name == null || name.equalsIgnoreCase("none")) {
            return null;
        }
        Currency vaultCurrency = currencies.get(name.toLowerCase());
        if (vaultCurrency == null) {
            getLogger().warning("Invalid vault-currency in config.yml: " + name + " does not exist");
        }
        return vaultCurrency;
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
     * Sets the default currency to be used in Vault transactions by its name
     *
     * This requires Vault to be installed
     *
     * @param name The currency to use for Vault, or "none" for no currency
     * @return True if the vault currency was set, otherwise False
     */
    public boolean setVaultCurrency(String name) {
        if (name == null || name.equalsIgnoreCase("none")) {
            setVaultCurrency((Currency) null);
            return true;
        }
        Currency currency = getCurrency(name);
        if (currency == null) {
            return false;
        }
        setVaultCurrency(currency);
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
     * Sets the default currency to be used in Vault transactions.
     *
     * This requires Vault to be installed
     *
     * @param currency The currency to use for Vault, or null for no currency
     */
    public boolean setVaultCurrency(Currency currency) {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }
        if (currency == null) {
            getConfig().set("vault-currency", null);
        } else {
            getConfig().set("vault-currency", currency.getName());
        }

        saveConfig();
        return true;
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
        if (currencies.containsKey(name.toLowerCase())) {
            return null;
        }

        ConfigurationSection currencySection = getConfig().createSection("currencies." + name);
        currencySection.set("alias", alias);
        Currency currency = new Currency(this, currencySection);
        currencies.put(name.toLowerCase(), currency);
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
        if (!currencies.containsKey(name.toLowerCase())) {
            return false;
        }

        currencies.remove(name.toLowerCase());
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
        currencies.remove(name.toLowerCase());
        getConfig().set("currencies." + name.toLowerCase(), null);
        saveConfig();
        if (vaultEconomy.getCurrency().equals(currency)) {
            unregisterVaultEconomy();
        }
    }

    /**
     * Gets a currency by its name. This will return null if the currency does
     * not exist
     *
     * @param name The name of the currency
     * @return The currency, or null if it does not exist
     */
    public Currency getCurrency(String name) {
        return currencies.get(name.toLowerCase());
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
     * Returns a list of all valid currency IDs
     *
     * @return A list of all currency IDs
     */
    public List<String> getCurrencyIds() {
        return new ArrayList<>(currencies.keySet());
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

        // Clear previous data
        if (!currencies.isEmpty()) {
            Currency vaultCurrency = getVaultCurrency();
            if (vaultCurrency != null) {
                unregisterVaultEconomy();
            }
        }

        currencies.clear();
        Iterator<Recipe> itRecipe = getServer().recipeIterator();
        while (itRecipe.hasNext()) {
            if (moneyBagTypes.contains(itRecipe.next())) {
                itRecipe.remove();
            }
        }
        moneyBagTypes.clear();

        // Load currencies
        ConfigurationSection currencySection = getConfig().getConfigurationSection("currencies");
        for (String key : currencySection.getKeys(false)) {
            Currency currency = new Currency(this, currencySection.getConfigurationSection(key));
            currencies.put(key.toLowerCase(), currency);
        }

        // Register with Vault (if installed)
        Currency vaultCurrency = getVaultCurrency();
        if (vaultCurrency != null) {
            registerVaultCurrency(vaultCurrency);
        }

        // Load money bag types
        ConfigurationSection moneybagSection = getConfig().getConfigurationSection("moneybags");
        for (String key : moneybagSection.getKeys(false)) {
            Recipe recipe = MoneyBag.loadMoneyBagType(moneybagSection.getConfigurationSection(key));
            getServer().addRecipe(recipe);
            moneyBagTypes.add(recipe);
        }

        // Save any formatting changes
        saveConfig();
    }

    public boolean isMoneyBag(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (!itemMeta.hasDisplayName() || !itemMeta.hasLore()) {
            return false;
        }
        for (Recipe moneyBagType : moneyBagTypes) {
            ItemStack result = moneyBagType.getResult();

            if (result.getTypeId() == item.getTypeId()
                    && result.getDurability() == item.getDurability()) {
                ItemMeta resultMeta = result.getItemMeta();
                if (resultMeta.getDisplayName().equals(itemMeta.getDisplayName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public MoneyBag getMoneyBag(ItemStack item) {
        if (!isMoneyBag(item)) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> loreStrings = meta.getLore();
        if (loreStrings.size() > 0) {
            String lore = loreStrings.get(0);
            String data = MoneyBag.decodeLore(lore);
            if (data.startsWith("moneybag")) { // This is a brand new money bag
                String[] parts = data.split(",");
                int size;
                try {
                    size = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ex) {
                    return null; // Invalid, no size
                }
                String title = meta.getDisplayName();

                String key = UUID.randomUUID().toString();
                while (moneybags.containsKey(key)) {
                    key = UUID.randomUUID().toString();
                }

                // Update item lore
                lore = MoneyBag.encodeLore(key) + ChatColor.LIGHT_PURPLE + "Right-click while holding to open";
                loreStrings.clear();
                loreStrings.add(lore);
                meta.setLore(loreStrings);
                item.setItemMeta(meta);

                // Create moneybag
                MoneyBag moneybag = new MoneyBag(this, key, size, title);
                moneybags.put(key, moneybag);
                moneybag.save();
                return moneybag;
            } else {
                return moneybags.get(data);
            }
        }
        return null;
    }

    public void loadMoneyBags() {
        FilenameFilter filenameFilter = new YamlFileFilter();
        File moneybagsFolder = new File(getDataFolder(), "moneybags");
        if (moneybagsFolder.exists()) {
            for (String filename : moneybagsFolder.list(filenameFilter)) {
                String key = filename.substring(0, filename.length() - ".yml".length());
                MoneyBag moneyBag = new MoneyBag(this, key);
                moneyBag.load();
                moneybags.put(key, moneyBag);
            }
        } else {
            moneybagsFolder.mkdirs();
        }
    }

    public void loadOfflineAccounts() {
        FilenameFilter filenameFilter = new YamlFileFilter();
        File pendingTransfersFolder = new File(getDataFolder(), "pending");
        if (pendingTransfersFolder.exists()) {
            for (String filename : pendingTransfersFolder.list(filenameFilter)) {
                String key = filename.substring(0, filename.length() - ".yml".length());
                PendingTransfers pendingTransfer = new PendingTransfers(this, key);
                pendingTransfer.load();
                pendingTransfers.put(key, pendingTransfer);
            }
        } else {
            pendingTransfersFolder.mkdirs();
        }
    }

    private PendingTransfers getOrCreatePendingTransfers(String playerName) {
        PendingTransfers pending = pendingTransfers.get(playerName);
        if (pending == null) {
            pending = new PendingTransfers(this, playerName);
            pendingTransfers.put(playerName, pending);
        }
        return pending;
    }

    public void completePendingTransactions(Player player) {
        PendingTransfers pending = pendingTransfers.get(player.getName());
        if (pending != null) {
            for (Currency currency : currencies.values()) {
                long balance = pending.getBalance(currency);
                if (balance > 0) {
                    currency.give(player, (int) balance);
                }
            }
        }
    }

    public long getPendingBalance(String playerName, Currency currency) {
        PendingTransfers pending = pendingTransfers.get(playerName);
        if (pending == null) {
            return 0;
        }
        return pending.getBalance(currency);
    }

    public boolean addPendingDeposit(String playerName, Currency currency, long amount) {
        PendingTransfers pending = getOrCreatePendingTransfers(playerName);
        if (!pending.deposit(currency, amount)) {
            return false;
        }
        pending.save();
        return true;
    }

    public boolean addPendingWithdrawal(String playerName, Currency currency, long amount) {
        PendingTransfers pending = getOrCreatePendingTransfers(playerName);
        if (!pending.withdraw(currency, amount)) {
            return false;
        }
        pending.save();
        return true;
    }

    /**
     * Registers this currency with Vault as the default currency to use for
     * transactions
     */
    public void registerVaultCurrency(Currency currency) {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            Vault vault = (Vault) getServer().getPluginManager().getPlugin("Vault");
            if (vaultEconomy == null) {
                vaultEconomy = new CoinageEconomy(this, currency);
                getServer().getServicesManager().register(Economy.class, vaultEconomy, vault, ServicePriority.High);
            } else {
                vaultEconomy.setCurrency(currency);
            }
            vaultEconomy.setEnabled(true);
            getLogger().info(currency.getName() + " was registered with Vault");
        } else {
            getLogger().warning("Could not find Vault to set " + currency.getName() + " as the default vault currency");
        }
    }

    /**
     * Unregisters this currency with Vault. It will no longer be the default
     * currency, if it was before
     */
    public void unregisterVaultEconomy() {
        if (vaultEconomy != null) {
            getServer().getServicesManager().unregister(vaultEconomy);
            getLogger().info(vaultEconomy.getCurrency().getAlias() + " was unregistered with Vault");
            vaultEconomy.setEnabled(false);
        }
    }
}

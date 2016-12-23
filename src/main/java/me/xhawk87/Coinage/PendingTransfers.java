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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.xhawk87.Coinage.utils.FileUpdater;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author XHawk87
 */
public class PendingTransfers {

    private Coinage plugin;
    private String name;
    private final File file;
    private FileUpdater fileUpdater;
    private Map<String, Long> balances = new HashMap<>();

    public PendingTransfers(Coinage plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.file = new File(new File(plugin.getDataFolder(), "pending"), name + ".yml");
        fileUpdater = new FileUpdater(file);
    }

    public String getName() {
        return name;
    }

    public long getBalance(Currency currency) {
        Long balance = balances.get(currency.getName());
        return balance != null ? balance : 0;
    }

    public void setBalance(Currency currency, long amount) {
        balances.put(currency.getName(), amount);
    }

    public boolean deposit(Currency currency, long amount) {
        if (amount < 1) {
            return false;
        }
        balances.put(currency.getName(), getBalance(currency) + amount);
        return true;
    }

    public boolean withdraw(Currency currency, long amount) {
        long balance = getBalance(currency);
        if (balance < amount) {
            return false;
        }
        balances.put(currency.getName(), balance - amount);
        return true;
    }

    public void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (file) {
                    final FileConfiguration data = new YamlConfiguration();
                    try {
                        data.load(file);
                    } catch (IOException | InvalidConfigurationException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Could not load " + file.getPath(), ex);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            onLoad(data);
                        }
                    }.runTask(plugin);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void onLoad(FileConfiguration data) {
        for (String currency : data.getKeys(false)) {
            long balance = data.getLong(currency);
            balances.put(currency, balance);
        }
    }

    public void save() {
        FileConfiguration data = new YamlConfiguration();
        for (Map.Entry<String, Long> entry : balances.entrySet()) {
            data.set(entry.getKey(), entry.getValue());
        }
        fileUpdater.save(plugin, data.saveToString());
    }
}

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
package me.xhawk87.Coinage.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author XHawk87
 */
public class FileUpdater {

    private static final long DEFAULT_INTERVAL = 1000;
    private final File file;
    private long saveCount = 0;
    private long lastSave = System.currentTimeMillis();
    private long interval;
    private BukkitTask saveTask = null;

    public FileUpdater(File file) {
        this(file, DEFAULT_INTERVAL);
    }

    public FileUpdater(File file, long interval) {
        this.file = file;
        this.interval = interval;
    }

    /**
     * Overwrites the file with the given text record
     *
     * This is performed asynchronously, one second after the last call
     *
     * @param plugin
     * @param record
     */
    public void save(final Plugin plugin, final String record) {
        saveCount++;
        final long currentSave = saveCount;
        if (saveTask != null) {
            saveTask.cancel();
        }
        int delay = (int) ((lastSave + interval - System.currentTimeMillis()) / 50);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentSave == saveCount) { // enforce save order
                    synchronized (file) {
                        if (currentSave == saveCount) { // enforce save order
                            try (FileWriter out = new FileWriter(file)) {
                                out.write(record);
                            } catch (IOException ex) {
                                plugin.getLogger().log(Level.SEVERE, "Could not save: " + file.getPath(), ex);
                            }
                            lastSave = System.currentTimeMillis();
                        }
                    }
                }
            }
        };
        if (delay > 1) {
            saveTask = task.runTaskLaterAsynchronously(plugin, delay);
        } else {
            saveTask = task.runTaskAsynchronously(plugin);
        }
    }
}

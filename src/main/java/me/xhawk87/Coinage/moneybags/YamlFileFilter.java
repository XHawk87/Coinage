/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.xhawk87.Coinage.moneybags;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author XHawk87
 */
public class YamlFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".yml");
    }
}

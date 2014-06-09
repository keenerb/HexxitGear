/*
 * HexxitGear
 * Copyright (C) 2013  Ryan Cohen
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

package sct.hexxitgear.setup;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import sct.hexxitgear.HexxitGear;

import java.io.*;
import java.util.Properties;

public class HexxitGearConfig {

    public static Property dimensionalBlacklist;

    public static File configFolder;

    public static void loadCommonConfig(FMLPreInitializationEvent evt)
    {
        Configuration c = new Configuration(evt.getSuggestedConfigurationFile());
        try {
            c.load();

            dimensionalBlacklist = c.get("World Generation", "Dimensional Blacklist", "");
            dimensionalBlacklist.comment = "Comma separated list of all blacklisted dimension IDs";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.save();
        }
    }

    public static String getConfigBaseFolder()
    {
        return "sct";
    }

    public static void setConfigFolderBase(File folder)
    {
        configFolder = new File(folder.getAbsolutePath() + "/" + getConfigBaseFolder() + "/"
                + HexxitGear.modId + "/");
    }

    public static void extractLang(String[] languages)
    {
        String langResourceBase = "/sct/" + HexxitGear.modId + "/lang/";
        for (String lang : languages)
        {
            InputStream is = HexxitGear.instance.getClass().getResourceAsStream(langResourceBase + lang + ".lang");
            try
            {
                File f = new File(configFolder.getAbsolutePath() + "/lang/"
                        + lang + ".lang");
                if (!f.exists())
                    f.getParentFile().mkdirs();
                OutputStream os = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = is.read(buffer)) != -1)
                {
                    os.write(buffer, 0, read);
                }
                is.close();
                os.flush();
                os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void registerDimBlacklist() {
        String blacklist = dimensionalBlacklist.getString().trim();

        for (String dim : blacklist.split(",")) {
            try {
                Integer dimID = Integer.parseInt(dim);
                HexxitGear.addToDimBlacklist(dimID);
            } catch (Exception e) {
            }
        }
    }
}

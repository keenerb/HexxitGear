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

package sct.hexxitgear;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import sct.hexxitgear.block.BlockHexbiscus;
import sct.hexxitgear.gui.HGCreativeTab;
import sct.hexxitgear.setup.HexxitGearRegistry;
import sct.hexxitgear.event.PlayerEventHandler;
import sct.hexxitgear.tick.PlayerTracker;
import sct.hexxitgear.item.*;
import sct.hexxitgear.setup.HexxitGearConfig;
import sct.hexxitgear.world.HGWorldGen;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = HexxitGear.modId, name = "Hexxit Gear", useMetadata = true, version = HexxitGear.version)
public class HexxitGear {

    public static final String modId = "hexxitgear";
    public static final String modNetworkChannel = "HexxitGear";
    public static final String version = "1.5.2R1.0";

    @Mod.Instance(modId)
    public static HexxitGear instance;

    @SidedProxy(clientSide="sct.hexxitgear.ClientProxy", serverSide="sct.hexxitgear.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static PlayerEventHandler playerEventHandler;

    public static Block hexbiscus;

    public static Item hexicalEssence;
    public static Item hexicalDiamond;

    public static Item tribalHelmet;
    public static Item tribalChest;
    public static Item tribalLeggings;
    public static Item tribalShoes;

    public static Item thiefHelmet;
    public static Item thiefChest;
    public static Item thiefLeggings;
    public static Item thiefBoots;

    public static Item scaleHelmet;
    public static Item scaleChest;
    public static Item scaleLeggings;
    public static Item scaleBoots;

    public static List<Integer> dimensionalBlacklist = new ArrayList<Integer>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        HexxitGearConfig.setConfigFolderBase(evt.getModConfigurationDirectory());

        HexxitGearConfig.loadCommonConfig(evt);
        HexxitGearConfig.registerDimBlacklist();

        logger = evt.getModLog();
        playerEventHandler = new PlayerEventHandler();
        MinecraftForge.EVENT_BUS.register(playerEventHandler);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        hexbiscus = new BlockHexbiscus().setBlockTextureName("hexxitgear:hexbiscus");

        tribalHelmet = new ItemTribalArmor(proxy.addArmor("tribal"), 0).setUnlocalizedName("hexxitgear.tribal.helmet").setTextureName("hexxitgear:tribal.helmet");
        tribalChest = new ItemTribalArmor(proxy.addArmor("tribal"), 1).setUnlocalizedName("hexxitgear.tribal.chest").setTextureName("hexxitgear:tribal.chest");
        tribalLeggings = new ItemTribalArmor(proxy.addArmor("tribal"), 2).setUnlocalizedName("hexxitgear.tribal.leggings").setTextureName("hexxitgear:tribal.leggings");
        tribalShoes = new ItemTribalArmor(proxy.addArmor("tribal"), 3).setUnlocalizedName("hexxitgear.tribal.boots").setTextureName("hexxitgear:tribal.boots");
        scaleHelmet = new ItemScaleArmor(proxy.addArmor("scale"), 0).setUnlocalizedName("hexxitgear.scale.helmet").setTextureName("hexxitgear:scale.helmet");
        scaleChest = new ItemScaleArmor(proxy.addArmor("scale"), 1).setUnlocalizedName("hexxitgear.scale.chest").setTextureName("hexxitgear:scale.chest");
        scaleLeggings = new ItemScaleArmor(proxy.addArmor("scale"), 2).setUnlocalizedName("hexxitgear.scale.leggings").setTextureName("hexxitgear:scale.leggings");
        scaleBoots = new ItemScaleArmor(proxy.addArmor("scale"), 3).setUnlocalizedName("hexxitgear.scale.boots").setTextureName("hexxitgear:scale.boots");
        thiefHelmet = new ItemThiefArmor(proxy.addArmor("thief"), 0).setUnlocalizedName("hexxitgear.thief.helmet").setTextureName("hexxitgear:thief.helmet");
        thiefChest = new ItemThiefArmor(proxy.addArmor("thief"), 1).setUnlocalizedName("hexxitgear.thief.chest").setTextureName("hexxitgear:thief.chest");
        thiefLeggings = new ItemThiefArmor(proxy.addArmor("thief"), 2).setUnlocalizedName("hexxitgear.thief.leggings").setTextureName("hexxitgear:thief.leggings");
        thiefBoots = new ItemThiefArmor(proxy.addArmor("thief"), 3).setUnlocalizedName("hexxitgear.thief.boots").setTextureName("hexxitgear:thief.boots");

        hexicalEssence = new Item().setCreativeTab(HGCreativeTab.tab).setUnlocalizedName("hexxitgear.hexicalessence").setTextureName("hexxitgear:hexicalEssence");
        hexicalDiamond = new Item().setTextureName("hexxitgear:hexicalDiamond").setCreativeTab(HGCreativeTab.tab).setUnlocalizedName("hexxitgear.hexicaldiamond");

        GameRegistry.registerBlock(hexbiscus, hexbiscus.getUnlocalizedName());

        GameRegistry.registerWorldGenerator(new HGWorldGen(), 100);

        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        FMLCommonHandler.instance().bus().register(new PlayerTracker());
        HexxitGearRegistry.init();
    }

    public static void addToDimBlacklist(int dimID) {
        if (!dimensionalBlacklist.contains(dimID))
            dimensionalBlacklist.add(dimID);
    }

    public static List<Integer> getDimBlacklist() {
        return dimensionalBlacklist;
    }
}

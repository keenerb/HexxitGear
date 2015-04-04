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

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import sct.hexxitgear.block.BlockHexbiscus;
import sct.hexxitgear.coremod.HexxitGearResourcePack;
import sct.hexxitgear.gui.HGCreativeTab;
import sct.hexxitgear.net.HexxitGearNetwork;
import sct.hexxitgear.setup.HexxitGearRegistry;
import sct.hexxitgear.event.PlayerEventHandler;
import sct.hexxitgear.tick.PlayerTracker;
import sct.hexxitgear.item.*;
import sct.hexxitgear.setup.HexxitGearConfig;
import sct.hexxitgear.world.HGWorldGen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HexxitGear extends DummyModContainer {

    public HexxitGear() {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = HexxitGear.modId;
        metadata.version = HexxitGear.version;
        metadata.name = "Hexxit Gear";
        metadata.logoFile = "hexxitgear.png";
        metadata.authorList = ImmutableList.of("sct", "CheapShot", "Cannibalvox");
        metadata.url = "http://www.technicpack.net/";
        metadata.credits = "Developed by Technic";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return HexxitGearResourcePack.class;
    }

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

    public static Item magicHelmet;
    public static Item magicChest;
    public static Item magicLeggings;
    public static Item magicBoots;

    public static List<Integer> dimensionalBlacklist = new ArrayList<Integer>();

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {
        if (FMLCommonHandler.instance().getSide().isClient())
            proxy = new ClientProxy();
        else
            proxy = new CommonProxy();

        HexxitGearConfig.setConfigFolderBase(evt.getModConfigurationDirectory());

        HexxitGearConfig.loadCommonConfig(evt);
        HexxitGearConfig.registerDimBlacklist();

        logger = evt.getModLog();
        playerEventHandler = new PlayerEventHandler();
        MinecraftForge.EVENT_BUS.register(playerEventHandler);
        FMLCommonHandler.instance().bus().register(playerEventHandler);
    }

    @Subscribe
    public void init(FMLInitializationEvent evt) {
        HexxitGearNetwork.init();
        instance = this;
        MinecraftForge.EVENT_BUS.register(instance);

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
        magicHelmet = new ItemMagicianArmor(proxy.addArmor("magic"), 0).setUnlocalizedName("hexxitgear.magic.helmet").setTextureName("hexxitgear:magic.helmet");
        magicChest = new ItemMagicianArmor(proxy.addArmor("magic"), 1).setUnlocalizedName("hexxitgear.magic.chest").setTextureName("hexxitgear:magic.chest");
        magicLeggings = new ItemMagicianArmor(proxy.addArmor("magic"), 2).setUnlocalizedName("hexxitgear.magic.leggings").setTextureName("hexxitgear:magic.leggings");
        magicBoots = new ItemMagicianArmor(proxy.addArmor("magic"), 3).setUnlocalizedName("hexxitgear.magic.boots").setTextureName("hexxitgear:magic.boots");

        hexicalEssence = new Item().setCreativeTab(HGCreativeTab.tab).setUnlocalizedName("hexxitgear.hexicalessence").setTextureName("hexxitgear:hexicalEssence");
        hexicalDiamond = new Item().setTextureName("hexxitgear:hexicalDiamond").setCreativeTab(HGCreativeTab.tab).setUnlocalizedName("hexxitgear.hexicaldiamond");

        GameRegistry.registerBlock(hexbiscus, "hexbiscus");
        GameRegistry.registerItem(tribalHelmet, "tribalHelmet");
        GameRegistry.registerItem(tribalChest, "tribalChest");
        GameRegistry.registerItem(tribalLeggings, "tribalLeggings");
        GameRegistry.registerItem(tribalShoes, "tribalShoes");
        GameRegistry.registerItem(scaleHelmet, "scaleHelmet");
        GameRegistry.registerItem(scaleChest, "scaleChest");
        GameRegistry.registerItem(scaleLeggings, "scaleLeggings");
        GameRegistry.registerItem(scaleBoots, "scaleBoots");
        GameRegistry.registerItem(thiefHelmet, "thiefHelmet");
        GameRegistry.registerItem(thiefChest, "thiefChest");
        GameRegistry.registerItem(thiefLeggings, "thiefLeggings");
        GameRegistry.registerItem(thiefBoots, "thiefBoots");
        GameRegistry.registerItem(magicHelmet, "magicHelmet");
        GameRegistry.registerItem(magicChest, "magicChest");
        GameRegistry.registerItem(magicLeggings, "magicLeggings");
        GameRegistry.registerItem(magicBoots, "magicBoots");
        GameRegistry.registerItem(hexicalEssence, "hexxicalEssence");
        GameRegistry.registerItem(hexicalDiamond, "hexxicalDiamond");

        GameRegistry.registerWorldGenerator(new HGWorldGen(), 100);

        proxy.init();
    }

    @Subscribe
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

    public static void translateAndAdd(String key, List list) {
        for (int i=0;i<10;i++) {
            if (StatCollector.canTranslate(key + Integer.toString(i))) {
                String line = StatCollector.translateToLocal(key + Integer.toString(i));
                list.add(line);
            } else
                break;
        }
    }
}

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

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import sct.hexxitgear.control.HGKeyHandler;
import sct.hexxitgear.model.*;

public class ClientProxy extends CommonProxy {

    public static ModelScaleHelmet scaleHelmet = new ModelScaleHelmet();
    public static ModelDualLayerArmor scaleChest = new ModelDualLayerArmor(1.0f);
    public static ModelDualLayerArmor scaleLeggings = new ModelDualLayerArmor(0.5f);
    public static ModelDualLayerArmor scaleFeet = new ModelDualLayerArmor(0.5f);
    public static ModelSkullHelmet tribalHelmet = new ModelSkullHelmet();
    public static ModelDualLayerArmor tribalChest = new ModelDualLayerArmor(1.0f);
    public static ModelDualLayerArmor tribalLeggings = new ModelDualLayerArmor(0.5f);
    public static ModelDualLayerArmor tribalFeet = new ModelDualLayerArmor(0.5f);
    public static ModelHoodHelmet thiefHelmet = new ModelHoodHelmet();
    public static ModelDualLayerArmor thiefLeggings = new ModelDualLayerArmor(0.5f);
    public static ModelDualLayerArmor thiefChest = new ModelDualLayerArmor(1.0f);
    public static ModelDualLayerArmor thiefFeet = new ModelDualLayerArmor(0.5f);
    public static ModelSageHood sageHelmet = new ModelSageHood();
    public static ModelDualLayerArmor sageLeggings = new ModelDualLayerArmor(0.5f);
    public static ModelDualLayerArmor sageChest = new ModelDualLayerArmor(1.0f);
    public static ModelDualLayerArmor sageFeet = new ModelDualLayerArmor(0.5f);


    @Override
    public int addArmor(String armorName) {
        return RenderingRegistry.addNewArmourRendererPrefix(armorName);
    }

    @Override
    public EntityPlayer findPlayer(String playerName) {
        for (Object a : FMLClientHandler.instance().getClient().theWorld.playerEntities) {
            EntityPlayer player = (EntityPlayer) a;
            if (player.getDisplayName().toLowerCase().equals(playerName.toLowerCase())) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void setPlayerCape(String playerName, String capeUrl) {
        EntityPlayer player = HexxitGear.proxy.findPlayer(playerName);
        setPlayerCape(player, capeUrl);
    }

    @Override
    public void setPlayerCape(EntityPlayer player, String capeUrl) {
        String token = capeUrl.substring(capeUrl.lastIndexOf("/") + 1);
        ResourceLocation capeResource = new ResourceLocation("hexxitgear:cloaks/" + token);
        if (player != null && player instanceof AbstractClientPlayer) {
            ResourceLocation locationCape = ((AbstractClientPlayer) player).locationCape;

            if (locationCape == null || !locationCape.equals(capeResource)) {
                AbstractClientPlayer capePlayer = (AbstractClientPlayer) player;
                capePlayer.onSkinAvailable(MinecraftProfileTexture.Type.CAPE, capeResource);
            }
        }
    }

    @Override
    public void resetPlayerCape(String playerName) {
        EntityPlayer player = HexxitGear.proxy.findPlayer(playerName);
        if (player != null && player instanceof AbstractClientPlayer) {
            AbstractClientPlayer capePlayer = (AbstractClientPlayer) player;
            capePlayer.onSkinAvailable(MinecraftProfileTexture.Type.CAPE, null);
            Minecraft.getMinecraft().getSkinManager().func_152790_a(capePlayer.getGameProfile(), capePlayer, true);
        }
    }

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        FMLCommonHandler.instance().bus().register(new HGKeyHandler());
    }

    @Override
    public boolean isClientPlayer(EntityPlayer player) {
        return player == Minecraft.getMinecraft().thePlayer;
    }
}

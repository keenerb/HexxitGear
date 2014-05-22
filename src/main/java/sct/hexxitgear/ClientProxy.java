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

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import sct.hexxitgear.control.HGKeyHandler;
import sct.hexxitgear.tick.PlayerTickHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public int addArmor(String armorName) {
        return RenderingRegistry.addNewArmourRendererPrefix(armorName);
    }

    @Override
    public EntityPlayer findPlayer(String playerName) {
        for (Object a : FMLClientHandler.instance().getClient().theWorld.playerEntities) {
            EntityPlayer player = (EntityPlayer) a;
            if (player.username.toLowerCase().equals(playerName.toLowerCase())) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void setPlayerCape(String playerName, String capeUrl) {
        EntityPlayer player = HexxitGear.proxy.findPlayer(playerName);
        if (player != null && player instanceof AbstractClientPlayer) {

            String token = capeUrl.substring(capeUrl.lastIndexOf("/")+1);

            AbstractClientPlayer capePlayer = (AbstractClientPlayer)player;
            capePlayer.locationCape = new ResourceLocation("cloaks/hexxitgear/"+token);
            capePlayer.downloadImageCape = AbstractClientPlayer.getDownloadImage(capePlayer.locationCape, capeUrl, (ResourceLocation)null, (IImageBuffer)null);
        }
    }

    @Override
    public void resetPlayerCape(String playerName) {
        EntityPlayer player = HexxitGear.proxy.findPlayer(playerName);
        if (player != null && player instanceof AbstractClientPlayer) {
            AbstractClientPlayer capePlayer = (AbstractClientPlayer)player;
            capePlayer.locationCape = capePlayer.getLocationCape(capePlayer.username);
            capePlayer.downloadImageCape = AbstractClientPlayer.getDownloadImageCape(capePlayer.locationCape, capePlayer.username);
        }
    }

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.CLIENT);
        KeyBindingRegistry.registerKeyBinding(new HGKeyHandler());
    }
}

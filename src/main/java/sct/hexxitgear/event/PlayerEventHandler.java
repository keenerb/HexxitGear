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

package sct.hexxitgear.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.core.CapeHandler;
import sct.hexxitgear.mixinsupport.climbing.IClimbingShoesWearer;

public class PlayerEventHandler {

    private int ticks = 0;

    @SubscribeEvent
    public void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (ticks > 16) {
            if (event.entityLiving != null && event.entityLiving instanceof EntityPlayer) {
                if (!event.entityLiving.worldObj.isRemote)
                    return;

                EntityPlayer player = (EntityPlayer) event.entityLiving;
                String capeUrl = CapeHandler.getCapeUrl(player.getDisplayName());
                if (capeUrl != null && player instanceof AbstractClientPlayer) {
                    HexxitGear.proxy.setPlayerCape(player, capeUrl);
                }
            }
            ticks = 0;
        }
        ticks++;
    }

    @SubscribeEvent
    public void wallRunningArmorEvent(TickEvent.PlayerTickEvent event) {
        IClimbingShoesWearer shoesWearer = (IClimbingShoesWearer) event.player;

        ItemStack boots = event.player.getCurrentArmor(0);
        shoesWearer.setClimbingShoesEquipped(boots != null && boots.getItem() == HexxitGear.tribalShoes);
    }
}

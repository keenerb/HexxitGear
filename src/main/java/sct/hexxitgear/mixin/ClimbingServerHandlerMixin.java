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

package sct.hexxitgear.mixin;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sct.hexxitgear.mixinsupport.climbing.ClimbingHelper;
import sct.hexxitgear.mixinsupport.climbing.IClimbingShoesWearer;

@Mixin(NetHandlerPlayServer.class)
public class ClimbingServerHandlerMixin {

    @Redirect(method="processPlayer", at=@At(value="INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;moveEntity(DDD)V"))
    public void proxyMoveEntity(EntityPlayerMP this$0, double motionX, double motionY, double motionZ) {
        IClimbingShoesWearer wearer = (IClimbingShoesWearer)this$0;
        wearer.setUpdating(true);
        if (wearer.areClimbingShoesEquipped()) {
            ClimbingHelper.transformEntity(this$0, wearer.getTransformer());
        }
        this$0.moveEntity(motionX, motionY, motionZ);
        if (wearer.areClimbingShoesEquipped()) {
            ClimbingHelper.untransformEntity(this$0, wearer.getTransformer());
        }
        wearer.setUpdating(false);
    }
}

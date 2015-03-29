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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sct.hexxitgear.mixinsupport.climbing.ClimbingHelper;
import sct.hexxitgear.mixinsupport.climbing.IClimbingShoesWearer;

@Mixin(EntityLivingBase.class)
public abstract class ClimbingShoesLivingMixin extends Entity implements IClimbingShoesWearer {
    protected ClimbingShoesLivingMixin(World world) { super(world); }

    @ModifyArg(method="moveEntityWithHeading", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;getChunkFromBlockCoords(II)Lnet/minecraft/world/chunk/Chunk;"), index = 0)
    private int getChunkFromBlockCoordsTransformX(int x, int z) {
        if (areClimbingShoesEquipped())
            return (int)getTransformer().unGetX(x, this.posY, z);
        else
            return x;
    }

    @ModifyArg(method="moveEntityWithHeading", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;getChunkFromBlockCoords(II)Lnet/minecraft/world/chunk/Chunk;"), index = 1)
    private int getChunkFromBlockCoordsTransformZ(int x, int z) {
        if (areClimbingShoesEquipped())
            return (int)getTransformer().unGetZ(x, this.posY, z);
        else
            return z;
    }

    @Inject(method="getLook", at=@At("RETURN"))
    private void modifyLookResult(float partialTick, CallbackInfoReturnable<Vec3> info) {
        if (getTransformer() != null && getTransformer().getAxisY() != ForgeDirection.UP) {
            Vec3 vector = info.getReturnValue();
            ClimbingHelper.rotateLookVector(vector, getTransformer());
        }
    }
}

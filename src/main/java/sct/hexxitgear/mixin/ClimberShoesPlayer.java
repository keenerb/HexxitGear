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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.hexxitgear.item.IClimbingShoesWearer;

@Mixin(EntityLivingBase.class)
public abstract class ClimberShoesPlayer extends Entity implements IClimbingShoesWearer {
    private ClimberShoesPlayer(World world) {
        super(world);
    }

    private ForgeDirection climbingShoesDirection;
    private boolean areClimbingShoesEquipped;

    @Inject(method = "<init>", at=@At("RETURN"))
    private void onConstructed(World world, CallbackInfo info) {
        this.climbingShoesDirection = ForgeDirection.DOWN;
        this.areClimbingShoesEquipped = false;
    }

    public void setClimbingShoesEquipped(boolean equipped) {
        this.areClimbingShoesEquipped = equipped;
    }

    @ModifyArg(method = "onUpdate()V", at = @At(value = "INVOKE", target="Lnet/minecraft/world/World;getCollidingBoundingBoxes(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"))
    private Entity entityGet(Entity entity) {
        return entity;
    }
}

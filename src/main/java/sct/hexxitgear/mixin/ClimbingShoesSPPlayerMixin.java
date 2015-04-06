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

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.hexxitgear.mixinsupport.climbing.ClimbingHelper;
import sct.hexxitgear.mixinsupport.climbing.IClimbingShoesWearer;
import sct.hexxitgear.mixinsupport.climbing.VectorTransformer;

@Mixin(EntityPlayerSP.class)
public abstract class ClimbingShoesSPPlayerMixin extends AbstractClientPlayer implements IClimbingShoesWearer {
    public ClimbingShoesSPPlayerMixin(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }

    protected boolean func_145771_j(double p_145771_1_, double p_145771_3_, double p_145771_5_){return true;}

    @Redirect(method="onLivingUpdate", at=@At(value="INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;func_145771_j(DDD)Z"))
    private boolean proxyFunc145771J(EntityPlayerSP this$0, double x, double y, double z) {
        if (areClimbingShoesEquipped()) {
            double xOffset = x - this.posX;
            double yOffset = y - this.posY;
            double zOffset = z - this.posZ;
            x = this.posX + getTransformer().getX(xOffset, yOffset, zOffset);
            y = this.posY + getTransformer().getY(xOffset, yOffset, zOffset);
            z = this.posZ + getTransformer().getZ(xOffset, yOffset, zOffset);
        }
        return func_145771_j(x, y, z);
    }

    @Redirect(method="onLivingUpdate", at=@At(value="INVOKE", target="Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 3))
    private void proxySetSprinting(EntityPlayerSP this$0, boolean sprinting) {
        if (!sprinting && this$0.isCollidedHorizontally) {
            boolean canSprint = (float)this$0.getFoodStats().getFoodLevel() > 6.0F || this$0.capabilities.allowFlying;
            boolean isSprinting = this$0.movementInput.moveForward >= 0.8f;

            if (canSprint && isSprinting && ((IClimbingShoesWearer)this$0).areClimbingShoesEquipped())
                return;
        }

        this$0.setSprinting(sprinting);
    }
}

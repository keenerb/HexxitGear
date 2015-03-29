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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sct.hexxitgear.mixinsupport.climbing.ClimbingHelper;
import sct.hexxitgear.mixinsupport.climbing.IClimbingShoesWearer;
import sct.hexxitgear.mixinsupport.climbing.VectorTransformer;

@Mixin(Entity.class)
public abstract class ClimbingShoesEntityMixin implements IClimbingShoesWearer {
    protected ClimbingShoesEntityMixin(World world) {}

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public final AxisAlignedBB boundingBox=null;
    @Shadow
    public void kill() {}

    @Inject(method="setSize", at = @At(value="FIELD", target="Lnet/minecraft/util/AxisAlignedBB;maxY", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    private void afterSetSize(float width, float height, CallbackInfo info) {
        if (getTransformer() != null)
            ClimbingHelper.rotateEntityBB((Entity)(Object)this, getTransformer());
    }

    @Shadow
    public float width;

    @Redirect(method="setPosition", at=@At(value = "INVOKE", target = "Lnet/minecraft/util/AxisAlignedBB;setBounds(DDDDDD)Lnet/minecraft/util/AxisAlignedBB;"))
    private AxisAlignedBB proxySetBounds(AxisAlignedBB this$0, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (this.getTransformer() == null || this.getTransformer().getAxisY() == ForgeDirection.UP)
            return this$0.setBounds(minX, minY, minZ, maxX, maxY, maxZ);

        if (areClimbingShoesEquipped())
            ClimbingHelper.untransformBB(this.boundingBox, getTransformer());

        double halfWidth = this.width / 2.0;
        double minYOffset = minY - this.posY;
        double maxYOffset = maxY - this.posY;

        minX = this.posX + this.getTransformer().getX(-halfWidth, minYOffset, -halfWidth);
        minY = this.posY + this.getTransformer().getY(-halfWidth, minYOffset, -halfWidth);
        minZ = this.posZ + this.getTransformer().getZ(-halfWidth, minYOffset, -halfWidth);
        maxX = this.posX + this.getTransformer().getX(halfWidth, maxYOffset, halfWidth);
        maxY = this.posY + this.getTransformer().getY(halfWidth, maxYOffset, halfWidth);
        maxZ = this.posZ + this.getTransformer().getZ(halfWidth, maxYOffset, halfWidth);
        AxisAlignedBB bb = this$0.setBounds(minX, minY, minZ, maxX, maxY, maxZ);

        if (areClimbingShoesEquipped())
            ClimbingHelper.transformBB(this.boundingBox, getTransformer());

        return bb;
    }

    @Inject(method="onEntityUpdate", at = @At(value="INVOKE", target="Lnet/minecraft/entity/Entity;handleWaterMovement()V", shift=At.Shift.AFTER))
    private void afterWaterMovement(CallbackInfo info) {
        VectorTransformer transformer = getTransformer();
        if (transformer != null) {
            double y = transformer.unGetY(this.posX, this.posY, this.posZ);
            if (y < -64)
                this.kill();
        }
    }

    @Redirect(method="onEntityUpdate", at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;kill()V"))
    private void proxyKill(Entity this$0) {
        if (!areClimbingShoesEquipped())
            this.kill();
    }

    @Redirect(method="func_145775_I", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;checkChunksExist(IIIIII)Z"))
    private boolean proxyCheckChunksExist(World this$0, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (!areClimbingShoesEquipped()) {
            return this$0.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ);
        }
        ClimbingHelper.untransformEntity((Entity)(Object)this, getTransformer());
        int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
        int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
        int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
        int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
        int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
        int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);
        boolean result = this$0.checkChunksExist(i, j, k, l, i1, j1);
        ClimbingHelper.transformEntity((Entity)(Object)this, getTransformer());
        return result;
    }

    @ModifyArg(method="func_145775_I", at=@At(value="INVOKE", target="Lnet/minecraft/block/Block;onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V"), index = 1)
    private int onEntityCollidedWithBlockTransformX(World world, int x, int y, int z, Entity entity) {
        if (areClimbingShoesEquipped()) {
            return (int)getTransformer().unGetX(x, y, z);
        } else
            return x;
    }

    @ModifyArg(method="func_145775_I", at=@At(value="INVOKE", target="Lnet/minecraft/block/Block;onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V"), index = 2)
    private int onEntityCollidedWithBlockTransformY(World world, int x, int y, int z, Entity entity) {
        if (areClimbingShoesEquipped()) {
            return (int)getTransformer().unGetY(x, y, z);
        } else
            return y;
    }

    @ModifyArg(method="func_145775_I", at=@At(value="INVOKE", target="Lnet/minecraft/block/Block;onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V"), index = 3)
    private int onEntityCollidedWithBlockTransformZ(World world, int x, int y, int z, Entity entity) {
        if (areClimbingShoesEquipped()) {
            return (int)getTransformer().unGetZ(x, y, z);
        } else
            return z;
    }

    @Override
    public VectorTransformer getTransformer() { return null; }

    @Override
    public void setClimbingShoesEquipped(boolean equipped) { }
    @Override
    public boolean areClimbingShoesEquipped() { return false; }
    @Override
    public void setUpdating(boolean updating) {}
    @Override
    public boolean isUpdating() { return false; }
}

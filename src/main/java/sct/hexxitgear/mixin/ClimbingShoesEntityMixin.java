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
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.hexxitgear.item.climbing.IClimbingShoesWearer;
import sct.hexxitgear.item.climbing.VectorTransformer;

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
    public void kill() {}

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
        if (getTransformer() == null)
            this.kill();
    }

    @Override
    public VectorTransformer getTransformer() { return null; }

    @Override
    public void setClimbingShoesEquipped(boolean equipped) { }
    @Override
    public boolean areClimbingShoesEquipped() { return false; }
}

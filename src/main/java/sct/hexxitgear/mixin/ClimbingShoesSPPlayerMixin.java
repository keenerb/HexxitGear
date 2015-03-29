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
}

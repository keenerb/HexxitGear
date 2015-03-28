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

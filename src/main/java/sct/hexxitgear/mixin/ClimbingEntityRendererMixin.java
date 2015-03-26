package sct.hexxitgear.mixin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.hexxitgear.item.climbing.IClimbingShoesWearer;

@Mixin(EntityRenderer.class)
@SideOnly(Side.CLIENT)
public class ClimbingEntityRendererMixin {

    @Shadow
    private Minecraft mc;

    @Inject(method="setupCameraTransform", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;orientCamera(F)V", shift= At.Shift.AFTER))
    private void preRotateCamera(float x, int z, CallbackInfo info) {
        IClimbingShoesWearer wearer = ((IClimbingShoesWearer)this.mc.renderViewEntity);
        if (wearer.areClimbingShoesEquipped()) {
            if (wearer.getTransformer().getAxisY() == ForgeDirection.DOWN)
                GL11.glRotatef(180, 0, 0, 1);
            else if (wearer.getTransformer().getAxisY() == ForgeDirection.WEST)
                GL11.glRotatef(90, 0, 0, 1);
            else if (wearer.getTransformer().getAxisY() == ForgeDirection.EAST)
                GL11.glRotatef(90, 0, 0, -1);
            else if (wearer.getTransformer().getAxisY() == ForgeDirection.NORTH)
                GL11.glRotatef(90, 1, 0, 0);
            else if (wearer.getTransformer().getAxisY() == ForgeDirection.SOUTH)
                GL11.glRotatef(90, -1, 0, 0);
        }
    }
}

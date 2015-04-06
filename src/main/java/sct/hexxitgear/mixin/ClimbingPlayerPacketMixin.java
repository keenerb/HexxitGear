package sct.hexxitgear.mixin;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import sct.hexxitgear.mixinsupport.climbing.ClimbingHelper;
import sct.hexxitgear.mixinsupport.climbing.IClimbingShoesWearer;
import sct.hexxitgear.mixinsupport.climbing.VectorTransformer;

@Mixin(C03PacketPlayer.class)
public abstract class ClimbingPlayerPacketMixin extends Packet {
    @Redirect(method="processPacket(Lnet/minecraft/network/play/INetHandlerPlayServer;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/network/play/INetHandlerPlayServer;processPlayer(Lnet/minecraft/network/play/client/C03PacketPlayer;)V"))
    private void processPlayerTransformPacket(INetHandlerPlayServer this$0, C03PacketPlayer packet) {
        if (this$0 instanceof NetHandlerPlayServer) {
            EntityPlayerMP player = ((NetHandlerPlayServer) this$0).playerEntity;
            VectorTransformer transformer = ((IClimbingShoesWearer)player).getTransformer();
            packet = ClimbingHelper.transformPacket(packet, player, transformer);
        }
        this$0.processPlayer(packet);
    }
}

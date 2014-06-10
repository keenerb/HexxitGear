package sct.hexxitgear.net.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import sct.hexxitgear.core.CapeHandler;

public class CapeChangePacket extends HexxitGearPacketBase {

    private String playerDisplayName;
    private String playerCapeUrl;

    public CapeChangePacket() {}
    public CapeChangePacket(String playerName, String capeUrl) {
        this.playerDisplayName = playerName;
        this.playerCapeUrl = capeUrl;
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(playerDisplayName);
        out.writeUTF(playerCapeUrl);
    }

    @Override
    public void read(ByteArrayDataInput in) {
        playerDisplayName = in.readUTF();
        playerCapeUrl = in.readUTF();
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        CapeHandler.readCapeUpdate(playerDisplayName, playerCapeUrl);
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {
        //Server to client packet
    }
}

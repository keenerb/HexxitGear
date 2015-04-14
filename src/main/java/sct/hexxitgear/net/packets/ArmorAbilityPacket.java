package sct.hexxitgear.net.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import sct.hexxitgear.core.ability.AbilityHandler;

public class ArmorAbilityPacket extends HexxitGearPacketBase {

    private String playerName;

    public ArmorAbilityPacket() {
    }

    public ArmorAbilityPacket(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(playerName);
    }

    @Override
    public void read(ByteArrayDataInput in) {
        playerName = in.readUTF();
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        //Client to server packet
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {
        AbilityHandler.readAbilityPacket(playerName);
    }
}

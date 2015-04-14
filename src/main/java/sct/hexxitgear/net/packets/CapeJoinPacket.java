package sct.hexxitgear.net.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import sct.hexxitgear.HexxitGear;

import java.util.HashMap;
import java.util.Map;

public class CapeJoinPacket extends HexxitGearPacketBase {

    private Map<String, String> allPlayerCapes = new HashMap<String, String>();

    public CapeJoinPacket() {
    }

    public CapeJoinPacket(Map<String, String> capes) {
        for (String playerName : capes.keySet()) {
            allPlayerCapes.put(playerName, capes.get(playerName));
        }
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeInt(allPlayerCapes.keySet().size());

        for (String playerName : allPlayerCapes.keySet()) {
            out.writeUTF(playerName);
            String capeUrl = allPlayerCapes.get(playerName);
            out.writeUTF(capeUrl);
        }
    }

    @Override
    public void read(ByteArrayDataInput in) {
        int capeCount = in.readInt();

        for (int i = 0; i < capeCount; i++) {
            String playerName = in.readUTF();
            String capeUrl = in.readUTF();
            allPlayerCapes.put(playerName, capeUrl);
        }
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        for (String playerName : allPlayerCapes.keySet()) {
            String capeUrl = allPlayerCapes.get(playerName);
            HexxitGear.proxy.setPlayerCape(playerName, capeUrl);
        }
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {
        //Server to client packet
    }
}

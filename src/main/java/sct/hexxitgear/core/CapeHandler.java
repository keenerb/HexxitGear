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

package sct.hexxitgear.core;

import net.minecraft.entity.player.EntityPlayer;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.net.HexxitGearNetwork;
import sct.hexxitgear.net.Packets;
import sct.hexxitgear.net.packets.CapeChangePacket;
import sct.hexxitgear.net.packets.CapeJoinPacket;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapeHandler {

    public static Map<String, String> capes = new HashMap<String, String>();

    public static void registerCape(String player, String capeUrl) {
        capes.put(player, capeUrl);
        sendCapeUpdate(player, capeUrl);
    }

    public static void removeCape(String playerName) {
        if (playerName != null) {
            capes.remove(playerName);
            sendCapeUpdate(playerName, null);
        }
    }

    public static String getCapeUrl(String player) {
        return capes.get(player);
    }

    public static void sendCapeUpdate(String player, String capeUrl) {
        if (capeUrl == null) {
            capeUrl = "";
        }

        HexxitGearNetwork.sendToAllPlayers(new CapeChangePacket(player, capeUrl));
    }

    public static void sendJoinUpdate(EntityPlayer player) {
        HexxitGearNetwork.sendToPlayer(new CapeJoinPacket(capes), player);
    }

    public static void readCapeUpdate(String playerName, String capeUrl) {
        EntityPlayer player = HexxitGear.proxy.findPlayer(playerName);
        if (!capeUrl.equals("")) {
            capes.put(playerName, capeUrl);
            if (player != null) {
                HexxitGear.proxy.setPlayerCape(playerName, capes.get(playerName));
            }
        } else {
            capes.remove(playerName);
            if (player != null)
                HexxitGear.proxy.resetPlayerCape(playerName);
        }
    }

    public static void readJoinUpdate(DataInputStream data) {
        try {
            capes = new HashMap<String, String>();

            int count = data.readByte();

            String playerName, capeUrl;
            for (int i = 0; i < count; i++) {
                playerName = data.readUTF();
                capeUrl = data.readUTF();
                capes.put(playerName, capeUrl);
                HexxitGear.proxy.setPlayerCape(playerName, capeUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

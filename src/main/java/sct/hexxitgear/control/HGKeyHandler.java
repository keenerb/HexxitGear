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

package sct.hexxitgear.control;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.core.ArmorSet;
import sct.hexxitgear.net.PacketWrapper;
import sct.hexxitgear.net.Packets;

import java.util.EnumSet;

@SideOnly(Side.CLIENT)
public class HGKeyHandler {

    public static KeyBinding activateHexxitArmor = new KeyBinding("Activate Hexxit Gear Armor", Keyboard.KEY_X, "Hexxit Gear");
    public static KeyBinding[] keybindArray = new KeyBinding[]{activateHexxitArmor};
    public static boolean[] repeats = new boolean[keybindArray.length];

    public HGKeyHandler() {
        ClientRegistry.registerKeyBinding(activateHexxitArmor);
    }

    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent event) {
        if (!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

            if (activateHexxitArmor.isPressed()) {
                if (ArmorSet.getPlayerArmorSet(player.getDisplayName()) != null) {
                    Object[] data = new Object[] { player.getDisplayName() };
                    PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(HexxitGear.modNetworkChannel, Packets.armorAbility, data));
                }
            }
        }
    }
}

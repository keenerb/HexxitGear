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

package sct.hexxitgear;

import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {

    public void init() {
        registerHandlers();
    }

    public int addArmor(String armorName) {
        return 0;
    }

    public EntityPlayer findPlayer(String playerName) {
        return null;
    }

    public void setPlayerCape(String playerName, String capeUrl) {
    }

    public void setPlayerCape(EntityPlayer player, String capeUrl) {
    }

    public void resetPlayerCape(String playerName) {
    }

    public void registerHandlers() {
    }

    public boolean isClientPlayer(EntityPlayer player) {
        return false;
    }
}

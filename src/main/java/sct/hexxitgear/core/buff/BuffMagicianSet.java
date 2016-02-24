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

package sct.hexxitgear.core.buff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class BuffMagicianSet implements IBuffHandler {

    @Override
    public void applyPlayerBuffs(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20, 0));
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 21 * 20, 0));
//        player.landMovementFactor = 0.15f;
//        player.jumpMovementFactor = player.landMovementFactor * 0.5F;

//        player.stepHeight = 1.003F;
    }

    @Override
    public void removePlayerBuffs(EntityPlayer player) {
        if (player.stepHeight == 1.003F) {
            player.stepHeight = 0.5001F;
        }
    }
}

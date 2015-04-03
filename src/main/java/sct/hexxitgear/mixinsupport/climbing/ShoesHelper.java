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

package sct.hexxitgear.mixinsupport.climbing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import sct.hexxitgear.core.ArmorSet;
import sct.hexxitgear.net.HexxitGearNetwork;
import sct.hexxitgear.net.packets.PolarityPacket;

import java.util.List;

public class ShoesHelper {
    public static void processShoes(EntityPlayer entity, List<ForgeDirection> collidedSides, Vec3 lookDir) {
        IClimbingShoesWearer wearer = (IClimbingShoesWearer)entity;

        if (entity.isJumping || entity.isInWater() || entity.fire > 0 || entity.fallDistance > 0.5) {
            wearer.setFloor(ForgeDirection.DOWN);
            return;
        }

        if (entity.onGround) {
            ForgeDirection facingDir = getFacingDirection(wearer.getTransformer().getAxisY(), lookDir);

            if (collidedSides.contains(facingDir)) {
                wearer.setFloor(facingDir);
            }
        }

        boolean hasFullSet = ArmorSet.getPlayerArmorSet(entity.getDisplayName()) != null;

        if ((hasFullSet && wearer.getTransformer().getAxisY() == ForgeDirection.DOWN) || (!hasFullSet && wearer.getTransformer().getAxisY() != ForgeDirection.UP)) {
            //Calculate distance to spend
            double diffX = entity.posX - entity.prevPosX;
            double diffY = entity.posY - entity.prevPosY;
            double diffZ = entity.posZ - entity.prevPosZ;
            double realDiffX = wearer.getTransformer().getX(diffX, diffY, diffZ);
            double realDiffZ = wearer.getTransformer().getZ(diffX, diffY, diffZ);

            int i = Math.round(MathHelper.sqrt_double(realDiffX * realDiffX + realDiffZ * realDiffZ) * 100.0F);
            wearer.spendDistance(i);
        } else {
            wearer.resetDistance();
        }
    }

    public static ForgeDirection getFacingDirection(ForgeDirection currentYAxis, Vec3 lookDir) {
        double[] nonVerticalComponents = new double[2];
        int index = 0;

        if (currentYAxis != ForgeDirection.EAST && currentYAxis != ForgeDirection.WEST)
            nonVerticalComponents[index++] = lookDir.xCoord;
        if (currentYAxis != ForgeDirection.NORTH && currentYAxis != ForgeDirection.SOUTH)
            nonVerticalComponents[index++] = lookDir.zCoord;
        if (currentYAxis != ForgeDirection.UP && currentYAxis != ForgeDirection.DOWN)
            nonVerticalComponents[index++] = lookDir.yCoord;

        double largestComponent = Math.max(Math.abs(nonVerticalComponents[0]), Math.abs(nonVerticalComponents[1]));

        if (Math.abs(lookDir.xCoord) == largestComponent) {
            if (lookDir.xCoord > 0)
                return ForgeDirection.EAST;
            else
                return ForgeDirection.WEST;
        }
        if (Math.abs(lookDir.yCoord) == largestComponent) {
            if (lookDir.yCoord > 0)
                return ForgeDirection.UP;
            else
                return ForgeDirection.DOWN;
        }

        if (Math.abs(lookDir.zCoord) == largestComponent) {
            if (lookDir.zCoord > 0)
                return ForgeDirection.SOUTH;
            else
                return ForgeDirection.NORTH;
        }

        return ForgeDirection.UNKNOWN;
    }
}

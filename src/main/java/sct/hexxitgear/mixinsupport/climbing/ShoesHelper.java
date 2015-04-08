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
import net.minecraft.util.AxisAlignedBB;
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
        boolean hasFullSet = ArmorSet.getPlayerArmorSet(entity.getDisplayName()) != null;
        boolean isOnEasySurface = (hasFullSet && wearer.getTransformer().getAxisY() != ForgeDirection.DOWN) || (!hasFullSet && wearer.getTransformer().getAxisY() == ForgeDirection.UP);

        float maxFallDistance = 0.5001F;
        if (entity.isJumping || entity.isInWater() || entity.fire > 0 || !entity.isSprinting()) {
            wearer.setFloor(ForgeDirection.DOWN);
            return;
        }

        if (entity.fallDistance > maxFallDistance) {
            boolean switchToFloor = true;
            if (hasFullSet && entity.fallDistance < 1.5f) {
                switchToFloor = false;
            }
            if (switchToFloor) {
                wearer.setFloor(ForgeDirection.DOWN);
                return;
            }
        }

        if (entity.onGround) {

            ForgeDirection facingDir = getFacingDirection(wearer.getTransformer().getAxisY(), lookDir);

            if (collidedSides.contains(facingDir) && (hasFullSet || facingDir != ForgeDirection.UP)) {
                wearer.setFloor(facingDir);
            } else if (entity.isCollidedHorizontally && wearer.getTransformer().getAxisY() != ForgeDirection.UP) {
                if (collidedSides.contains(ForgeDirection.UP) && wearer.getTransformer().getAxisY() != ForgeDirection.DOWN) {
                    //We're going to expand our bounding box upward but we're already flush with the ceiling so we need to
                    //drop our position downward a bit first
                    float offset = (entity.width /2.0f) - entity.yOffset;
                    entity.posY += offset;
                    entity.boundingBox.offset(0, offset, 0);
                }
                entity.setSprinting(false);
                wearer.setFloor(ForgeDirection.DOWN);
            }
        }

        if (!isOnEasySurface) {
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

    @SuppressWarnings("rawtypes")
    public static boolean processStep(EntityPlayer player, double motionX, double motionZ) {
        double motionY = 1.003;
        IClimbingShoesWearer wearer = (IClimbingShoesWearer)player;
        boolean hasFullSet = ArmorSet.getPlayerArmorSet(player.getDisplayName()) != null;
        boolean isOnEasySurface = (hasFullSet && wearer.getTransformer().getAxisY() != ForgeDirection.DOWN) || (!hasFullSet && wearer.getTransformer().getAxisY() == ForgeDirection.UP);
        AxisAlignedBB boundingBoxCopy = player.boundingBox.copy();

        if (isOnEasySurface && player.isSprinting()) {
            player.boundingBox.setBB(boundingBoxCopy);
            double x = motionX;
            double y = motionY;
            double z = motionZ;
            List collision = player.worldObj.getCollidingBoundingBoxes(player, player.boundingBox.addCoord(x, y, z));

            for (int i = 0; i < collision.size(); ++i) {
                y = ((AxisAlignedBB)collision.get(i)).calculateYOffset(player.boundingBox, y);
            }

            player.boundingBox.offset(0,y,0);

            if (motionY != y) {
                x = 0;
                y = 0;
                z = 0;
            }

            for (int i = 0; i < collision.size(); i++) {
                x = ((AxisAlignedBB)collision.get(i)).calculateXOffset(player.boundingBox, x);
            }

            player.boundingBox.offset(motionX, 0, 0);

            if (motionX != x) {
                x = 0;
                y = 0;
                z = 0;
            }

            for (int i = 0; i < collision.size(); i++) {
                z = ((AxisAlignedBB)collision.get(i)).calculateZOffset(player.boundingBox, z);
            }

            player.boundingBox.offset(0, 0, motionZ);

            if (motionZ != z) {
                x = 0;
                y = 0;
                z = 0;
            }

            if (motionY != y) {
                x = 0;
                y = 0;
                z = 0;
            } else {
                y = -1.003;

                collision = player.worldObj.getCollidingBoundingBoxes(player, player.boundingBox.addCoord(0,y,0));

                for (int i = 0; i < collision.size(); i++) {
                    y = ((AxisAlignedBB)collision.get(i)).calculateYOffset(player.boundingBox, y);
                }
                player.boundingBox.offset(0, y, 0);

                player.posX = (player.boundingBox.minX + player.boundingBox.maxX) / 2.0D;
                player.posY = player.boundingBox.minY + (double)player.yOffset - (double)player.yOffset2;
                player.posZ = (player.boundingBox.minZ + player.boundingBox.maxZ) / 2.0D;
                player.isCollidedHorizontally = false;
                if (!player.isCollidedVertically) player.isCollided = false;
                return true;
            }
        }

        player.boundingBox.setBB(boundingBoxCopy);
        return false;
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

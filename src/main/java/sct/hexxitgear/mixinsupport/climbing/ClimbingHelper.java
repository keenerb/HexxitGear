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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class ClimbingHelper {
    public static C03PacketPlayer transformPacket(C03PacketPlayer packet, EntityPlayerMP player, VectorTransformer transformer) {
        if (packet.func_149466_j()) {
            double x = packet.getPositionX();
            double z = packet.getPositionZ();
            float offset = player.getDefaultEyeHeight();

            if (transformer.getAxisY() == ForgeDirection.EAST) {
                x -= offset;
            } else if (transformer.getAxisY() == ForgeDirection.WEST) {
                x += offset;
            } else if (transformer.getAxisY() == ForgeDirection.NORTH) {
                z += offset;
            } else if (transformer.getAxisY() == ForgeDirection.SOUTH) {
                z -= offset;
            }

            if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                packet = new C03PacketPlayer.C04PacketPlayerPosition(x, packet.getPositionY(), packet.getStance(), z, packet.func_149465_i());
            } else if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                packet = new C03PacketPlayer.C06PacketPlayerPosLook(x, packet.getPositionY(), packet.getStance(), z, packet.getYaw(), packet.getPitch(), packet.func_149465_i());
            }
        }


        return packet;
    }

    public static AxisAlignedBB setBounds(IClimbingShoesWearer wearer, Entity entity, AxisAlignedBB this0) {
        if (wearer.areClimbingShoesEquipped())
            ClimbingHelper.untransformBB(entity.boundingBox, wearer.getTransformer());

        double halfWidth = entity.width / 2.0;
        double minYOffset = entity.yOffset2 - entity.yOffset;
        double maxYOffset = (entity.yOffset2 + entity.height) - entity.yOffset;

        double minX = entity.posX + wearer.getTransformer().unGetX(-halfWidth, minYOffset, -halfWidth);
        double minY = entity.posY + wearer.getTransformer().unGetY(0, minYOffset, 0);
        double minZ = entity.posZ + wearer.getTransformer().unGetZ(-halfWidth, minYOffset, -halfWidth);
        double maxX = entity.posX + wearer.getTransformer().unGetX(halfWidth, maxYOffset, halfWidth);
        double maxY = entity.posY + Math.abs(wearer.getTransformer().unGetY(entity.width, maxYOffset, entity.width));
        double maxZ = entity.posZ + wearer.getTransformer().unGetZ(halfWidth, maxYOffset, halfWidth);

        AxisAlignedBB bb = this0.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
        ClimbingHelper.normalizeBB(bb);

        if (wearer.areClimbingShoesEquipped())
            ClimbingHelper.transformBB(entity.boundingBox, wearer.getTransformer());

        return bb;
    }

    public static void transformEntity(Entity entity, VectorTransformer transformer) {
        double prevPosX = entity.prevPosX;
        double prevPosY = entity.prevPosY;
        double prevPosZ = entity.prevPosZ;
        double posX = entity.posX;
        double posY = entity.posY;
        double posZ = entity.posZ;
        double motionX = entity.motionX;
        double motionY = entity.motionY;
        double motionZ = entity.motionZ;

        entity.prevPosX = transformer.getX(prevPosX, prevPosY, prevPosZ);
        entity.prevPosY = transformer.getY(prevPosX, prevPosY, prevPosZ);
        entity.prevPosZ = transformer.getZ(prevPosX, prevPosY, prevPosZ);
        entity.posX = transformer.getX(posX, posY, posZ);
        entity.posY = transformer.getY(posX, posY, posZ);
        entity.posZ = transformer.getZ(posX, posY, posZ);
        entity.motionX = transformer.getX(motionX, motionY, motionZ);
        entity.motionY = transformer.getY(motionX, motionY, motionZ);
        entity.motionZ = transformer.getZ(motionX, motionY, motionZ);
        transformBB(entity.boundingBox, transformer);

        ((IClimbingWorld)entity.worldObj).setWorldTransformer(transformer);
    }

    public static void untransformEntity(Entity entity, VectorTransformer transformer) {
        double prevPosX = entity.prevPosX;
        double prevPosY = entity.prevPosY;
        double prevPosZ = entity.prevPosZ;
        double posX = entity.posX;
        double posY = entity.posY;
        double posZ = entity.posZ;
        double motionX = entity.motionX;
        double motionY = entity.motionY;
        double motionZ = entity.motionZ;

        entity.prevPosX = transformer.unGetX(prevPosX, prevPosY, prevPosZ);
        entity.prevPosY = transformer.unGetY(prevPosX, prevPosY, prevPosZ);
        entity.prevPosZ = transformer.unGetZ(prevPosX, prevPosY, prevPosZ);
        entity.posX = transformer.unGetX(posX, posY, posZ);
        entity.posY = transformer.unGetY(posX, posY, posZ);
        entity.posZ = transformer.unGetZ(posX, posY, posZ);
        entity.motionX = transformer.unGetX(motionX, motionY, motionZ);
        entity.motionY = transformer.unGetY(motionX, motionY, motionZ);
        entity.motionZ = transformer.unGetZ(motionX, motionY, motionZ);
        untransformBB(entity.boundingBox, transformer);
        ((IClimbingWorld)entity.worldObj).setWorldTransformer(null);
    }

    public static void rotateEntityBBFromTo(Entity entity,ForgeDirection from, ForgeDirection to) {
        VectorTransformer fromTransform = new VectorTransformer(from);
        VectorTransformer toTransform = new VectorTransformer(to);
        transformEntity(entity, fromTransform);
        entity.motionY = 0;
        untransformEntity(entity, fromTransform);

        normalizeBB(entity.boundingBox);

        double diffX = entity.boundingBox.maxX - entity.boundingBox.minX;
        double diffY = entity.boundingBox.maxY - entity.boundingBox.minY;
        double diffZ = entity.boundingBox.maxZ - entity.boundingBox.minZ;

        double interDiffX = fromTransform.unGetX(diffX, diffY, diffZ);
        double interDiffY = fromTransform.unGetY(diffX, diffY, diffZ);
        double interDiffZ = fromTransform.unGetZ(diffX, diffY, diffZ);

        double realDiffX = Math.abs(toTransform.getX(interDiffX, interDiffY, interDiffZ));
        double realDiffY = Math.abs(toTransform.getY(interDiffX, interDiffY, interDiffZ));
        double realDiffZ = Math.abs(toTransform.getZ(interDiffX, interDiffY, interDiffZ));

        if (to == ForgeDirection.EAST || from == ForgeDirection.EAST)
            entity.boundingBox.minX = entity.boundingBox.maxX - realDiffX;
        else
            entity.boundingBox.maxX = entity.boundingBox.minX + realDiffX;

        if (to == ForgeDirection.UP || from == ForgeDirection.UP)
            entity.boundingBox.minY = entity.boundingBox.maxY - realDiffY;
        else
            entity.boundingBox.maxY = entity.boundingBox.minY + realDiffY;

        if (to == ForgeDirection.SOUTH || from == ForgeDirection.SOUTH)
            entity.boundingBox.minZ = entity.boundingBox.maxZ - realDiffZ;
        else
            entity.boundingBox.maxZ = entity.boundingBox.minZ + realDiffZ;

        float halfWidth = entity.width/2.0f;
        float eyeHeight = entity.yOffset;

        if (to == ForgeDirection.EAST)
            entity.posX = entity.boundingBox.maxX - eyeHeight;
        else if (to == ForgeDirection.WEST)
            entity.posX = entity.boundingBox.minX + eyeHeight;
        else
            entity.posX = entity.boundingBox.minX + halfWidth;

        if (to == ForgeDirection.SOUTH)
            entity.posZ = entity.boundingBox.maxZ - eyeHeight;
        else if (to == ForgeDirection.NORTH)
            entity.posZ = entity.boundingBox.minZ + eyeHeight;
        else
            entity.posZ = entity.boundingBox.minZ + halfWidth;

        if (to == ForgeDirection.UP)
            entity.posY = entity.boundingBox.maxY - eyeHeight;
        else if (to == ForgeDirection.DOWN)
            entity.posY = entity.boundingBox.minY + eyeHeight;
        else
            entity.posY = entity.boundingBox.minY + halfWidth;

        if ((to == ForgeDirection.UP && (from == ForgeDirection.NORTH || from == ForgeDirection.SOUTH)) ||
                (from == ForgeDirection.UP && (to == ForgeDirection.NORTH || to == ForgeDirection.SOUTH)))
            entity.rotationYaw += 180;
    }

    public static void transformBB(AxisAlignedBB box, VectorTransformer transformer) {
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        box.minX = transformer.getX(minX, minY, minZ);
        box.minY = transformer.getY(minX, minY, minZ);
        box.minZ = transformer.getZ(minX, minY, minZ);
        box.maxX = transformer.getX(maxX, maxY, maxZ);
        box.maxY = transformer.getY(maxX, maxY, maxZ);
        box.maxZ = transformer.getZ(maxX, maxY, maxZ);

        normalizeBB(box);
    }

    public static void untransformBB(AxisAlignedBB box, VectorTransformer transformer) {
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        box.minX = transformer.unGetX(minX, minY, minZ);
        box.minY = transformer.unGetY(minX, minY, minZ);
        box.minZ = transformer.unGetZ(minX, minY, minZ);
        box.maxX = transformer.unGetX(maxX, maxY, maxZ);
        box.maxY = transformer.unGetY(maxX, maxY, maxZ);
        box.maxZ = transformer.unGetZ(maxX, maxY, maxZ);

        normalizeBB(box);
    }

    public static void normalizeBB(AxisAlignedBB box) {
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        box.minX = Math.min(minX, maxX);
        box.maxX = Math.max(minX, maxX);
        box.minY = Math.min(minY, maxY);
        box.maxY = Math.max(minY, maxY);
        box.minZ = Math.min(minZ, maxZ);
        box.maxZ = Math.max(minZ, maxZ);
    }

    public static void rotateLookVector(Vec3 vector, VectorTransformer transformer) {
        if (transformer.getAxisY() == ForgeDirection.DOWN)
            vector.rotateAroundZ((float)Math.PI);
        else if (transformer.getAxisY() == ForgeDirection.SOUTH)
            vector.rotateAroundX((float)(-Math.PI/2.0));
        else if (transformer.getAxisY() == ForgeDirection.NORTH)
            vector.rotateAroundX((float)(Math.PI/2.0));
        else if (transformer.getAxisY() == ForgeDirection.WEST)
            vector.rotateAroundZ((float)(-Math.PI/2.0));
        else if (transformer.getAxisY() == ForgeDirection.EAST)
            vector.rotateAroundZ((float)(Math.PI/2.0));
    }
}

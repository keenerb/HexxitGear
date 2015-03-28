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
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class ClimbingHelper {
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
        double minX = entity.boundingBox.minX;
        double minY = entity.boundingBox.minY;
        double minZ = entity.boundingBox.minZ;
        double maxX = entity.boundingBox.maxX;
        double maxY = entity.boundingBox.maxY;
        double maxZ = entity.boundingBox.maxZ;

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
        double minX = entity.boundingBox.minX;
        double minY = entity.boundingBox.minY;
        double minZ = entity.boundingBox.minZ;
        double maxX = entity.boundingBox.maxX;
        double maxY = entity.boundingBox.maxY;
        double maxZ = entity.boundingBox.maxZ;

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

    public static void rotateEntityBB(Entity entity, VectorTransformer transformer) {
        double boxBottom = entity.boundingBox.minY;
        double minX = entity.boundingBox.minX - entity.posX;
        double minY = entity.boundingBox.minY - boxBottom;
        double minZ = entity.boundingBox.minZ - entity.posZ;
        double maxX = entity.boundingBox.maxX - entity.posX;
        double maxY = entity.boundingBox.maxY - boxBottom;
        double maxZ = entity.boundingBox.maxZ - entity.posZ;

        entity.boundingBox.minX = entity.posX + transformer.getX(minX, minY, minZ);
        entity.boundingBox.minY = boxBottom;
        entity.boundingBox.minZ = entity.posZ + transformer.getZ(minX, minY, minZ);
        entity.boundingBox.maxX = entity.posX + transformer.getX(maxX, maxY, maxZ);
        entity.boundingBox.maxY = boxBottom + Math.abs(transformer.getY(maxX, maxY, maxZ)) + Math.abs(transformer.getY(minX, minY, minZ));
        entity.boundingBox.maxZ = entity.posZ + transformer.getZ(maxX, maxY, maxZ);

        normalizeBB(entity.boundingBox);
    }

    public static void unrotateEntityBB(Entity entity, VectorTransformer transformer) {
        double boxBottom = entity.boundingBox.minY;
        double minX = entity.boundingBox.minX - entity.posX;
        double minY = entity.boundingBox.minY - boxBottom;
        double minZ = entity.boundingBox.minZ - entity.posZ;
        double maxX = entity.boundingBox.maxX - entity.posX;
        double maxY = entity.boundingBox.maxY - boxBottom;
        double maxZ = entity.boundingBox.maxZ - entity.posZ;

        entity.boundingBox.minX = entity.posX + transformer.unGetX(minX, minY, minZ);
        entity.boundingBox.minY = boxBottom;
        entity.boundingBox.minZ = entity.posZ + transformer.unGetZ(minX, minY, minZ);
        entity.boundingBox.maxX = entity.posX + transformer.unGetX(maxX, maxY, maxZ);
        entity.boundingBox.maxY = boxBottom + Math.abs(transformer.unGetY(maxX, maxY, maxZ)) + Math.abs(transformer.unGetY(minX, minY, minZ));
        entity.boundingBox.maxZ = entity.posZ + transformer.unGetZ(maxX, maxY, maxZ);

        normalizeBB(entity.boundingBox);
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
}

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

package sct.hexxitgear.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sct.hexxitgear.mixinsupport.climbing.ClimbingHelper;
import sct.hexxitgear.mixinsupport.climbing.ClimbingWorldHelper;
import sct.hexxitgear.mixinsupport.climbing.IClimbingWorld;
import sct.hexxitgear.mixinsupport.climbing.VectorTransformer;

import java.util.ArrayList;
import java.util.List;

@Mixin(World.class)
public abstract class ClimbingWorldMixin implements IClimbingWorld {
    private VectorTransformer transformer;

    @Override
    public void setWorldTransformer(VectorTransformer transformer) {
        this.transformer = transformer;
    }
    @Override
    public VectorTransformer getWorldTransformer() { return this.transformer; }

    @Shadow
    public abstract Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_);

    @Shadow
    @SuppressWarnings("rawtypes")
    private ArrayList collidingBoundingBoxes;
    @Shadow
    public boolean captureBlockSnapshots;
    @Shadow
    public boolean isRemote;
    @Shadow
    public ArrayList<net.minecraftforge.common.util.BlockSnapshot> capturedBlockSnapshots;
    @Shadow
    public final Profiler theProfiler = null;
    @Shadow
    public abstract boolean updateAllLightTypes(int p_147451_1_, int p_147451_2_, int p_147451_3_);
    @Shadow
    public abstract void markAndNotifyBlock(int x, int y, int z, Chunk chunk, Block oldBlock, Block newBlock, int flag);

    @Shadow
    public abstract boolean blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_);

    @Shadow
    public abstract Block getBlock(int x, int y, int z);

    @Shadow
    @SuppressWarnings("rawtypes")
    public abstract List getEntitiesWithinAABBExcludingEntity(Entity p_72839_1_, AxisAlignedBB p_72839_2_);

    @Shadow
    protected abstract boolean chunkExists(int p_72916_1_, int p_72916_2_);

    @Inject(method="blockExists", at=@At("HEAD"), cancellable = true)
    private void transformBlockExists(int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
        if (transformer == null)
            return;
        int tempX = x, tempY = y, tempZ = z;
        x = (int)transformer.unGetX(tempX, tempY, tempZ);
        y = (int)transformer.unGetY(tempX, tempY, tempZ);
        z = (int)transformer.unGetZ(tempX, tempY, tempZ);

        info.setReturnValue(y >= 0 && y < 256 ? this.chunkExists(x >> 4, z >> 4) : false);
        info.cancel();
    }

    @Inject(method="setBlock", at=@At("HEAD"), cancellable = true)
    private void transformSetBlock(int x, int y, int z, Block block, int metadata, int flags, CallbackInfoReturnable<Boolean> info) {
        if (transformer == null)
            return;

        int tempX = x, tempY = y, tempZ = z;
        x = (int)transformer.unGetX(tempX, tempY, tempZ);
        y = (int)transformer.unGetY(tempX, tempY, tempZ);
        z = (int)transformer.unGetZ(tempX, tempY, tempZ);

        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (y < 0) {
                info.setReturnValue(false);
                info.cancel();
                return;
            } else if (y >= 256) {
                info.setReturnValue(false);
                info.cancel();
                return;
            } else {
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                Block block1 = null;
                net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;

                if ((flags & 1) != 0) {
                    block1 = chunk.getBlock(x & 15, y, z & 15);
                }

                if (this.captureBlockSnapshots && !this.isRemote) {
                    blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot((World)(Object)this, x, y, z, flags);
                    this.capturedBlockSnapshots.add(blockSnapshot);
                }

                boolean flag = chunk.setBlockIDWithMetadata(x & 15, y, z & 15, block, metadata);

                if (!flag && blockSnapshot != null) {
                    this.capturedBlockSnapshots.remove(blockSnapshot);
                    blockSnapshot = null;
                }

                this.theProfiler.startSection("checkLight");
                this.updateAllLightTypes(x, y, z);
                this.theProfiler.endSection();

                if (flag && blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    // Modularize client and physic updates
                    this.markAndNotifyBlock(x, y, z, chunk, block1, block, flags);
                }

                info.setReturnValue(flag);
                info.cancel();
                return;
            }
        } else {
            info.setReturnValue(false);
            info.cancel();
            return;
        }
    }

    @Inject(method="getBlock", at=@At("HEAD"), cancellable = true)
    private void transformGetBlock(int x, int y, int z, CallbackInfoReturnable<Block> info) {
        if (transformer == null)
            return;
        int tmpX = x;
        int tmpY = y;
        int tmpZ = z;
        x = (int)transformer.unGetX(tmpX, tmpY, tmpZ);
        y = (int)transformer.unGetY(tmpX, tmpY, tmpZ);
        z = (int)transformer.unGetZ(tmpX, tmpY, tmpZ);

        info.setReturnValue(ClimbingWorldHelper.getBlock((World)(Object)this, x, y, z));
        info.cancel();
    }

    @Inject(method="getBlockMetadata", at=@At("HEAD"), cancellable = true)
    private void transformGetBlockMetadata(int x, int y, int z, CallbackInfoReturnable<Integer> info) {
        if (transformer == null)
            return;
        int tmpX = x;
        int tmpY = y;
        int tmpZ = z;
        x = (int)transformer.unGetX(tmpX, tmpY, tmpZ);
        y = (int)transformer.unGetY(tmpX, tmpY, tmpZ);
        z = (int)transformer.unGetZ(tmpX, tmpY, tmpZ);

        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000)
        {
            if (y < 0)
            {
                info.setReturnValue(0);
            }
            else if (y >= 256)
            {
                info.setReturnValue(0);
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                x &= 15;
                z &= 15;
                info.setReturnValue(chunk.getBlockMetadata(x, y, z));
            }
        }
        else
        {
            info.setReturnValue(0);
        }

        info.cancel();
    }

    @Inject(method="getCollidingBoundingBoxes", at=@At("HEAD"), cancellable = true)
    @SuppressWarnings({"rawtypes","unchecked"})
    private void transformGetCollidingBoundingBoxes(Entity entity, AxisAlignedBB box, CallbackInfoReturnable<List> info) {
        if (this.transformer == null)
            return;

        VectorTransformer transformer = this.transformer;

        ClimbingHelper.untransformEntity(entity, transformer);
        ClimbingHelper.untransformBB(box, transformer);

        ClimbingWorldHelper.processCollision(this.collidingBoundingBoxes, entity, box, (World)(Object)this);

        ClimbingHelper.transformEntity(entity, transformer);

        List outBoxes = new ArrayList(this.collidingBoundingBoxes.size());

        for(Object out : this.collidingBoundingBoxes) {
            AxisAlignedBB outBox = (AxisAlignedBB)out;
            AxisAlignedBB newBox = AxisAlignedBB.getBoundingBox(outBox.minX, outBox.minY, outBox.minZ, outBox.maxX, outBox.maxY, outBox.maxZ);
            ClimbingHelper.transformBB(newBox, transformer);
            outBoxes.add(newBox);
        }

        info.setReturnValue(outBoxes);
        info.cancel();
    }

    @Inject(method="handleMaterialAcceleration", at=@At("HEAD"), cancellable = true)
    private void transformHandleMaterialAcceleration(AxisAlignedBB box, Material material, Entity entity, CallbackInfoReturnable<Boolean> info) {
        VectorTransformer transformer = getWorldTransformer();
        if (transformer != null) {
            ClimbingHelper.untransformEntity(entity, transformer);
            if (box != entity.boundingBox)
                ClimbingHelper.untransformBB(box, transformer);
        }

        info.setReturnValue(ClimbingWorldHelper.handleMaterialAcceleration((World)(Object)this, box, material, entity));

        if (transformer != null) {
            ClimbingHelper.transformEntity(entity, transformer);
            if (box != entity.boundingBox)
                ClimbingHelper.transformBB(box, transformer);
        }

        info.cancel();
    }
}

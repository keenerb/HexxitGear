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

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sct.hexxitgear.item.climbing.ClimbingHelper;
import sct.hexxitgear.item.climbing.IClimbingWorld;
import sct.hexxitgear.item.climbing.VectorTransformer;

import java.util.ArrayList;
import java.util.List;

@Mixin(World.class)
public abstract class ClimbingWorldMixin implements IClimbingWorld {
    private VectorTransformer transformer;

    @Override
    public void setWorldTransformer(VectorTransformer transformer) {
        this.transformer = transformer;
    }

    @Shadow
    public Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_) { return null; }

    @Shadow
    private ArrayList collidingBoundingBoxes;

    @Shadow
    public boolean blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_) {return true;}

    @Shadow
    public Block getBlock(int x, int y, int z) { return null; }

    @Shadow
    public List getEntitiesWithinAABBExcludingEntity(Entity p_72839_1_, AxisAlignedBB p_72839_2_) { return null; }

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

        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256)
        {
            Chunk chunk = null;

            try
            {
                chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                info.setReturnValue(chunk.getBlock(x & 15, y, z & 15));
                info.cancel();
                return;
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception getting block type in world");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
                crashreportcategory.addCrashSection("Found chunk", Boolean.valueOf(chunk == null));
                crashreportcategory.addCrashSection("Location", CrashReportCategory.getLocationInfo(x, y, z));
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            info.setReturnValue(Blocks.air);
            info.cancel();
            return;
        }
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
    private void transformGetCollidingBoundingBoxes(Entity entity, AxisAlignedBB box, CallbackInfoReturnable<List> info) {
        if (this.transformer == null)
            return;

        VectorTransformer transformer = this.transformer;

        ClimbingHelper.untransformEntity(entity, transformer);
        ClimbingHelper.untransformBB(box, transformer);

        this.collidingBoundingBoxes.clear();
        int i = MathHelper.floor_double(box.minX);
        int j = MathHelper.floor_double(box.maxX + 1.0D);
        int k = MathHelper.floor_double(box.minY);
        int l = MathHelper.floor_double(box.maxY + 1.0D);
        int i1 = MathHelper.floor_double(box.minZ);
        int j1 = MathHelper.floor_double(box.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (this.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        Block block;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000)
                        {
                            block = this.getBlock(k1, i2, l1);
                        }
                        else
                        {
                            block = Blocks.stone;
                        }

                        block.addCollisionBoxesToList(((World)((Object)this)), k1, i2, l1, box, this.collidingBoundingBoxes, entity);
                    }
                }
            }
        }

        double d0 = 0.25D;
        List list = this.getEntitiesWithinAABBExcludingEntity(entity, box.expand(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getBoundingBox();

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(box))
            {
                this.collidingBoundingBoxes.add(axisalignedbb1);
            }

            axisalignedbb1 = entity.getCollisionBox((Entity)list.get(j2));

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(box))
            {
                this.collidingBoundingBoxes.add(axisalignedbb1);
            }
        }

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
}

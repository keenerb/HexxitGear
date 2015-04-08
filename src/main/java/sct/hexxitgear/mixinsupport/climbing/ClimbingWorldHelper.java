package sct.hexxitgear.mixinsupport.climbing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class ClimbingWorldHelper {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void processCollision(List collisionList, Entity entity, AxisAlignedBB box, World world) {
        collisionList.clear();
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
                if (world.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        Block block;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000)
                        {
                            block = world.getBlock(k1, i2, l1);
                        }
                        else
                        {
                            block = Blocks.stone;
                        }

                        block.addCollisionBoxesToList(world, k1, i2, l1, box, collisionList, entity);
                    }
                }
            }
        }

        double d0 = 0.25D;
        List list = world.getEntitiesWithinAABBExcludingEntity(entity, box.expand(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getBoundingBox();

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(box))
            {
                collisionList.add(axisalignedbb1);
            }

            axisalignedbb1 = entity.getCollisionBox((Entity)list.get(j2));

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(box))
            {
                collisionList.add(axisalignedbb1);
            }
        }
    }

    public static Block getBlock(World world, int x, int y, int z) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256)
        {
            Chunk chunk = null;

            try
            {
                chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                return chunk.getBlock(x & 15, y, z & 15);
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
            return Blocks.air;
        }
    }

    public static boolean handleMaterialAcceleration(World world, AxisAlignedBB box, Material material, Entity entity) {
        int i = MathHelper.floor_double(box.minX);
        int j = MathHelper.floor_double(box.maxX + 1.0D);
        int k = MathHelper.floor_double(box.minY);
        int l = MathHelper.floor_double(box.maxY + 1.0D);
        int i1 = MathHelper.floor_double(box.minZ);
        int j1 = MathHelper.floor_double(box.maxZ + 1.0D);

        if (!world.checkChunksExist(i, k, i1, j, l, j1))
        {
            return false;
        }
        else
        {
            boolean flag = false;
            Vec3 vec3 = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        Block block = world.getBlock(k1, l1, i2);

                        if (block.getMaterial() == material)
                        {
                            double d0 = (double)((float)(l1 + 1) - BlockLiquid.getLiquidHeightPercent(world.getBlockMetadata(k1, l1, i2)));

                            if ((double)l >= d0)
                            {
                                flag = true;
                                block.modifyEntityVelocity(world, k1, l1, i2, entity, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.lengthVector() > 0.0D && entity.isPushedByWater())
            {
                vec3 = vec3.normalize();
                double d1 = 0.014D;
                entity.motionX += vec3.xCoord * d1;
                entity.motionY += vec3.yCoord * d1;
                entity.motionZ += vec3.zCoord * d1;
            }

            return flag;
        }
    }
}

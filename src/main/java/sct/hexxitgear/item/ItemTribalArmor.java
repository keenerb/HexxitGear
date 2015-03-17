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

package sct.hexxitgear.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.core.ArmorSet;
import sct.hexxitgear.model.ModelSkullHelmet;
import sct.hexxitgear.util.FormatCodes;

import java.util.List;

public class ItemTribalArmor extends ItemHexxitArmor {

    public ItemTribalArmor(int renderIndex, int slot) {
        super(ArmorMaterial.DIAMOND, renderIndex, slot);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, java.lang.String type) {
        if (slot == 0)
            return "hexxitgear:textures/maps/SkullHelmet.png";

        if (stack.getItem() == HexxitGear.tribalLeggings)
            return "hexxitgear:textures/armor/tribal2.png";

        return "hexxitgear:textures/armor/tribal.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if (armorSlot == 0)
            return new ModelSkullHelmet();
        return null;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.tribal"));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {
        super.onArmorTick(world, player, itemStack);

        if (itemStack.getItem() == HexxitGear.tribalShoes) {
            shoesWaterWalkCheck(world, player);
        }
    }

    private void shoesWaterWalkCheck(World world, EntityPlayer player) {
        int x = (int) Math.floor(player.posX);
        int y = (int) (player.posY - player.getYOffset());
        int z = (int) Math.floor(player.posZ);

        Block floor = world.getBlock(x, y - 1, z);
        Block playerLegs = world.getBlock(x, y, z);

        if (!playerLegs.isAir(world, x, y, z)) {
            return;
        }

        boolean hasFullSet = (ArmorSet.getPlayerArmorSet(player.getDisplayName()) != null);
        boolean floorIsGood = false;

        if (floor.getMaterial() == Material.water)
            floorIsGood = true;
        else if (hasFullSet && floor.getMaterial() == Material.lava)
            floorIsGood = true;

        if (floorIsGood && !player.isSneaking() && (hasFullSet || player.isSprinting())) {
            player.motionY = 0.0d;
            player.fallDistance = 0.0f;
            player.onGround = true;
        }
    }
}

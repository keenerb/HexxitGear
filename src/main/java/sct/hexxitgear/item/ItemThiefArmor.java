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
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.model.ModelHoodHelmet;
import sct.hexxitgear.util.FormatCodes;

import java.util.List;

public class ItemThiefArmor extends ItemHexxitArmor {

    public ItemThiefArmor(int renderIndex, int slot) {
        super(ArmorMaterial.DIAMOND, renderIndex, slot);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, java.lang.String type) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isPotionActive(Potion.invisibility))
                return "hexxitgear:textures/armor/invisible.png";
        }

        // If the helmet slot, return helmet texture map
        if (slot == 0)
            return "hexxitgear:textures/maps/HoodHelmet.png";

        if (stack.getItem() == HexxitGear.thiefLeggings)
            return "hexxitgear:textures/armor/thief2.png";

        return "hexxitgear:textures/armor/thief.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if (armorSlot == 0)
            return new ModelHoodHelmet();
        return null;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.thief"));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {
        super.onArmorTick(world, player, itemStack);

        if (itemStack.getItem() == HexxitGear.thiefBoots) {
            shoesClimbCheck(world, player);
        }
    }

    private void shoesClimbCheck(World world, EntityPlayer player) {
        ForgeDirection floor = ForgeDirection.VALID_DIRECTIONS[player.getEntityData().getByte("BootFloor")];

        ForgeDirection posY = ForgeDirection.UP;
        ForgeDirection posX = ForgeDirection.EAST;
        ForgeDirection posZ = ForgeDirection.SOUTH;

        if (floor == ForgeDirection.UP) {
            posY = ForgeDirection.DOWN;
            posX = ForgeDirection.WEST;
            posZ = ForgeDirection.NORTH;
        } else if (floor != ForgeDirection.DOWN) {
            posY = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[floor.ordinal()]];
            switch (floor) {
                case WEST:
                    posX = posX.getRotation(ForgeDirection.SOUTH);
                    break;
                case EAST:
                    posX = posX.getRotation(ForgeDirection.NORTH);
                    break;
                case NORTH:
                    posZ = posZ.getRotation(ForgeDirection.EAST);
                    break;
                case SOUTH:
                    posZ = posZ.getRotation(ForgeDirection.WEST);
                    break;
            }
        }

        double motionX = player.motionX;
        double motionY = player.motionY;
        double motionZ = player.motionZ;

        player.motionX = posX.offsetX * motionX + posX.offsetY * motionY + posX.offsetZ * motionZ;
        player.motionY = posY.offsetX * motionX + posY.offsetY * motionY + posY.offsetZ * motionZ;
        player.motionZ = posZ.offsetX * motionX + posZ.offsetY * motionY + posZ.offsetZ * motionZ;
    }
}

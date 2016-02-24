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
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.model.ModelHoodHelmet;
import sct.hexxitgear.model.ModelSageHood;
import sct.hexxitgear.util.FormatCodes;

import java.util.List;

public class ItemMagicianArmor extends ItemHexxitArmor {

    public ItemMagicianArmor(int renderIndex, int slot) {
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
            return "hexxitgear:textures/maps/SageHood.png";

        if (stack.getItem() == HexxitGear.magicLeggings)
            return "hexxitgear:textures/armor/sage2.png";

        return "hexxitgear:textures/armor/sage.png";
    }

    @SideOnly(Side.CLIENT)
    private static ModelSageHood hood;

    @SideOnly(Side.CLIENT)
    protected ModelSageHood getHoodModel() {
        if (hood == null)
            hood = new ModelSageHood();
        return hood;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if (armorSlot == 0) {
            ModelBiped retVal = getHoodModel();
            retVal.isSneak = entityLiving.isSneaking();
            return retVal;
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.magician"));
    }
}

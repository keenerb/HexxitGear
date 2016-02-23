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
import net.minecraft.util.StatCollector;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.model.ModelDualLayerArmor;
import sct.hexxitgear.model.ModelScaleHelmet;
import sct.hexxitgear.util.FormatCodes;

import java.util.List;

public class ItemScaleArmor extends ItemHexxitArmor {

    public ItemScaleArmor(int renderIndex, int slot) {
        super(ArmorMaterial.DIAMOND, renderIndex, slot);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getHoodTexture() {
        return "hexxitgear:textures/maps/ScaleHelmet.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getBodyTexture() {
        return "hexxitgear:textures/armor/scale2.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getOverlayTexture() {
        return "hexxitgear:textures/armor/scale.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected ModelDualLayerArmor getBodyModel(int slot) {
        switch(slot) {
            case 2:
                return leggings;
            case 1:
                return chest;
            case 3:
                return feet;
            default:
                return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private static ModelScaleHelmet scaleHelmet = new ModelScaleHelmet();
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor chest = new ModelDualLayerArmor(1.0f);
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor leggings = new ModelDualLayerArmor(0.5f);
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor feet = new ModelDualLayerArmor(0.5f);

    @SideOnly(Side.CLIENT)
    @Override
    protected ModelBiped getHeadModel() {
        return scaleHelmet;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.scale"));
    }
}

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
import org.lwjgl.input.Keyboard;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.core.ArmorSet;
import sct.hexxitgear.model.ModelDualLayerArmor;
import sct.hexxitgear.model.ModelSkullHelmet;
import sct.hexxitgear.util.FormatCodes;

import java.util.List;

public class ItemTribalArmor extends ItemHexxitArmor {

    public ItemTribalArmor(int renderIndex, int slot) {
        super(ArmorMaterial.DIAMOND, renderIndex, slot);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getHoodTexture() {
        return "hexxitgear:textures/maps/SkullHelmet.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getBodyTexture() {
        return "hexxitgear:textures/armor/tribal2.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getOverlayTexture() {
        return "hexxitgear:textures/armor/tribal.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected ModelDualLayerArmor getBodyModel(int slot) {
        switch (slot) {
            case 1: return chest;
            case 2: return leggings;
            case 3: return feet;
            default: return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private static ModelSkullHelmet skullHelmet = new ModelSkullHelmet();
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor chest = new ModelDualLayerArmor(1.0f);
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor leggings = new ModelDualLayerArmor(0.5f);
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor feet = new ModelDualLayerArmor(0.5f);

    @SideOnly(Side.CLIENT)
    @Override
    protected ModelSkullHelmet getHeadModel() {
        return skullHelmet;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Bold.format + FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.tribal") + FormatCodes.Reset.format);

        if (this.armorType == 3) {
            if (par4 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                boolean isEmpowered = (par2EntityPlayer.getCurrentArmor(0) != null && par2EntityPlayer.getCurrentArmor(0).getItem() == this && ArmorSet.getPlayerArmorSet(par2EntityPlayer.getDisplayName()) != null);
                String powerName = isEmpowered ? "gui.hexxitgear.boots.tribal.empowered" : "gui.hexxitgear.boots.tribal";
                infoList.add(FormatCodes.Bold.format + FormatCodes.Yellow.format + StatCollector.translateToLocal(powerName));
                HexxitGear.translateAndAdd(powerName, infoList);
            } else {
                infoList.add(FormatCodes.Indigo.format + FormatCodes.Italic.format + StatCollector.translateToLocal("gui.hexxitgear.shiftprompt") + FormatCodes.Reset.format);
            }
        }
    }

}

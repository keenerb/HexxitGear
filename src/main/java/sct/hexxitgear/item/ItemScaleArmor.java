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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.core.ArmorSet;
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Bold.format + FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.scale") + FormatCodes.Reset.format);

        if (this.armorType == 3) {
            if (par4 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                boolean isEmpowered = (par2EntityPlayer.getCurrentArmor(0) != null && par2EntityPlayer.getCurrentArmor(0).getItem() == this && ArmorSet.getPlayerArmorSet(par2EntityPlayer.getDisplayName()) != null);
                String powerName = isEmpowered ? "gui.hexxitgear.boots.scale.empowered" : "gui.hexxitgear.boots.scale";
                infoList.add(FormatCodes.Bold.format + FormatCodes.Yellow.format + StatCollector.translateToLocal(powerName));
                HexxitGear.translateAndAdd(powerName, infoList);
            } else {
                infoList.add(FormatCodes.Indigo.format + FormatCodes.Italic.format + StatCollector.translateToLocal("gui.hexxitgear.shiftprompt") + FormatCodes.Reset.format);
            }
        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);

        if (itemStack.getItem() == HexxitGear.scaleBoots) {
            shoesChargeCheck(player);
        }
    }

    private void shoesChargeCheck(EntityPlayer player) {
        if (player.isSprinting()) {
            if (player.worldObj.isRemote)
                return;

            EntityLivingBase target = null;
            for (Object entityObj : player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox)) {
                if (entityObj instanceof EntityLivingBase) {
                    target = (EntityLivingBase) entityObj;
                    break;
                }
            }

            if (target == null)
                return;

            Vec3 targetPosition = target.getPosition(1.0f);

            AxisAlignedBB impactBox = player.boundingBox;
            boolean hasFullSet = (ArmorSet.getPlayerArmorSet(player.getDisplayName()) != null);

            if (hasFullSet)
                impactBox.expand(2.0, 0, 2.0);
            else
                impactBox.expand(0.5, 0, 0.5);

            for (Object entityObj : player.worldObj.getEntitiesWithinAABBExcludingEntity(player, impactBox)) {
                if (entityObj instanceof EntityLivingBase) {
                    impactEntity(player, (EntityLivingBase) entityObj);
                }
            }

            player.motionX *= 0.6D;
            player.motionZ *= 0.6D;
            player.setSprinting(false);
            if (!hasFullSet)
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 60, 3));
        }
    }

    private void impactEntity(EntityPlayer player, EntityLivingBase target) {
        target.attackEntityFrom(DamageSource.causePlayerDamage(player), 5.0f);
        target.addVelocity((double) (-MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * 0.5F), 0.1D, (double) (MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * 0.5F));
    }
}

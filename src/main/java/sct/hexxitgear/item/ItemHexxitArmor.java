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
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import sct.hexxitgear.core.ArmorSet;
import sct.hexxitgear.core.ability.AbilityHandler;
import sct.hexxitgear.core.buff.IBuffHandler;
import sct.hexxitgear.gui.HGCreativeTab;
import sct.hexxitgear.model.ModelDualLayerArmor;
import sct.hexxitgear.util.FormatCodes;

public abstract class ItemHexxitArmor extends ItemArmor implements ISpecialArmor {

    public ItemHexxitArmor(ArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
        super(par2EnumArmorMaterial, par3, par4);
        setCreativeTab(HGCreativeTab.tab);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ArmorProperties(1, damageReduceAmount / 22D, armor.getMaxDurability() + 1);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return damageReduceAmount;
    }

    public IBuffHandler getBuffHandler() {
        return null;
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    @Override
    public int getColor(ItemStack stack) {
        if (stack == null || stack.getItem() == null || ((ItemHexxitArmor)stack.getItem()).armorType == 0)
            return -1;
        return 16777215;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, java.lang.String type) {
        // If the helmet slot, return helmet texture map
        if (slot == 0)
            return getHoodTexture();

        if (type != null && type.equals("overlay")) {
            ModelDualLayerArmor armor = getBodyModel(slot);
            if (armor != null)
                armor.prepareForOverlay();
            return getOverlayTexture();
        }

        ModelDualLayerArmor otherArmor = getBodyModel(slot);
        if (otherArmor != null)
            otherArmor.prepareForNormal();
        return getBodyTexture();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        ModelBiped biped = null;
        if (armorSlot == 0) {
            biped = getHeadModel();
        } else {
            biped = getBodyModel(armorSlot);
        }

        if (biped == null)
            return null;

        biped.isSneak = entityLiving.isSneaking();
        biped.bipedHead.showModel = armorSlot == 0;
        biped.bipedHeadwear.showModel = armorSlot == 0;
        biped.bipedBody.showModel = armorSlot == 1 || armorSlot == 2;
        biped.bipedRightArm.showModel = armorSlot == 1;
        biped.bipedLeftArm.showModel = armorSlot == 1;
        biped.bipedRightLeg.showModel = armorSlot == 2 || armorSlot == 3;
        biped.bipedLeftLeg.showModel = armorSlot == 2 || armorSlot == 3;

        ItemStack itemstack = entityLiving.getHeldItem();
        biped.heldItemRight = itemstack != null ? 1 : 0;

        if (itemstack != null && entityLiving.getHeldItem().stackSize > 0)
        {
            EnumAction enumaction = itemstack.getItemUseAction();

            if (enumaction == EnumAction.block)
            {
                biped.heldItemRight = 3;
            }
            else if (enumaction == EnumAction.bow)
            {
                biped.aimedBow = true;
            }
        }
        return biped;
    }

    @SideOnly(Side.CLIENT)
    protected abstract String getHoodTexture();
    @SideOnly(Side.CLIENT)
    protected abstract String getBodyTexture();
    @SideOnly(Side.CLIENT)
    protected abstract String getOverlayTexture();
    @SideOnly(Side.CLIENT)
    protected abstract ModelDualLayerArmor getBodyModel(int slot);
    @SideOnly(Side.CLIENT)
    protected abstract ModelBiped getHeadModel();

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (entity instanceof EntityPlayer && !(((EntityPlayer) entity).capabilities.isCreativeMode)) {
            if (stack.getMetadata() < stack.getMaxDurability()) {
                stack.setMetadata(stack.getMaxDurability() + 1);
            } else {
                // Create broken item here
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        return FormatCodes.Yellow.format + super.getItemStackDisplayName(par1ItemStack);
    }
}

package sct.hexxitgear.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.core.ArmorSet;
import sct.hexxitgear.model.ModelDualLayerArmor;
import sct.hexxitgear.model.ModelSageHood;
import sct.hexxitgear.util.FormatCodes;

import java.util.List;

public class ItemMagicianArmor extends ItemHexxitArmor {

    public ItemMagicianArmor(int renderIndex, int slot) {
        super(ArmorMaterial.DIAMOND, renderIndex, slot);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getHoodTexture() {
        return "hexxitgear:textures/maps/SageHood.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getOverlayTexture() {
        return "hexxitgear:textures/armor/sage.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getBodyTexture() {
        return "hexxitgear:textures/armor/sage2.png";
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
    private static ModelSageHood hood = new ModelSageHood();
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor leggings = new ModelDualLayerArmor(0.5f);
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor chest = new ModelDualLayerArmor(1.0f);
    @SideOnly(Side.CLIENT)
    private static ModelDualLayerArmor feet = new ModelDualLayerArmor(0.5f);

    @SideOnly(Side.CLIENT)
    protected ModelBiped getHeadModel() {
        return hood;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
        infoList.add(FormatCodes.Bold.format + FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.magic") + FormatCodes.Reset.format);

        if (this.armorType == 3) {
            if (par4 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                boolean isEmpowered = (par2EntityPlayer.getCurrentArmor(0) != null && par2EntityPlayer.getCurrentArmor(0).getItem() == this && ArmorSet.getPlayerArmorSet(par2EntityPlayer.getDisplayName()) != null);
                String powerName = isEmpowered ? "gui.hexxitgear.boots.magic.empowered" : "gui.hexxitgear.boots.magic";
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

        if (itemStack.getItem() == HexxitGear.magicBoots) {
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

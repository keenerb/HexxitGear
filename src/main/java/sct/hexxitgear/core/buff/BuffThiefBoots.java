package sct.hexxitgear.core.buff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class BuffThiefBoots implements IBuffHandler {
    @Override
    public void applyPlayerBuffs(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2 * 20, 0));

        player.stepHeight = 1.003F;
    }

    @Override
    public void removePlayerBuffs(EntityPlayer player) {
        if (player.stepHeight == 1.003F) {
            player.stepHeight = 0.5001F;
        }
    }
}

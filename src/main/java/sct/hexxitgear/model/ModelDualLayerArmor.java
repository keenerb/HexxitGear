package sct.hexxitgear.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModelDualLayerArmor extends ModelBiped {
    private ModelBiped skinModel;
    private ModelBiped armorModel;
    private boolean drawOverlay = false;

    public ModelDualLayerArmor() {
        this(0.05f);
    }

    public ModelDualLayerArmor(float skinSize)
    {
        skinModel = new ModelBiped(0.05f);
        armorModel = new ModelBiped(skinSize);
    }

    public void prepareForOverlay() {
        drawOverlay = true;
    }

    public void prepareForNormal() {
        drawOverlay = false;
    }

    @Override
    public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float scale)
    {
        //Pick out the correct model to draw
        ModelBiped biped = null;
        if (drawOverlay) {
            biped = armorModel;
            drawOverlay = false;
        } else
            biped = skinModel;

        //Set up anything we can't pipe through
        setupRenderData(biped);
        biped.render(entity, f1, f2, f3, f4, f5, scale);
    }

    @Override
    public void renderEars(float scale)
    {
        setupRenderData(armorModel);
        armorModel.renderEars(scale);
    }

    @Override
    public void renderCloak(float scale) {
        setupRenderData(armorModel);
        armorModel.renderCloak(scale);
    }

    private void setupRenderData(ModelBiped biped) {
        biped.isSneak = this.isSneak;
        biped.heldItemLeft = this.heldItemLeft;
        biped.heldItemRight = this.heldItemRight;
        biped.aimedBow = this.aimedBow;
        biped.bipedHead.showModel = this.bipedHead.showModel;
        biped.bipedHeadwear.showModel = this.bipedHeadwear.showModel;
        biped.bipedBody.showModel = this.bipedBody.showModel;
        biped.bipedRightArm.showModel = this.bipedRightArm.showModel;
        biped.bipedLeftArm.showModel = this.bipedLeftArm.showModel;
        biped.bipedLeftLeg.showModel = this.bipedLeftLeg.showModel;
        biped.bipedRightLeg.showModel = this.bipedRightLeg.showModel;
        biped.bipedCloak.showModel = this.bipedCloak.showModel;
        biped.bipedEars.showModel = this.bipedEars.showModel;
        biped.swingProgress = this.swingProgress;
        biped.isRiding = this.isRiding;
        biped.isChild = this.isChild;
    }
}

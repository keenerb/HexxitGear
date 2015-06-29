package sct.hexxitgear.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSageHood extends ModelBiped {
    //fields
    ModelRenderer head;
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;

    public ModelSageHood() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -7.5F, -4F, 8, 8, 8);
        head.setRotationPoint(0F, 0F, 0F);
        head.setTextureSize(64, 64);
        head.mirror = true;

        Shape1 = new ModelRenderer(this, 0, 33);
        Shape1.addBox(-5F, -9F, -5F, 10, 9, 10);
        Shape1.setRotationPoint(0F, 0F, 0F);
        Shape1.setTextureSize(64, 64);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);

        Shape2 = new ModelRenderer(this, 49, 0);
        Shape2.addBox(-3F, -9F, 5F, 6, 5, 1);
        Shape2.setRotationPoint(0F, 0F, 0F);
        Shape2.setTextureSize(64, 64);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);

        Shape3 = new ModelRenderer(this, 49, 6);
        Shape3.addBox(-2F, -9F, 6F, 4, 3, 1);
        Shape3.setRotationPoint(0F, 0F, 0F);
        Shape3.setTextureSize(64, 64);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        float scaledUp = f5 + 0.009F;
        float suLarge = f5 + 0.002F;
        setRotationAngles(f, f1, f2, f3, f4, scaledUp, entity);

        head.render(scaledUp);
        Shape1.render(suLarge);
        Shape2.render(suLarge);
        Shape3.render(suLarge);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        setup(head, p_78087_4_, p_78087_5_);
        setup(Shape1, p_78087_4_, p_78087_5_);
        setup(Shape2, p_78087_4_, p_78087_5_);
        setup(Shape3, p_78087_4_, p_78087_5_);
    }

    private void setup(ModelRenderer box, float x, float y) {
        box.rotateAngleY = x / (180F / (float) Math.PI);
        box.rotateAngleX = y / (180F / (float) Math.PI);
        box.rotationPointY = (this.isSneak) ? 1.0f : 0.0f;
    }
}
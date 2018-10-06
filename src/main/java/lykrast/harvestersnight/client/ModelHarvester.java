package lykrast.harvestersnight.client;

import lykrast.harvestersnight.common.EntityHarvester;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;

public class ModelHarvester extends ModelBiped {

	public ModelHarvester() {
		this(0.0F);
	}

	public ModelHarvester(float modelSize) {
		super(modelSize, 0.0F, 64, 64);
		bipedLeftLeg.showModel = false;
		bipedHeadwear.showModel = false;
		bipedRightLeg = new ModelRenderer(this, 32, 0);
		bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        EntityHarvester EntityHarvester = (EntityHarvester)entityIn;

        if (EntityHarvester.isCharging())
        {
            if (EntityHarvester.getPrimaryHand() == EnumHandSide.RIGHT) bipedRightArm.rotateAngleX = 3.7699115F;
            else bipedLeftArm.rotateAngleX = 3.7699115F;
        }
        if (EntityHarvester.isCasting())
        {
            if (EntityHarvester.getPrimaryHand() == EnumHandSide.RIGHT) bipedLeftArm.rotateAngleX = 3.7699115F;
            else bipedRightArm.rotateAngleX = 3.7699115F;
        }

        bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
    }

}

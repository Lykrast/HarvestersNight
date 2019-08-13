package lykrast.harvestersnight.client;

import lykrast.harvestersnight.common.EntityHarvester;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.HandSide;

public class ModelHarvester extends BipedModel<EntityHarvester> {
	public ModelHarvester() {
		this(0.0F);
	}

	public ModelHarvester(float modelSize) {
		super(modelSize, 0.0F, 64, 64);
		bipedLeftLeg.showModel = false;
		bipedHeadwear.showModel = false;
		bipedRightLeg = new RendererModel(this, 32, 0);
		bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
	}
	
	@Override
	public void setRotationAngles(EntityHarvester entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        EntityHarvester EntityHarvester = (EntityHarvester)entityIn;

        if (EntityHarvester.isCharging())
        {
            if (EntityHarvester.getPrimaryHand() == HandSide.RIGHT) bipedRightArm.rotateAngleX = 3.7699115F;
            else bipedLeftArm.rotateAngleX = 3.7699115F;
        }
        if (EntityHarvester.isCasting())
        {
            if (EntityHarvester.getPrimaryHand() == HandSide.RIGHT) bipedLeftArm.rotateAngleX = 3.7699115F;
            else bipedRightArm.rotateAngleX = 3.7699115F;
        }

        bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
    }

}

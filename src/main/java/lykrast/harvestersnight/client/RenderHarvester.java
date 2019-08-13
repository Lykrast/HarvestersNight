package lykrast.harvestersnight.client;

import lykrast.harvestersnight.common.EntityHarvester;
import lykrast.harvestersnight.common.HarvestersNight;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RenderHarvester extends BipedRenderer<EntityHarvester, ModelHarvester> {
	public static final ResourceLocation TEXTURES = new ResourceLocation(HarvestersNight.MODID, "textures/entity/harvester.png"),
			EYES = new ResourceLocation(HarvestersNight.MODID, "textures/entity/harvester_eyes.png");

	public RenderHarvester(EntityRendererManager rendermanagerIn) {
		super(rendermanagerIn, new ModelHarvester(), 0.5F);
        addLayer(new LayerEyesHarvester(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHarvester entity) {
		return TEXTURES;
	}

}

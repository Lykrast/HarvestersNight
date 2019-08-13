package lykrast.harvestersnight.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import lykrast.harvestersnight.common.EntityHarvester;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerEyesHarvester extends LayerRenderer<EntityHarvester, ModelHarvester> {
	public LayerEyesHarvester(IEntityRenderer<EntityHarvester, ModelHarvester> render) {
    	super(render);
    }
	
	@Override
	public void render(EntityHarvester entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.bindTexture(RenderHarvester.EYES);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		if (entityIn.isInvisible()) GlStateManager.depthMask(false);
		else GlStateManager.depthMask(true);

		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GameRenderer gamerenderer = Minecraft.getInstance().gameRenderer;
		gamerenderer.setupFogColor(true);
		getEntityModel().render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		gamerenderer.setupFogColor(false);
		i = entityIn.getBrightnessForRender();
		j = i % 65536;
		k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
		this.func_215334_a(entityIn);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}

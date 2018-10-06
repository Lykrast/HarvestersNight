package lykrast.harvestersnight.client;

import lykrast.harvestersnight.common.CommonProxy;
import lykrast.harvestersnight.common.EntityHarvester;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		RenderingRegistry.registerEntityRenderingHandler(EntityHarvester.class, RenderHarvester::new);
	}

}

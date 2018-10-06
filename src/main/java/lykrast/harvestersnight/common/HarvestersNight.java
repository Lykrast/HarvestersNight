package lykrast.harvestersnight.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = HarvestersNight.MODID, 
	name = HarvestersNight.NAME, 
	version = HarvestersNight.VERSION, 
	acceptedMinecraftVersions = "[1.12, 1.13)")
@Mod.EventBusSubscriber
public class HarvestersNight {
    public static final String MODID = "harvestersnight";
    public static final String NAME = "Harvester's Night";
    public static final String VERSION = "@VERSION@";

	public static Logger logger = LogManager.getLogger(MODID);
	
	@SidedProxy(clientSide = "lykrast.harvestersnight.client.ClientProxy", serverSide = "lykrast.harvestersnight.common.CommonProxy")
	public static CommonProxy proxy;
	
	//Shoving everything in this class since it's not gonna be a big mod
	public static ToolMaterial harvesterMaterial;
	public static Item harvesterScythe;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		//Only one item so inlining my usual helper functions
		harvesterMaterial = EnumHelper.addToolMaterial("harvester", 
				ToolMaterial.IRON.getHarvestLevel(), 
				1323, 
				ToolMaterial.IRON.getEfficiency(), 
				ToolMaterial.IRON.getAttackDamage(), 
				ToolMaterial.IRON.getEnchantability());
		harvesterScythe = new ItemHarvesterScythe(harvesterMaterial)
				.setRegistryName(new ResourceLocation(MODID, "harvester_scythe"))
				.setUnlocalizedName(MODID + ".harvester_scythe")
				.setCreativeTab(CreativeTabs.TOOLS);
		event.getRegistry().register(harvesterScythe);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent evt) {
		ModelLoader.setCustomModelResourceLocation(harvesterScythe, 0, new ModelResourceLocation(harvesterScythe.getRegistryName(), "inventory"));
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityHarvester.class)
				.name(MODID + ".harvester")
				.id(new ResourceLocation(MODID, "harvester"), 1)
				.tracker(64, 3, true)
				.egg(0x764F29, 0xFFD108)
				.build()
			);
		//LootTableList.register(EntityHarvester.LOOT);
	}

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	proxy.preInit(event);
    }
}

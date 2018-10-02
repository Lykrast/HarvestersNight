package lykrast.harvestersnight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}

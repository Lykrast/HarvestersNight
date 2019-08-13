package lykrast.harvestersnight.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lykrast.harvestersnight.client.RenderHarvester;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(HarvestersNight.MODID)
@Mod.EventBusSubscriber
public class HarvestersNight {
    public static final String MODID = "harvestersnight";

	public static Logger logger = LogManager.getLogger(MODID);
	
	public HarvestersNight() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::clientInit);
		bus.addListener(this::setupCommon);
//		bus.addListener(ConfigHandler::configChanged);
		
//		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.Common.CONFIG_SPEC);
	}

    private void clientInit(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityHarvester.class, RenderHarvester::new);
    }
    
    private void setupCommon(final FMLCommonSetupEvent event) {
    	//TODO: Spawns
//    	if (HarvestersNightConfig.harvesterWeight > 0) builder.spawn(EnumCreatureType.MONSTER, HarvestersNightConfig.harvesterWeight, 1, 1, ForgeRegistries.BIOMES.getValuesCollection());
    }
	
	//Shoving everything in this class since it's not gonna be a big mod
	public static IItemTier harvesterMaterial;
	public static Item harvesterScythe, egg;
	
	public static EntityType<EntityHarvester> harvester;
	
	public static SoundEvent harvesterCharge, harvesterSpell, harvesterSpawn, harvesterHurt, harvesterDie;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		harvesterMaterial = new CustomItemTier(2, 1323, 6.0F, 2.0F, 14, () -> Ingredient.fromItems(Items.IRON_INGOT));
		harvesterScythe = new ItemHarvesterScythe(harvesterMaterial, (new Item.Properties()).group(ItemGroup.TOOLS))
				.setRegistryName(new ResourceLocation(MODID, "harvester_scythe"));
		event.getRegistry().register(harvesterScythe);
		
		//Need the mob early for the egg
		harvester = EntityType.Builder.create(EntityHarvester::new, EntityClassification.MONSTER).size(0.6F, 1.95F).setTrackingRange(32).setUpdateInterval(3).build("harvester");
		harvester.setRegistryName(MODID, "harvester");
		
		egg = new SpawnEggItem(harvester, 0x764F29, 0xFFD108, new Item.Properties().group(ItemGroup.MISC));
		egg.setRegistryName(MODID, "harvester_spawn_egg");
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		event.getRegistry().register(harvester);
	}
	
	@SubscribeEvent
	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> reg = event.getRegistry();
		harvesterCharge = registerSoundEvent(reg, "harvester.charge");
		harvesterSpell = registerSoundEvent(reg, "harvester.spell");
		harvesterSpawn = registerSoundEvent(reg, "harvester.spawn");
		harvesterHurt = registerSoundEvent(reg, "harvester.hurt");
		harvesterDie = registerSoundEvent(reg, "harvester.die");
	}

	public static SoundEvent registerSoundEvent(IForgeRegistry<SoundEvent> reg, String name) {
		ResourceLocation location = new ResourceLocation(MODID, name);
		SoundEvent event = new SoundEvent(location).setRegistryName(location);
		reg.register(event);

		return event;
	}
}

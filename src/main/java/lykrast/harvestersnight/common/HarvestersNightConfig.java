package lykrast.harvestersnight.common;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = HarvestersNight.MODID)
@LangKey("config." + HarvestersNight.MODID + ".title")
public class HarvestersNightConfig {
	@RangeInt(min = 0)
	@RequiresMcRestart
	@LangKey("config." + HarvestersNight.MODID + ".weight")
	@Comment({"Spawn weight of the Harvester", "0 disables natural spawn"})
	public static int harvesterWeight = 5;
	
	@RangeInt(min = 1)
	@LangKey("config." + HarvestersNight.MODID + ".chance")
	@Comment("1 in X chance that the Harvester actually spawns when it can")
	public static int harvesterChance = 50;
	
	//TODO: find how to have a list in there
//	@LangKey("config." + HarvestersNight.MODID + ".mode")
//	@Comment("Is the dimension ID list a whitelist (true) or blacklist (false)?")
//	public static boolean whiteList = true;
//	
//	@LangKey("config." + HarvestersNight.MODID + ".dim")
//	@Comment("Dimension IDs the Harvester is (dis)allowed to spawn in")
//	public static List<Integer> dimList;
//	
//	static {
//		dimList = new ArrayList<>();
//		dimList.add(0);
//	}
	
	@Mod.EventBusSubscriber(modid = HarvestersNight.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(HarvestersNight.MODID)) {
				ConfigManager.sync(HarvestersNight.MODID, Config.Type.INSTANCE);
			}
		}
	}

}

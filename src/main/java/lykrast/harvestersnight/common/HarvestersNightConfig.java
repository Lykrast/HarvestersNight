package lykrast.harvestersnight.common;

//@Config(modid = HarvestersNight.MODID)
//@LangKey("config." + HarvestersNight.MODID + ".title")
public class HarvestersNightConfig {
	//TODO: remake config
	
	//Spawning
//	@RangeInt(min = 0)
//	@RequiresMcRestart
//	@LangKey("config." + HarvestersNight.MODID + ".weight")
//	@Comment({"Spawn weight of the Harvester", "0 disables natural spawn"})
	public static int harvesterWeight = 5;
	
//	@RangeInt(min = 1)
//	@LangKey("config." + HarvestersNight.MODID + ".chance")
//	@Comment("1 in X chance that the Harvester actually spawns when it can")
	public static int harvesterChance = 50;
	
//	@LangKey("config." + HarvestersNight.MODID + ".mode")
//	@Comment("Is the dimension ID list a whitelist (true) or blacklist (false)?")
	public static boolean whiteList = true;
	
//	@LangKey("config." + HarvestersNight.MODID + ".dim")
//	@Comment("Dimension IDs the Harvester is (dis)allowed to spawn in")
	public static int[] dimList = {0};
	
	//Being a boss
//	@LangKey("config." + HarvestersNight.MODID + ".isboss")
//	@Comment("Whether the Harvester is internally considered a boss (some mods care)")
	public static boolean isBoss = true;
	
//	@LangKey("config." + HarvestersNight.MODID + ".healthbar")
//	@Comment("Whether a boss health bar is shown or not")
	public static boolean healthBar = true;
	
//	@LangKey("config." + HarvestersNight.MODID + ".lightning")
//	@Comment("Whether lightning strikes when the Harvester spawns")
	public static boolean lightning = true;
	
//	@LangKey("config." + HarvestersNight.MODID + ".laugh")
//	@Comment("Whether the Harvester laughs when it spawns")
	public static boolean laugh = true;

}

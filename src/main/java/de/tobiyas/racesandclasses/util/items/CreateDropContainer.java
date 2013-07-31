package de.tobiyas.racesandclasses.util.items;

import java.io.File;
import java.io.IOException;

import org.bukkit.entity.EntityType;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;

public class CreateDropContainer {
	
	/**
	 * Creates a Drop Container for the passed path + {@link EntityType}.
	 * 
	 * @param path to the configuration
	 * @param type of the {@link EntityType}
	 * @return
	 */
	public static DropContainer loadDropContainer(String path, EntityType type){
		YAMLConfigExtended config = new YAMLConfigExtended(path).load();
		if(!config.getValidLoad()){
			return null;
		}
		
		for(String name : config.getChildren("monster")){
			String exactName = config.getString("monster." + name + ".type");
			if(type.name().equalsIgnoreCase(exactName)){
				return generateContainer(config, "monster." + name + ".");
			}
		}
		
		return null;
	}
	
	
	private static DropContainer generateContainer(YAMLConfigExtended config, String pre){
		int minEXP = config.getInt(pre + "expmin", 0);
		int maxEXP = config.getInt(pre + "expmax", -1);
		
		DropContainer container = new DropContainer(minEXP, maxEXP);
		
		for(String item : config.getChildren(pre + "drop")){
			String extend = config.getString(pre + "drop." + item);
			try{
				container.parseString(item + ";" + extend);
			}catch(Exception e){
				RacesAndClasses.getPlugin().getDebugLogger().logError("Could not create Item: " + item + " with extention: " + extend + " in type: " + pre);
				continue;
			}
		}
		
		return container;
	}
	
	/**
	 * Creates all DropContainers to the passed Path.
	 * 
	 * @param path
	 */
	public static void createAllContainers(String path) {
		if(!createStructure(path)) return;
		YAMLConfigExtended config = new YAMLConfigExtended(path).load();
		if(!config.getValidLoad()) return;
		
		config.createSection("monster");
		
		for(EntityType type : EntityType.values()){
			createContainer(type, config);
		}
		
		config.save();
	}
	
	private static boolean createStructure(String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
				return true;
			} catch (IOException e) {
				RacesAndClasses.getPlugin().log("Could not create DropRates.yml");
			}
		}
		
		return false;
	}

	private static void createContainer(EntityType type, YAMLConfigExtended config){
		switch(type){
			case CHICKEN: createChickenContainer(config); break;
			case COW: createCowContainer(config); break;
			case MUSHROOM_COW: createMushroomCowContainer(config); break;
			case OCELOT: createOcelotContainer(config); break;
			case PIG: createPigContainer(config); break;
			case SHEEP: createSheepContainer(config); break;
			case SQUID: createSquidContainer(config); break;
			case VILLAGER: createVillagerContainer(config); break;
			case ENDERMAN: createEndermanContainer(config); break;
			case WOLF: createWolfContainer(config); break;
			case PIG_ZOMBIE: createZombiePigmanContainer(config); break;
			case BLAZE: createBlazeContainer(config); break;
			case CAVE_SPIDER: createCavespiderContainer(config); break;
			case CREEPER: createCreeperContainer(config); break;
			case GHAST: createGhastContainer(config); break;
			case MAGMA_CUBE: createMagmacubeContainer(config); break;
			case SILVERFISH: createSilverfishContainer(config); break;
			case SKELETON: createSkeletonContainer(config); break;
			case SLIME: createSlimeContainer(config); break;
			case SPIDER: createSpiderContainer(config); break;
			case ZOMBIE: createZombieContainer(config); break;
			case SNOWMAN: createSnowgolemContainer(config); break;
			case IRON_GOLEM: createIrongolemContainer(config); break;
			case ENDER_DRAGON: createEnderdragonContainer(config); break;
			case GIANT: createGiantContainer(config); break;
			case BAT: createBatContainer(config); break;
			case WITCH: createWitchContainer(config); break;
			case WITHER: createWitherContainer(config); break;
		
			default: //TODO: Think about//RacesAndClasses.getPlugin().getDebugLogger().log("Could not find STD dropContainer for: " + type.name());
		}
	}
	
	private static void createChickenContainer(YAMLConfigExtended config){
		String pre = "monster.chicken";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.CHICKEN.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createCowContainer(YAMLConfigExtended config){
		String pre = "monster.cow";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.COW.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createMushroomCowContainer(YAMLConfigExtended config){
		String pre = "monster.mushroomcow";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.MUSHROOM_COW.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createOcelotContainer(YAMLConfigExtended config){
		String pre = "monster.ocelot";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.OCELOT.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createPigContainer(YAMLConfigExtended config){
		String pre = "monster.pig";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.PIG.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createSheepContainer(YAMLConfigExtended config){
		String pre = "monster.sheep";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SHEEP.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createSquidContainer(YAMLConfigExtended config){
		String pre = "monster.squid";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SQUID.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createVillagerContainer(YAMLConfigExtended config){
		String pre = "monster.villager";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.VILLAGER.name());
		config.set(pre + ".expmin", 0);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createEndermanContainer(YAMLConfigExtended config){
		String pre = "monster.enderman";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.ENDERMAN.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createWolfContainer(YAMLConfigExtended config){
		String pre = "monster.wolf";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.WOLF.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", 3);
	}
	
	private static void createZombiePigmanContainer(YAMLConfigExtended config){
		String pre = "monster.zombiepigman";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.PIG_ZOMBIE.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createBlazeContainer(YAMLConfigExtended config){
		String pre = "monster.blaze";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.BLAZE.name());
		config.set(pre + ".expmin", 10);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createCavespiderContainer(YAMLConfigExtended config){
		String pre = "monster.cavespider";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.CAVE_SPIDER.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createCreeperContainer(YAMLConfigExtended config){
		String pre = "monster.creeper";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.CREEPER.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createGhastContainer(YAMLConfigExtended config){
		String pre = "monster.ghast";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.GHAST.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createMagmacubeContainer(YAMLConfigExtended config){
		String pre = "monster.magmacube";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.MAGMA_CUBE.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createSilverfishContainer(YAMLConfigExtended config){
		String pre = "monster.silverfish";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SILVERFISH.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createSkeletonContainer(YAMLConfigExtended config){
		String pre = "monster.skeleton";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SKELETON.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createSlimeContainer(YAMLConfigExtended config){
		String pre = "monster.slime";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SLIME.name());
		config.set(pre + ".expmin", 1);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createSpiderContainer(YAMLConfigExtended config){
		String pre = "monster.spider";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SPIDER.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createZombieContainer(YAMLConfigExtended config){
		String pre = "monster.zombie";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.ZOMBIE.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createSnowgolemContainer(YAMLConfigExtended config){
		String pre = "monster.snowgolem";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.SNOWMAN.name());
		config.set(pre + ".expmin", 0);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createIrongolemContainer(YAMLConfigExtended config){
		String pre = "monster.irongolem";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.IRON_GOLEM.name());
		config.set(pre + ".expmin", 0);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createEnderdragonContainer(YAMLConfigExtended config){
		String pre = "monster.enderdragon";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.ENDER_DRAGON.name());
		config.set(pre + ".expmin", 20000);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createGiantContainer(YAMLConfigExtended config){
		String pre = "monster.giant";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.GIANT.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createBatContainer(YAMLConfigExtended config){
		String pre = "monster.bat";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.BAT.name());
		config.set(pre + ".expmin", 0);
		config.set(pre + ".expmax", -1);
	}
	
	private static void createWitchContainer(YAMLConfigExtended config){
		String pre = "monster.witch";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.WITCH.name());
		config.set(pre + ".expmin", 5);
		config.set(pre + ".expmax", -1);
	}

	private static void createWitherContainer(YAMLConfigExtended config){
		String pre = "monster.wither";
		config.createSection(pre);
		config.set(pre + ".type", EntityType.WITHER.name());
		config.set(pre + ".expmin", 50);
		config.set(pre + ".expmax", -1);
	}

}

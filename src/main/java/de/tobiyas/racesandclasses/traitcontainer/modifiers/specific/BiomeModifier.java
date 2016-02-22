package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;

public class BiomeModifier extends AbstractModifier {

	/**
	 * The Biomes to be applied.
	 */
	protected final Set<Biome> biomes;
	
	
	public BiomeModifier(Set<Biome> biomes, double modifier, String toModify) {
		super(modifier, toModify);
		
		this.biomes = biomes;
	}
	
	
	@Override
	public boolean canBeApplied(String toModify, RaCPlayer player) {
		if(!super.canBeApplied(toModify, player)) return false;
		
		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		
		return biomes.contains(player.getWorld().getBiome(x, z));
	}

	
	/**
	 * Generates the Modifier by the values Passed.
	 * 
	 * @param descriptor the descriptor to parse
	 * @param modifier the modifier to parse.
	 * 
	 * @return the Generated Modifier or Null if not possible.
	 */
	public static BiomeModifier generate(String descriptor, double modifier, String toModify){
		String[] biomeSplit = descriptor.split(",");
		Set<Biome> biomes = new HashSet<Biome>();
		
		for(String biomeName : biomeSplit){
			if(biomeName.equals("all")) for(Biome biome : Biome.values()) biomes.add(biome);
			
			Biome biome = null;
			try{ biome = Biome.valueOf(biomeName.toUpperCase()); }catch(IllegalArgumentException exp){ continue; }
			if(biome != null) biomes.add(biome);
		}
		
		//Keine Arme, Keine Kekse!
		if(biomes.isEmpty()) return null;
		
		return new BiomeModifier(biomes, modifier, toModify);
	}
}

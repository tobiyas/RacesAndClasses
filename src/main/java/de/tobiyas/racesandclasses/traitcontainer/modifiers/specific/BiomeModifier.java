package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.ModifierConfigurationException;

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
	 * 
	 * @throws NoBiomesSelectedException 
	 */
	public static BiomeModifier generate(String descriptor, double modifier, String toModify) throws ModifierConfigurationException {
		String[] biomeSplit = descriptor.split( Pattern.quote( "," ) );
		Set<Biome> biomes = new HashSet<Biome>();
		
		for(String biomeName : biomeSplit){
			if(biomeName.equals("all")) biomes.addAll( Arrays.asList( Biome.values() ) );
			
			Biome biome = null;
			try{ biome = Biome.valueOf(biomeName.toUpperCase()); }catch(IllegalArgumentException exp){ continue; }
			if(biome != null) biomes.add(biome);
		}
		
		//Keine Arme, Keine Kekse!
		if(biomes.isEmpty()) {
			throw new NoBiomesSelectedException("biome:"+descriptor+":"+modifier+":"+toModify, descriptor, modifier, toModify);
		}
		
		return new BiomeModifier(biomes, modifier, toModify);
	}
	
	
	
	public static class NoBiomesSelectedException extends ModifierConfigurationException {
	
		private static final long serialVersionUID = 2353232997031140761L;

		
		public NoBiomesSelectedException(String total, String descriptor, double value, String appliedOn) {
			super(total, "biome", descriptor, value, appliedOn);
		}
	
		@Override
		public String formatErrorMSG() {
			return "No biome could be parsed from: " + descriptor;
		}
		
	}
	
}

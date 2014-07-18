package de.tobiyas.racesandclasses.util.traitutil;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class TraitRegionChecker {

	/**
	 * Checks if the Location is laying in an Disabled WG-Region.
	 * <br>If no regions Defined or WG not present, it's always false.
	 * 
	 * @param location to check
	 * @return true if it is.
	 */
	public static boolean isInDisabledLocation(Location location){
		Set<String> disabledRegions = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledRegions();
		if(disabledRegions.isEmpty()) return false;
		
		if(Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return false;
		
		World world = location.getWorld();
		ApplicableRegionSet regions = WGBukkit.getRegionManager(world).getApplicableRegions(location);
		for(ProtectedRegion region : regions){
			for(String disabled : disabledRegions){
				if(region.getId().equalsIgnoreCase(disabled)) return true;
			}
		}
		
		return false;
	}
}

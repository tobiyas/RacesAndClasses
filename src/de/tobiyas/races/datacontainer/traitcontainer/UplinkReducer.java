package de.tobiyas.races.datacontainer.traitcontainer;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.traits.activate.TraitsWithUplink;

public class UplinkReducer implements Runnable {

	private ArrayList<TraitsWithUplink> traits;
	
	public UplinkReducer(){
		traits = new ArrayList<TraitsWithUplink>();
		int precision = Races.getPlugin().interactConfig().getconfig_globalUplinkTickPresition();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Races.getPlugin(), this, precision, precision);
	}
	
	public void registerTrait(TraitsWithUplink trait){
		traits.add(trait);
	}
	
	@Override
	public void run() {
		for(TraitsWithUplink trait : traits){
			if(trait != null)
				trait.tickReduceUplink();
		}
	}

}

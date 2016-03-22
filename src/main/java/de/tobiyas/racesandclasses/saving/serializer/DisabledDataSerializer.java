package de.tobiyas.racesandclasses.saving.serializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.saving.PlayerSavingData;

/**
 * This is a Data serializer that does nothing!
 * <br>No saving / loading!
 * 
 * @author Tobiyas
 */
public class DisabledDataSerializer implements PlayerDataSerializer {

	
	
	@Override
	public void saveData(PlayerSavingData data) {}
	

	@Override
	public void loadData(UUID id, PlayerDataLoadedCallback callback) {}

	@Override
	public void bulkLoadData(Set<UUID> ids, PlayerDataLoadedCallback callback) {}

	@Override
	public Collection<PlayerSavingData> bulkLoadDataNow(Set<UUID> ids) {
		Collection<PlayerSavingData> data = new HashSet<>();
		for(UUID id : ids) data.add(new PlayerSavingData(id));
		return data;
	}

	@Override
	public PlayerSavingData loadDataNow(UUID id) {
		return new PlayerSavingData(id);
	}

	@Override
	public Set<UUID> getAllIDsPresent() {
		return new HashSet<>();
	}

	@Override
	public void shutdown() {}

	@Override
	public boolean isFunctional() {
		return true;
	}

}

package de.tobiyas.races.tests.generate.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Player.class)
public class GeneratePlayer {

	
	public static Player generatePlayer(String playerName){
		Player mockPlayer = Mockito.mock(Player.class);
		Mockito.when(mockPlayer.getName()).thenReturn(playerName);
		
		PlayerInventory mockInventory = Mockito.mock(PlayerInventory.class);
		Mockito.when(mockPlayer.getInventory()).thenReturn(mockInventory);
		
		return mockPlayer;
	}
}

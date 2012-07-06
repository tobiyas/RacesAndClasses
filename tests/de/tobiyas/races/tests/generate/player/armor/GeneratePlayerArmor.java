package de.tobiyas.races.tests.generate.player.armor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerInventory.class)
public class GeneratePlayerArmor {

	public static Player addLeatherArmor(Player mockPlayer){
		PlayerInventory mockInventory = mockPlayer.getInventory();
		
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		
		ItemStack[] armor = new ItemStack[]{helmet, chest, legs, boots};
		Mockito.when(mockInventory.getArmorContents()).thenReturn(armor);
		return mockPlayer;
	}
	
	public static Player addGoldArmor(Player mockPlayer){
		PlayerInventory mockInventory = mockPlayer.getInventory();
		
		ItemStack helmet = new ItemStack(Material.GOLD_HELMET, 1);
		ItemStack chest = new ItemStack(Material.GOLD_CHESTPLATE, 1);
		ItemStack legs = new ItemStack(Material.GOLD_LEGGINGS, 1);
		ItemStack boots = new ItemStack(Material.GOLD_BOOTS, 1);
		
		ItemStack[] armor = new ItemStack[]{helmet, chest, legs, boots};
		Mockito.when(mockInventory.getArmorContents()).thenReturn(armor);
		return mockPlayer;
	}
	
	public static Player addIronArmor(Player mockPlayer){
		PlayerInventory mockInventory = mockPlayer.getInventory();
		
		ItemStack helmet = new ItemStack(Material.IRON_HELMET, 1);
		ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
		ItemStack legs = new ItemStack(Material.IRON_LEGGINGS, 1);
		ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
		
		ItemStack[] armor = new ItemStack[]{helmet, chest, legs, boots};
		Mockito.when(mockInventory.getArmorContents()).thenReturn(armor);
		return mockPlayer;
	}
	
	public static Player addChainArmor(Player mockPlayer){
		PlayerInventory mockInventory = mockPlayer.getInventory();
		
		ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET, 1);
		ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
		ItemStack legs = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
		ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
		
		ItemStack[] armor = new ItemStack[]{helmet, chest, legs, boots};
		Mockito.when(mockInventory.getArmorContents()).thenReturn(armor);
		return mockPlayer;
	}
	
	public static Player addDiamondArmor(Player mockPlayer){
		PlayerInventory mockInventory = mockPlayer.getInventory();
		
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
		
		ItemStack[] armor = new ItemStack[]{helmet, chest, legs, boots};
		Mockito.when(mockInventory.getArmorContents()).thenReturn(armor);
		return mockPlayer;
	}
}

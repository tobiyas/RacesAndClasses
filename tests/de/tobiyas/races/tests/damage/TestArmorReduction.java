package de.tobiyas.races.tests.damage;

import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import de.tobiyas.races.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.races.tests.generate.player.GeneratePlayer;
import de.tobiyas.races.tests.generate.player.armor.GeneratePlayerArmor;

public class TestArmorReduction {

	@Test
	public void testLeather(){
		Player player = GeneratePlayer.generatePlayer("Player");
		player = GeneratePlayerArmor.addLeatherArmor(player);
		
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		Assert.assertTrue("Armor not calced correctly: leather", armorToolManager.getArmorLevel(player) == 7);
	}
	
	@Test
	public void testGold(){
		Player player = GeneratePlayer.generatePlayer("Player");
		player = GeneratePlayerArmor.addGoldArmor(player);
		
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		Assert.assertTrue("Armor not calced correctly: gold", armorToolManager.getArmorLevel(player) == 11);
	}
	
	@Test
	public void testChain(){
		Player player = GeneratePlayer.generatePlayer("Player");
		player = GeneratePlayerArmor.addChainArmor(player);
		
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		Assert.assertTrue("Armor not calced correctly: chain", armorToolManager.getArmorLevel(player) == 12);
	}
	
	@Test
	public void testIron(){
		Player player = GeneratePlayer.generatePlayer("Player");
		player = GeneratePlayerArmor.addIronArmor(player);
		
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		Assert.assertTrue("Armor not calced correctly: iron", armorToolManager.getArmorLevel(player) == 15);
	}
	
	@Test
	public void testDiamond(){
		Player player = GeneratePlayer.generatePlayer("Player");
		player = GeneratePlayerArmor.addDiamondArmor(player);
		
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		Assert.assertTrue("Armor not calced correctly: diamond", armorToolManager.getArmorLevel(player) == 20);
	}
}

/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.damage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.utils.tests.generate.player.armor.GeneratePlayerArmor;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class TestArmorReduction {

	@Before
	public void initStuff(){
		GenerateBukkitServer.generateServer();
		
		GenerateBukkitServer.generatePlayerOnServer("PlayerArmorLeather");
		GeneratePlayerArmor.addLeatherArmor(Bukkit.getPlayer("PlayerArmorLeather"));
		
		GenerateBukkitServer.generatePlayerOnServer("PlayerArmorGold");
		GeneratePlayerArmor.addGoldArmor(Bukkit.getPlayer("PlayerArmorGold"));
		
		GenerateBukkitServer.generatePlayerOnServer("PlayerArmorChain");
		GeneratePlayerArmor.addChainArmor(Bukkit.getPlayer("PlayerArmorChain"));
		
		GenerateBukkitServer.generatePlayerOnServer("PlayerArmorIron");		
		GeneratePlayerArmor.addIronArmor(Bukkit.getPlayer("PlayerArmorIron"));
		
		GenerateBukkitServer.generatePlayerOnServer("PlayerArmorDiamond");
		GeneratePlayerArmor.addDiamondArmor(Bukkit.getPlayer("PlayerArmorDiamond"));
	}
	
	@Test
	public void testLeather(){
		Player player = Bukkit.getPlayer("PlayerArmorLeather");
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		
		//Test ArmorLevel
		Assert.assertEquals(7, armorToolManager.getArmorLevel(player), 0.1);
		
		//Test DamageReduction : work
		Assert.assertEquals(0.72, armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT), 0.01);
		
		//Test DamageReduction : regression
		Assert.assertFalse(armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT) == 1);
		
		//Test DamageIgnoreArmor
		Assert.assertEquals(1, armorToolManager.calcDamageToArmor(1, DamageCause.FIRE_TICK), 0.01);
	}
	
	@Test
	public void testGold(){
		Player player = Bukkit.getPlayer("PlayerArmorGold");
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		
		//Test ArmorLevel
		Assert.assertEquals(11, armorToolManager.getArmorLevel(player), 0.1);
		
		//Test DamageReduction : work
		Assert.assertEquals(0.56, armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT), 0.01);
		
		//Test DamageReduction : regression
		Assert.assertFalse(armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT) == 1);
		
		//Test DamageIgnoreArmor
		Assert.assertEquals(1, armorToolManager.calcDamageToArmor(1, DamageCause.FIRE_TICK), 0.01);
	}
	
	@Test
	public void testChain(){
		Player player = Bukkit.getPlayer("PlayerArmorChain");
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		
		//Test ArmorLevel
		Assert.assertEquals(12, armorToolManager.getArmorLevel(player), 0.1);
		
		//Test DamageReduction : work
		Assert.assertEquals(0.52, armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT), 0.01);
		
		//Test DamageReduction : regression
		Assert.assertFalse(armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT) == 1);
		
		//Test DamageIgnoreArmor
		Assert.assertEquals(1, armorToolManager.calcDamageToArmor(1, DamageCause.FIRE_TICK), 0.01);
	}
	
	@Test
	public void testIron(){
		Player player = Bukkit.getPlayer("PlayerArmorIron");
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		
		//Test ArmorLevel
		Assert.assertEquals(15, armorToolManager.getArmorLevel(player), 0.1);
		
		//Test DamageReduction : work
		Assert.assertEquals(0.4, armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT), 0.01);
		
		//Test DamageReduction : regression
		Assert.assertFalse(armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT) == 1);
		
		//Test DamageIgnoreArmor
		Assert.assertEquals(1, armorToolManager.calcDamageToArmor(1, DamageCause.FIRE_TICK), 0.01);
	}
	
	@Test
	public void testDiamond(){
		Player player = Bukkit.getPlayer("PlayerArmorDiamond");
		ArmorToolManager armorToolManager = new ArmorToolManager(player.getName());
		
		//Test ArmorLevel
		Assert.assertEquals(20, armorToolManager.getArmorLevel(player), 0.1);
		
		//Test DamageReduction : work
		Assert.assertEquals(0.2, armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT), 0.01);
		
		//Test DamageReduction : regression
		Assert.assertFalse(armorToolManager.calcDamageToArmor(1, DamageCause.CONTACT) == 1);
		
		//Test DamageIgnoreArmor
		Assert.assertEquals(1, armorToolManager.calcDamageToArmor(1, DamageCause.FIRE_TICK), 0.01);
	}
	
	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
	}
}

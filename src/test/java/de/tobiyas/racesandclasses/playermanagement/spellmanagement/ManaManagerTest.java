package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.MemberConfigManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.MagicSpellTrait.CostType;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;


public class ManaManagerTest {

	private ManaManager sut;
	private String playerName = "player";
	
	
	
	private void mockRaceManaAddition(double value){
		RaceContainer raceContainer = mock(RaceContainer.class);
		when(raceContainer.getManaBonus()).thenReturn(value);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainer);
	}
	
	
	private void mockClassManaAddition(double value){
		ClassContainer classContainer = mock(ClassContainer.class);
		when(classContainer.getManaBonus()).thenReturn(value);
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
	}
	
	
	@Before
	public void setupSut(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager()).thenReturn(mock(MemberConfigManager.class, Mockito.RETURNS_DEEP_STUBS));
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName)
				.getValueDisplayName("healthDisplayType")).thenReturn("score");
		
		sut = new ManaManager(playerName);
	}
	
	@After
	public void tearDown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	
	@Test
	public void test_constructor_and_getters(){
		assertEquals(playerName, sut.getPlayerName());
		assertEquals(0, sut.getCurrentMana(), 0.001);
		assertEquals(0, sut.getMaxMana(), 0.001);
	}
	
	@Test
	public void test_adding_mana_by_race_works(){
		double valueAdded = 42;
		mockRaceManaAddition(valueAdded);
		
		sut.rescanPlayer();
		assertEquals(valueAdded, sut.getMaxMana(), 0.001);
	}
	
	@Test
	public void setting_current_mana_to_max_when_max_sincs_works(){
		double valueAdded = 42;
		mockRaceManaAddition(valueAdded);
		sut.rescanPlayer();
		
		sut.fillMana(valueAdded);
		
		mockRaceManaAddition(valueAdded / 2);
		sut.rescanPlayer();
		
		assertEquals(valueAdded / 2, sut.getCurrentMana(), 0.001);
	}
	
	@Test
	public void test_adding_mana_by_class_works(){
		double valueAdded = 42;
		mockClassManaAddition(valueAdded);
		
		sut.rescanPlayer();
		assertEquals(valueAdded, sut.getMaxMana(), 0.001);
	}
	
	@Test
	public void test_filling_current_mana_works(){
		double valueAdded = 42;
		mockRaceManaAddition(valueAdded);
		sut.rescanPlayer();
		
		sut.fillMana(valueAdded);
		assertEquals(valueAdded, sut.getCurrentMana(), 0.001);
	}
	
	@Test
	public void test_filling_current_mana_over_max_fills_to_max_works(){
		double valueAdded = 42;
		mockRaceManaAddition(valueAdded);
		sut.rescanPlayer();
		
		sut.fillMana(valueAdded * 2);
		assertEquals(valueAdded, sut.getCurrentMana(), 0.001);
	}
	
	@Test
	public void test_drowning_current_mana_works(){
		double valueAdded = 42;
		mockRaceManaAddition(valueAdded);
		sut.rescanPlayer();
		
		sut.fillMana(valueAdded);
		
		sut.drownMana(valueAdded / 2);
		assertEquals(valueAdded / 2, sut.getCurrentMana(), 0.001);
	}
	
	@Test
	public void test_drowning_empty_current_mana_drowns_nothing_works(){
		double valueAdded = 42;
		mockRaceManaAddition(valueAdded);
		sut.rescanPlayer();
		
		sut.drownMana(valueAdded);
		assertEquals(0, sut.getCurrentMana(), 0.001);
	}
	
	
	@Test
	public void test_spell_casting_works_with_enough_mana(){
		double maxMana = 42;
		mockRaceManaAddition(maxMana);
		sut.rescanPlayer();
		
		sut.fillMana(maxMana);
		
		MagicSpellTrait spell = mock(MagicSpellTrait.class);
		when(spell.getCost()).thenReturn(maxMana / 2);
		when(spell.getCostType()).thenReturn(CostType.MANA);
		
		assertTrue(sut.playerCastSpell(spell));
	}
	
	@Test
	public void test_spell_casting_fails_with_not_enough_mana(){
		double maxMana = 42;
		mockRaceManaAddition(maxMana);
		sut.rescanPlayer();
		
		sut.fillMana(maxMana / 2);
		
		MagicSpellTrait spell = mock(MagicSpellTrait.class);
		when(spell.getCost()).thenReturn((maxMana / 2 ) + 1);
		when(spell.getCostType()).thenReturn(CostType.MANA);
		
		assertFalse(sut.playerCastSpell(spell));
	}
	
	@Test
	public void test_spell_casting_fails_with_CostType_HEALTH_needed(){
		double maxMana = 42;
		mockRaceManaAddition(maxMana);
		sut.rescanPlayer();
		
		sut.fillMana(maxMana);
		
		MagicSpellTrait spell = mock(MagicSpellTrait.class);
		when(spell.getCost()).thenReturn(maxMana / 2);
		when(spell.getCostType()).thenReturn(CostType.HEALTH);
		
		assertFalse(sut.playerCastSpell(spell));
	}
	
	@Test
	public void test_spell_casting_fails_with_CostType_ITEM_needed(){
		double maxMana = 42;
		mockRaceManaAddition(maxMana);
		sut.rescanPlayer();
		
		sut.fillMana(maxMana);
		
		MagicSpellTrait spell = mock(MagicSpellTrait.class);
		when(spell.getCost()).thenReturn(maxMana / 2);
		when(spell.getCostType()).thenReturn(CostType.ITEM);
		
		assertFalse(sut.playerCastSpell(spell));
	}
	
	@Test
	public void test_player_has_enough_mana_works(){
		double maxMana = 42;
		mockRaceManaAddition(maxMana);
		sut.rescanPlayer();
		
		assertFalse(sut.hasEnoughMana(maxMana/ 2));		
		sut.fillMana(maxMana);
		
		assertTrue(sut.hasEnoughMana(maxMana / 2));
	}
	
	@Test
	public void test_player_has_enough_mana_with_spells_works(){
		double maxMana = 42;
		mockRaceManaAddition(maxMana);
		sut.rescanPlayer();
		
		MagicSpellTrait spell = mock(MagicSpellTrait.class);
		when(spell.getCost()).thenReturn(maxMana / 2);
		when(spell.getCostType()).thenReturn(CostType.MANA);
		
		assertFalse(sut.hasEnoughMana(spell));
		
		sut.fillMana(maxMana);
		assertTrue(sut.hasEnoughMana(spell));
		
		when(spell.getCostType()).thenReturn(CostType.HEALTH);
		assertFalse(sut.hasEnoughMana(spell));
		
		when(spell.getCostType()).thenReturn(CostType.ITEM);
		assertFalse(sut.hasEnoughMana(spell));
	}

}

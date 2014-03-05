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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.HolderPermissions;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.traits.InvisibleTrait;
import de.tobiyas.racesandclasses.generate.traits.TraitWithConfigAnnotations;
import de.tobiyas.racesandclasses.generate.traits.TraitWithNoAnnotations;
import de.tobiyas.racesandclasses.generate.traits.VisibleTrait;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TraitStore.class, TraitConfigParser.class})
public abstract class AbstractTraitHolderTest {
	
	protected final AbstractTraitHolder sut;
	protected final String holderName;
	protected final String holderTag;
	protected AbstractHolderManager manager;
	
	protected YAMLConfigExtended config;
	
	
	protected AbstractTraitHolderTest(String holderName, String holderTag, AbstractTraitHolder sut){
		this.holderName = holderName;
		this.holderTag = holderTag;
		this.sut = sut;
		this.config = sut.config;
	}
	
	
	@Before
	public void init(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		manager = getHolderManager();
	}
	
	protected abstract AbstractHolderManager getHolderManager();
	
	
	@After
	public void teardown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	
	@Test
	public void constructor_sets_all_values_correct(){
		assertTrue(sut.getArmorPerms().isEmpty());
		assertEquals("", sut.getArmorString());
		assertEquals(0, sut.getManaBonus(), 0.001);
		assertTrue(sut.getVisibleTraits().isEmpty());
		assertTrue(sut.getTraits().isEmpty());
		assertTrue(sut.getPermissions().getPermissions().isEmpty());
		assertTrue(sut.getParsingExceptionsHappened().isEmpty());
		
		assertEquals(this.holderName, sut.getName());
		assertEquals(this.holderTag, sut.getTag());
		assertEquals(this.holderName, sut.toString());
	}
	
	
	@Test
	public void check_containsPlayer_works(){
		String playerName = "testPlayer";
		when(manager.getHolderOfPlayer(playerName)).thenReturn(sut);
		
		assertTrue(sut.containsPlayer(playerName));
		
		
		when(manager.getHolderOfPlayer(playerName)).thenReturn(null);
		assertFalse(sut.containsPlayer(playerName));
		
		AbstractTraitHolder otherMockHolder = mock(AbstractTraitHolder.class);
		when(otherMockHolder.getName()).thenReturn("other");
		when(manager.getHolderOfPlayer(playerName)).thenReturn(otherMockHolder);
		
		assertFalse(sut.containsPlayer(playerName));
	}
	
	
	@Test
	public void readArmor_works(){
		sut.config.set(holderName + ".config.armor", "leather,iron,gold,diamond,chain");
		sut.readArmor();
		
		assertEquals(ItemQuality.values().length - 1, sut.getArmorPerms().size());
	}
	
	@Test
	public void readArmor_with_all_works(){
		sut.config.set(holderName + ".config.armor", "all");
		sut.readArmor();
		
		assertEquals(ItemQuality.values().length - 1, sut.getArmorPerms().size());
	}
	
	
	@Test
	public void readPermissions_works(){
		List<String> permissions = new LinkedList<String>();
		permissions.add("perm1");
		permissions.add("perm2");
		permissions.add("perm3");
		
		sut.config.set(holderName + ".permissions", permissions);
		sut.readPermissionSection();
		
		HolderPermissions perms = sut.getPermissions();
		
		assertEquals(permissions.size(), perms.getPermissions().size());
		
		for(String permission : permissions){
			assertTrue(perms.getPermissions().contains(permission));
		}
	}
	
	@Test
	public void readPermissions_is_empty_when_section_does_not_exist_works(){
		sut.readPermissionSection();
		
		HolderPermissions perms = sut.getPermissions();
		
		assertTrue(perms.getPermissions().isEmpty());
	}
	
	
	@Test
	public abstract void test_config_load_works_with_full_config();

	
	@Test
	public abstract void test_config_load_works_with_empty_config();
	
	@Test
	public void loading_valid_traits_works() throws IOException{
		PowerMockito.mockStatic(TraitStore.class);
		
		Trait mock = mock(Trait.class);
		Trait mock2 = mock(Trait.class);
		Trait mock3 = mock(Trait.class);
		
		PowerMockito.when(TraitStore.buildTraitByName("test1", sut)).thenReturn(mock);
		PowerMockito.when(TraitStore.buildTraitByName("test2", sut)).thenReturn(mock2);
		PowerMockito.when(TraitStore.buildTraitByName("NormalArrow", sut)).thenReturn(mock3);
		
		PowerMockito.mockStatic(TraitConfigParser.class);
		
		
		config.createSection(holderName);
		config.createSection(holderName + ".traits");
		config.createSection(holderName + ".traits.test1");
		config.createSection(holderName + ".traits.test2");
		
		config.set(holderName + ".traits.test1.value", 42);
		config.set(holderName + ".traits.test2.value", 43);
		
		sut.readTraitSection();
		
		assertTrue(sut.getTraits().contains(mock));
		assertTrue(sut.getTraits().contains(mock2));
		
		if(manager == RacesAndClasses.getPlugin().getRaceManager()){
			assertTrue(sut.getTraits().contains(mock3));
		}
	}
	
	
	@Test
	public void loading_invalid_traits_adds_expcetions_to_list_works() throws IOException{
		String traitExceptionMessage1 = "Field: 'test.traits.test1.needed' not found in Config for Trait: ";		
		String traitExceptionMessage2 = "Field: 'test.traits.test2.needed' not found in Config for Trait: ";		
		
		PowerMockito.mockStatic(TraitStore.class);
		
		Trait trait1 = new TraitWithConfigAnnotations();
		Trait trait2 = new TraitWithConfigAnnotations();
		
		Trait trait3 = mock(Trait.class);
		when(trait3.getName()).thenReturn("NormalArrow");
		
		PowerMockito.when(TraitStore.buildTraitByName("test1", sut)).thenReturn(trait1);
		PowerMockito.when(TraitStore.buildTraitByName("test2", sut)).thenReturn(trait2);
		
		PowerMockito.when(TraitStore.buildTraitByName("NormalArrow", sut)).thenReturn(trait3);
		
		
		
		config.createSection(holderName);
		config.createSection(holderName + ".traits");
		config.createSection(holderName + ".traits.test1");
		config.createSection(holderName + ".traits.test2");
		
		config.set(holderName + ".traits.test1.value", 42);
		config.set(holderName + ".traits.test2.value", 43);
		
		sut.readTraitSection();
		
		if(manager == RacesAndClasses.getPlugin().getRaceManager()){
			assertTrue(sut.getTraits().contains(trait3));
		}
		
		assertEquals(2, sut.getParsingExceptionsHappened().size());
		
		HolderTraitParseException actualExp = sut.getParsingExceptionsHappened().get(0);
		assertEquals(traitExceptionMessage1, actualExp.getMessage());
		
		HolderTraitParseException actualExp2 = sut.getParsingExceptionsHappened().get(1);
		assertEquals(traitExceptionMessage2, actualExp2.getMessage());
	}
	
	
	@Test
	public void get_visible_traits_only_returns_visible_trais() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException{
		Trait invisibleTrait = new InvisibleTrait();
		Trait visibleTrait = new VisibleTrait();
		Trait traitWithoutAnnotations = new TraitWithNoAnnotations();

		sut.traits.add(invisibleTrait);
		sut.traits.add(visibleTrait);
		sut.traits.add(traitWithoutAnnotations);
		
		
		Set<Trait> traits = sut.getVisibleTraits();
		assertTrue(traits.contains(visibleTrait));
		assertFalse(traits.contains(invisibleTrait));
		assertFalse(traits.contains(traitWithoutAnnotations));
	}
}

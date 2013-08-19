package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolderTest;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TraitStore.class})
public class RaceContainerTest extends AbstractTraitHolderTest{

	
	public RaceContainerTest() throws IOException {
		super("test", "[test]", new RaceContainer(new YAMLConfigExtended(File.createTempFile("race", ",yml")), "test"));
	}
	

	@Override
	protected AbstractHolderManager getHolderManager() {
		return RacesAndClasses.getPlugin().getRaceManager();
	}

	@Test
	@Override
	public void test_config_load_works_with_full_config() {
		PowerMockito.mockStatic(TraitStore.class);
		Trait mock = mock(Trait.class);
		PowerMockito.when(TraitStore.buildTraitByName("NormalArrow", sut)).thenReturn(mock);
		
		
		double manabonus = 42;
		double raceMaxHealth = 30d;
		String chatColor = ChatColor.RED.toString();
		String chatFormat = "test123";
		
		config.set(holderName + ".config.manabonus", manabonus);
		config.set(holderName + ".config.racetag", holderTag);
		config.set(holderName + ".config.raceMaxHealth", raceMaxHealth);
		config.set(holderName + ".config.chat.color", chatColor);
		config.set(holderName + ".config.chat.format", chatFormat);
			
		try{
			sut.load();
			
			RaceContainer container = (RaceContainer) sut;
			
			assertEquals(manabonus, container.getManaBonus(), 0.001);
			assertEquals(raceMaxHealth, container.getRaceMaxHealth(), 0.001);
			assertEquals(chatColor, container.getRaceChatColor());
			assertEquals(chatFormat, container.getRaceChatFormat());
			assertEquals(holderTag, container.getTag());
			
			assertEquals(1, sut.getTraits().size());
		}catch(HolderParsingException exp){
			fail("Config full load failed: " + exp);
		}
	}

	@Test
	@Override
	public void test_config_load_works_with_empty_config() {
		int defaultMana = 0;
		double defaultHealth = 30;
		String chatColor = ChatColor.AQUA + "~";
		String chatFormat = "test123";
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth()).thenReturn(defaultHealth);
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color()).thenReturn(chatColor);
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format()).thenReturn(chatFormat);
		
		PowerMockito.mockStatic(TraitStore.class);
		Trait mock = mock(Trait.class);
		PowerMockito.when(TraitStore.buildTraitByName("NormalArrow", sut)).thenReturn(mock);
		
		try{
			sut.load();
			
			RaceContainer container = (RaceContainer) sut;
			
			assertEquals(defaultMana, container.getManaBonus(), 0.001);
			assertEquals(defaultHealth, container.getRaceMaxHealth(), 0.001);
			assertEquals(chatColor, container.getRaceChatColor());
			assertEquals(chatFormat, container.getRaceChatFormat());
			assertEquals(holderTag, container.getTag());
			
			assertEquals(1, sut.getTraits().size());
		}catch(HolderParsingException exp){
			fail("Config full load failed: " + exp);
		}
	}
	
	@Test
	public void loading_with_empty_config_by_static_constructor_works() throws IOException {
		double defaultHealth = 30;
		String chatColor = ChatColor.AQUA + "~";
		String chatFormat = "test123";
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth()).thenReturn(defaultHealth);
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color()).thenReturn(chatColor);
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format()).thenReturn(chatFormat);
		
		PowerMockito.mockStatic(TraitStore.class);
		Trait mock = mock(Trait.class);
		PowerMockito.when(TraitStore.buildTraitByName(anyString(), any(AbstractTraitHolder.class))).thenReturn(mock);
		
		try{
			sut.load();
			
			RaceContainer sut2 = RaceContainer.loadRace( new YAMLConfigExtended(File.createTempFile("race", ",yml")), holderName);
			RaceContainer container = (RaceContainer) sut;
			
			assertEquals(sut2.getManaBonus(), container.getManaBonus(), 0.001);
			assertEquals(sut2.getRaceMaxHealth(), container.getRaceMaxHealth(), 0.001);
			assertEquals(sut2.getRaceChatColor(), container.getRaceChatColor());
			assertEquals(sut2.getRaceChatFormat(), container.getRaceChatFormat());
			assertEquals(sut2.getTag(), container.getTag());
			
			assertEquals(sut2.getTraits(), sut.getTraits());
		}catch(HolderParsingException exp){
			fail("Config full load failed: " + exp);
		}
	}
	

	@Test
	public void reloading_adds_STDTraits_NormalArrowTrait(){
		PowerMockito.mockStatic(TraitStore.class);
		
		Trait mock = mock(Trait.class);
		PowerMockito.when(TraitStore.buildTraitByName("NormalArrow", sut)).thenReturn(mock);
		
		
		assertTrue(sut.getTraits().isEmpty());
		
		try{
			sut.load();
		
			assertTrue(sut.getTraits().size() == 1);
		}catch(HolderParsingException exp){
			fail("reloading adds STD Traits failed: " + exp);
		}
	}
	
	
	@Test
	public void test_editTABEntry_works(){
		String playerName = "player";
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_adaptListName()).thenReturn(true);
		
		((RaceContainer)sut ).editTABListEntry(playerName);
		
		String name = holderTag + playerName;
		verify(Bukkit.getPlayer(playerName)).setPlayerListName(name);
	}
	
	@Test
	public void editTabEntry_with_offline_player_fails(){
		String playerName = "player";
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_adaptListName()).thenReturn(true);
		
		((RaceContainer)sut ).editTABListEntry(playerName);
		
		verify(RacesAndClasses.getPlugin().getServer()).getPlayer(playerName);
	}
	
	@Test
	public void editTabEntry_fails_when_disabled(){
		String playerName = "player";
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_adaptListName()).thenReturn(false);
		
		((RaceContainer)sut ).editTABListEntry(playerName);
		
		verify(Bukkit.getPlayer(playerName), never()).setPlayerListName(anyString());
	}
	
	@Test
	public void editTabEntry_cuts_when_more_as_16_letters() throws IOException{
		String playerName = "player";
		String longHolderName = "Reaaaaaaly long long name";
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_adaptListName()).thenReturn(true);
		
		RaceContainer sut = new RaceContainer( new YAMLConfigExtended(File.createTempFile("race", ",yml")), longHolderName);
		((RaceContainer)sut ).editTABListEntry(playerName);
		
		
		String name = "[" + longHolderName + "]" + playerName;
		verify(Bukkit.getPlayer(playerName)).setPlayerListName(name.substring(0, 15));
	}
	
	@Test
	public void std_race_generation_works(){
		String raceChatColor = "";
		String raceChatFormat = "";
		double defaultHealth = 30d;
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultRaceName()).thenReturn(holderName);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultRaceTag()).thenReturn(holderTag);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth()).thenReturn(defaultHealth);		
		
		PowerMockito.mockStatic(TraitStore.class);
		
		Trait mock = mock(Trait.class);
		PowerMockito.when(TraitStore.buildTraitByName(anyString(), any(RaceContainer.class))).thenReturn(mock);
		
		RaceContainer sut = RaceContainer.generateSTDRace();
		
		assertEquals(0, sut.getManaBonus(), 0.001);
		assertEquals(defaultHealth, sut.getRaceMaxHealth(), 0.001);
		assertEquals(raceChatColor, sut.getRaceChatColor());
		assertEquals(raceChatFormat, sut.getRaceChatFormat());
		assertEquals(holderTag, sut.getTag());
		assertTrue(sut.getPermissions().getPermissions().isEmpty());
		assertEquals(0, sut.getArmorPerms().size());
		
		assertEquals(1, sut.getTraits().size());
		assertTrue(sut.getParsingExceptionsHappened().isEmpty());
		
	}

}

package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaBar;
import com.nisovin.magicspells.mana.ManaChangeReason;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaFoodBarRunner;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;

public class MagicSpellsManaManager implements ManaManager {

	/**
	 * The player to use.
	 */
	private final RaCPlayer player;
	
	/**
	 * The runner for the Mana food bar.
	 */
	private final ManaFoodBarRunner foodBar;
	
	/**
	 * The plugin to use.
	 */
	private final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	
	public MagicSpellsManaManager(RaCPlayer player) {
		this.player = player;
		
		foodBar = new ManaFoodBarRunner(this);
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useFoodManaBar()){
			foodBar.start();
		}
	}

	@Override
	public void rescanPlayer() {
	}

	@Override
	public void outputManaToPlayer() {
	}

	@Override
	public boolean playerCastSpell(MagicSpellTrait spellToCast) {
		if(!hasEnoughMana(spellToCast)) return false;
		return MagicSpells.getManaHandler().removeMana(player.getPlayer(), (int)spellToCast.getCost(), ManaChangeReason.SPELL_COST);
	}

	@Override
	public boolean hasEnoughMana(MagicSpellTrait spell) {
		return hasEnoughMana(spell.getCost());
	}

	@Override
	public double fillMana(double value) {
		MagicSpells.getManaHandler().addMana(player.getPlayer(), (int)value, ManaChangeReason.OTHER);
		return getCurrentMana();
	}

	@Override
	public double drownMana(double value) {
		MagicSpells.getManaHandler().removeMana(player.getPlayer(), (int)value, ManaChangeReason.OTHER);
		return getCurrentMana();
	}

	@Override
	public boolean hasEnoughMana(double manaNeeded) {
		return MagicSpells.getManaHandler().hasMana(player.getPlayer(), (int)manaNeeded);
	}

	@Override
	public double getMaxMana() {
		return MagicSpells.getManaHandler().getMaxMana(player.getPlayer());
	}

	@Override
	public double getCurrentMana() {
		ManaBar bar = getBar();
		if(bar == null) return 0;
		
		return bar.getMana();
	}

	@Override
	public void addMaxManaBonus(String key, double value) {
		//not supported here.
	}

	@Override
	public void removeMaxManaBonus(String key) {
		//not supported here.
	}

	@Override
	public Map<String, Double> getAllBonuses() {
		return new HashMap<String, Double>();
	}

	@Override
	public RaCPlayer getPlayer() {
		return player;
	}

	@Override
	public boolean isManaFull() {
		return getCurrentMana() >= getMaxMana();
	}
	
	
	private ManaBar getBar(){
		try{
			Method method = MagicSpells.getManaHandler().getClass().getDeclaredMethod("getManaBar", Player.class);
			return (ManaBar) method.invoke(MagicSpells.getManaHandler(), player.getPlayer());
		}catch(Throwable exp){ return null; }
	}
	
}

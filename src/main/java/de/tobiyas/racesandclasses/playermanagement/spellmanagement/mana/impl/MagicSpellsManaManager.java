package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaBar;
import com.nisovin.magicspells.mana.ManaChangeReason;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaFoodBarRunner;

public class MagicSpellsManaManager extends AbstractManaManager {

	
	/**
	 * The runner for the Mana food bar.
	 */
	private final ManaFoodBarRunner foodBar;	
	
	
	public MagicSpellsManaManager(RaCPlayer racPlayer) {
		super(racPlayer);
		
		foodBar = new ManaFoodBarRunner(this);
		foodBar.start();
	}


	@Override
	public void outputManaToPlayer() {
	}



	@Override
	public double fillMana(double value) {
		MagicSpells.getManaHandler().addMana(racPlayer.getPlayer(), (int)value, ManaChangeReason.OTHER);
		return getCurrentMana();
	}

	@Override
	public double drownMana(double value) {
		MagicSpells.getManaHandler().removeMana(racPlayer.getPlayer(), (int)value, ManaChangeReason.OTHER);
		return getCurrentMana();
	}


	@Override
	public double getMaxMana() {
		return MagicSpells.getManaHandler().getMaxMana(racPlayer.getPlayer());
	}

	@Override
	public double getCurrentMana() {
		ManaBar bar = getBar();
		if(bar == null) return 0;
		
		return bar.getMana();
	}

	
	
	private ManaBar getBar(){
		try{
			Method method = MagicSpells.getManaHandler().getClass().getDeclaredMethod("getManaBar", Player.class);
			method.setAccessible(true);
			return (ManaBar) method.invoke(MagicSpells.getManaHandler(), racPlayer.getPlayer());
		}catch(Throwable exp){ 
			exp.printStackTrace();
			return null; 
		}
	}


	@Override
	protected void applyMaxManaBonus(double bonus) {}
	
}

package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.BlockDisguiseTrait;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.player.PlayerUtils;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class DisguiseData {

	/**
	 * The Player that used it.
	 */
	private final RaCPlayer player;
	
	/**
	 * The location used.
	 */
	private final Location location;
	
	/**
	 * The old Material.
	 */
	private final Material oldMaterial;
	
	/**
	 * The old damage Value.
	 */
	private final byte oldDamage;
	
	/**
	 * The fake material
	 */
	private final Material fakeMaterial;
	
	/**
	 * The Fake data to use.
	 */
	private final byte fakeData;
	
	
	/**
	 * If still valid.
	 */
	private boolean valid = true;
	
	/**
	 * Tick for ticking stuff modulo.
	 */
	private int tick = 0;
	
	
	@SuppressWarnings("deprecation")
	public DisguiseData(RaCPlayer player, Block oldBlock, Material fakeMaterial, byte fakeData) {
		this.player = player;
		this.location = player.getLocation().getBlock().getLocation();
		this.oldMaterial = oldBlock.getType();
		this.oldDamage = oldBlock.getData();
		
		this.fakeMaterial = fakeMaterial;
		this.fakeData = fakeData;
		
		//Set as Cobweb to be sure.
		oldBlock.setType(Material.WEB);
		
		//Send fake data at start.
		setInvisToAll();
		sendFakeBlockToAll();
	}
	
	
	/**
	 * Ticks the Disguise.
	 */
	public DisguiseData tick(){
		if(!valid) return this;
		
		//Check if player offline:
		if(!player.isOnline()){
			this.valid = false;
			return this;
		}
		
		//Check if broken:
		if(location.getBlock().getType() == Material.AIR){
			this.valid = false;
			return this;
		}
		
		//check for player moved.
		if(!location.getBlock().equals(player.getLocation().getBlock())){
			this.valid = false;
			return this;
		}
		
		
		//Send fake stuff:
		sendFakeBlockToAll();
		setInvisToAll();
		if(tick % 2 == 0) sendParticlesToAll();
		
		tick++;
		return this;
	}
	
	
	/**
	 * Sends a fake block to all players except self.
	 */
	@SuppressWarnings("deprecation")
	private void sendFakeBlockToAll() {
		Player player = this.player.getRealPlayer();
		
		for(Player pl : PlayerUtils.getOnlinePlayers()) {
			if(player.isOnline() && player.getPlayer() == pl) continue;
			pl.sendBlockChange(location, fakeMaterial, fakeData);
		}
	}


	/**
	 * Sends some awsome particles.
	 */
	private void sendParticlesToAll() {
		VollotileCodeManager.getVollotileCode().sendParticleEffectToAll(
				ParticleEffects.TOWN_AURA, 
				location.clone().add(Math.random() - 0.5, Math.random() + 0.5, Math.random() - 0.5), 
				new Vector((Math.random()* 0.2) - 0.1,0.1, (Math.random()* 0.2) - 0.1), 0, 1);
	}


	/**
	 * Checks if still valid.
	 * @return
	 */
	public boolean isStillValid(){
		return valid;
	}
	
	
	/**
	 * Restores the old block.
	 */
	@SuppressWarnings("deprecation")
	public void restoreOld(){
		Block block = location.getBlock();
		block.setType(oldMaterial);
		block.setData(oldDamage);
		
		//Send block restore to all:
		for(Player pl : PlayerUtils.getOnlinePlayers()) {
			pl.sendBlockChange(location, oldMaterial, oldDamage);
			pl.showPlayer(player.getPlayer());
		}
		
		setVisibleToAll();
	}

	
	private void setInvisToAll(){
		if(!player.isOnline()) return;
		Player ownPlayer = player.getRealPlayer();
		
		for(Player player : PlayerUtils.getOnlinePlayers()){
			player.hidePlayer(ownPlayer);
		}
	}
	
	
	private void setVisibleToAll(){
		if(!player.isOnline()) return;
		Player ownPlayer = player.getRealPlayer();
		
		for(Player player : PlayerUtils.getOnlinePlayers()){
			player.showPlayer(ownPlayer);
		}
	}
	
	

	public RaCPlayer getPlayer() {
		return player;
	}
	
	
	public Location getLocation() {
		return location;
	}
}

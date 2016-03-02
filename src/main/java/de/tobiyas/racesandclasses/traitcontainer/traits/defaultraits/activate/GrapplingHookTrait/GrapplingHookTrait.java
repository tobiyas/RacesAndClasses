/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.GrapplingHookTrait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.schedule.DebugBukkitRunnable;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class GrapplingHookTrait extends AbstractBasicTrait{
	
	private Material materialToUse = Material.ARROW;
	
	private final Map<Projectile, UUID> launchMap = new HashMap<Projectile, UUID>();
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class, ProjectileHitEvent.class})
	@Override
	public void generalInit(){
		plugin.registerEvents(this);
	}

	@Override
	public String getName() {
		return "GrapplingHookTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		return "fire a Grappling hook.";
	}

	@TraitConfigurationNeeded(fields = {
				@TraitConfigurationField(fieldName = "material", classToExpect = Material.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		materialToUse = configMap.getAsMaterial("material");
	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   
		Event event = eventWrapper.getEvent();
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			Player player = Eevent.getPlayer();
			
			//If pulling -> return.
			if(pulling.contains(player.getUniqueId())) return TraitResults.False();
			
			Arrow projectile = player.launchProjectile(Arrow.class);
			launchMap.put(projectile, player.getUniqueId());
			return TraitResults.True();
		}
		
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent hitEvent = ((ProjectileHitEvent)event);
			Projectile projectile = hitEvent.getEntity();
			
			Player player = Bukkit.getPlayer(launchMap.get(projectile));
			
			launchMap.remove(projectile);
			projectile.remove();
			
			//Just sanity Check.
			if(pulling.contains(player.getUniqueId())) return TraitResults.False();

			//Only if online!
			if(player.isOnline()) pullPlayerTo(player, projectile.getLocation());
		}
		
		return TraitResults.True();
	}
	
	
	/**
	 * The Set of pulling people.
	 */
	private Set<UUID> pulling = new HashSet<>();
	
	/**
	 * If currently teleporting.
	 */
	private boolean isGrapplingTeleport = false;
	
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event){
		if(isGrapplingTeleport) return;
		if(event.getFrom().distance(event.getTo()) < 0.1) return;
		
		Player player = event.getPlayer();
		if(pulling.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent event){
		Player pl = event.getPlayer();
		
		//Small quickfix for Leaving players still have fly.
		if(pulling.contains(pl.getUniqueId())){
			pl.setAllowFlight(false);
			pl.setFlying(false);
		}
	}
	
	
	private void pullPlayerTo(final Player player, final Location location) {
		final boolean isFlying = player.isFlying();
		final boolean allowFlying = player.getAllowFlight();
		if(!isFlying) { player.setFlying(true); player.setAllowFlight(true);}
		pulling.add(player.getUniqueId());
		
		
		BukkitRunnable runnable = new DebugBukkitRunnable("GrapplingHookPull") {
			int i = 0;
			double dist = 100000;
			
			@Override
			public void runIntern() {
				//Have a safety abort:
				if(i++>100) {stopIt(); return;}
				
				
				//If gone offline -> set flying false!
				if(!player.isOnline() || player.isDead()) {
					stopIt();
					return;
				}
				
				//Check if near enough:
				Location playerLocation = player.getLocation();
				double curDist = playerLocation.distance(location);
				if(Math.abs(curDist - dist) < 0.4){
					stopIt();
					return;
				}
				
				dist = curDist;
				if(playerLocation.distance(location) < 1 || launchMap.values().contains(player.getName())){
					stopIt();
					return;
				}
				
				//Calculate the Direction:
				Vector vec = location.toVector().subtract(playerLocation.toVector());
				vec.normalize().multiply(1.4);
				
				//Now check to be sure to not get into a solid block:
				Location teleportLocation = playerLocation.add(vec);
				Location up = teleportLocation.clone().add(0,2,0);
				if(up.getBlock().getType().isSolid()){
					//We will never go into a solid block!
					stopIt();
					return;
				}
				
				//Finally teleport.
				isGrapplingTeleport = true;
				player.teleport(teleportLocation);
				isGrapplingTeleport = false;
				
				player.setFlying(true);
				
				//every 4th tick -> do fancy particles.
				if(i % 2 == 0){
					List<Location> between = getBetween(playerLocation, location, 10);
					for(Location loc : between){
						VollotileCodeManager.getVollotileCode().sendParticleEffectToAll(
								ParticleEffects.CRIT, loc, new Vector(0,0,0), 0, 3);
					}
				}
			}
			
			private void stopIt(){
				pulling.remove(player.getUniqueId());
				cancel();
				
				try{ if(!isFlying) player.setFlying(false); }catch(Throwable exp){}
				try{ if(!allowFlying) player.setAllowFlight(false); }catch(Throwable exp){}
			}
			
			
			private List<Location> getBetween(Location start, Location end, int amount){
				start = start.clone();
				end = end.clone();
				
				Vector vec = end.toVector().subtract(start.toVector());
				vec = vec.divide(new Vector(amount,amount,amount));
				List<Location> locs = new LinkedList<Location>();
				for(int i = 1; i < amount; i++){
					start.add(vec);
					locs.add(start.clone());
				}
				
				return locs;
			}
		};
		
		runnable.runTaskTimer(plugin, 2, 2);
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "You fire a grappling hook which lifts you.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}
	
	@TraitInfos(category="activate", traitName="GrapplingHookTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent(); //TODO fix sometime...
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			Player player = Eevent.getPlayer();
			UUID id = player.getUniqueId();
			
			if(player.getItemInHand().getType() == materialToUse
					&& Eevent.getAction() == Action.RIGHT_CLICK_AIR
					&& !launchMap.values().contains(id)
					&& !pulling.contains(id)) {
				return true;
			}
		}
		
		if(event instanceof ProjectileHitEvent){
			Projectile projectile = ((ProjectileHitEvent) event).getEntity();
			
			if(launchMap.containsKey(projectile)){
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public boolean isBindable() {
		return true;
	}
	
	
	@Override
	protected TraitResults bindCastIntern(RaCPlayer player) {
		EventWrapper event = EventWrapperFactory.buildFromEvent(new PlayerInteractEvent(player.getPlayer(), null, null, null, null));
		return trigger(event);
	}
	
}

package de.tobiyas.racesandclasses.playermanagement.player;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public abstract class PlayerProxy implements Player {

	public PlayerProxy() {
	}
	
	
	/**
	 * Returns the Player
	 */
	public abstract Player getRealPlayer();
	

	@Override
	public String getName() {
		return getRealPlayer().getName();
	}

	@Override
	public PlayerInventory getInventory() {
		return getRealPlayer().getInventory();
	}

	@Override
	public Inventory getEnderChest() {
		return getRealPlayer().getEnderChest();
	}

	@Override
	public boolean setWindowProperty(Property prop, int value) {
		return getRealPlayer().setWindowProperty(prop, value);
	}

	@Override
	public InventoryView getOpenInventory() {
		return getRealPlayer().getOpenInventory();
	}

	@Override
	public InventoryView openInventory(Inventory inventory) {
		return getRealPlayer().openInventory(inventory);
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean force) {
		return getRealPlayer().openWorkbench(location, force);
	}

	@Override
	public InventoryView openEnchanting(Location location, boolean force) {
		return getRealPlayer().openEnchanting(location, force);
	}

	@Override
	public void openInventory(InventoryView inventory) {
		getRealPlayer().openInventory(inventory);
	}

	@Override
	public void closeInventory() {
		getRealPlayer().closeInventory();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInHand() {
		return getRealPlayer().getItemInHand();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setItemInHand(ItemStack item) {
		getRealPlayer().setItemInHand(item);
	}

	@Override
	public ItemStack getItemOnCursor() {
		return getRealPlayer().getItemOnCursor();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setItemOnCursor(ItemStack item) {
		getRealPlayer().setItemInHand(item);
	}

	@Override
	public boolean isSleeping() {
		return getRealPlayer().isSleeping();
	}

	@Override
	public int getSleepTicks() {
		return getRealPlayer().getSleepTicks();
	}

	@Override
	public GameMode getGameMode() {
		return getRealPlayer().getGameMode();
	}

	@Override
	public void setGameMode(GameMode mode) {
		getRealPlayer().setGameMode(mode);
	}

	@Override
	public boolean isBlocking() {
		return getRealPlayer().isBlocking();
	}

	@Override
	public int getExpToLevel() {
		return getRealPlayer().getExpToLevel();
	}

	@Override
	public double getEyeHeight() {
		return getRealPlayer().getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean ignoreSneaking) {
		return getRealPlayer().getEyeHeight(ignoreSneaking);
	}

	@Override
	public Location getEyeLocation() {
		return getRealPlayer().getEyeLocation();
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
		return getRealPlayer().getLineOfSight(transparent, maxDistance);
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
		return getRealPlayer().getLineOfSight(transparent, maxDistance);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
		return getRealPlayer().getTargetBlock(transparent, maxDistance);
	}

	@Override
	public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
		return getRealPlayer().getTargetBlock(transparent, maxDistance);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent,
			int maxDistance) {
		return getRealPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> transparent,
			int maxDistance) {
		return getRealPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
	}

	//@Override
	public Egg throwEgg() {
		return getRealPlayer().launchProjectile(Egg.class);
	}

	//@Override
	public Snowball throwSnowball() {
		return getRealPlayer().launchProjectile(Snowball.class);
	}

	//@Override
	public Arrow shootArrow() {
		return getRealPlayer().launchProjectile(Arrow.class);
	}

	@Override
	public int getRemainingAir() {
		return getRealPlayer().getRemainingAir();
	}

	@Override
	public void setRemainingAir(int ticks) {
		getRealPlayer().setRemainingAir(ticks);
	}

	@Override
	public int getMaximumAir() {
		return getRealPlayer().getMaximumAir();
	}

	@Override
	public void setMaximumAir(int ticks) {
		getRealPlayer().setMaximumAir(ticks);
	}

	@Override
	public int getMaximumNoDamageTicks() {
		return getRealPlayer().getMaximumNoDamageTicks();
	}

	@Override
	public void setMaximumNoDamageTicks(int ticks) {
		getRealPlayer().setMaximumNoDamageTicks(ticks);
	}

	@Override
	public double getLastDamage() {
		return getRealPlayer().getLastDamage();
	}

	@Override
	public int _INVALID_getLastDamage() {
		return (int) getRealPlayer().getLastDamage();
	}

	@Override
	public void setLastDamage(double damage) {
		getRealPlayer().setLastDamage(damage);
	}

	@Override
	public void _INVALID_setLastDamage(int damage) {
		getRealPlayer().setLastDamage(damage);
	}

	@Override
	public int getNoDamageTicks() {
		return getRealPlayer().getNoDamageTicks();
	}

	@Override
	public void setNoDamageTicks(int ticks) {
		getRealPlayer().setNoDamageTicks(ticks);
	}

	@Override
	public Player getKiller() {
		return getRealPlayer().getKiller();
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect) {
		return getRealPlayer().addPotionEffect(effect);
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect, boolean force) {
		return getRealPlayer().addPotionEffect(effect, force);
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> effects) {
		return getRealPlayer().addPotionEffects(effects);
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return getRealPlayer().hasPotionEffect(type);
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		getRealPlayer().removePotionEffect(type);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		return getRealPlayer().getActivePotionEffects();
	}

	@Override
	public boolean hasLineOfSight(Entity other) {
		return getRealPlayer().hasLineOfSight(other);
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return getRealPlayer().getRemoveWhenFarAway();
	}

	@Override
	public void setRemoveWhenFarAway(boolean remove) {
		getRealPlayer().setRemoveWhenFarAway(remove);
	}

	@Override
	public EntityEquipment getEquipment() {
		return getRealPlayer().getEquipment();
	}

	@Override
	public void setCanPickupItems(boolean pickup) {
		getRealPlayer().setCanPickupItems(pickup);
	}

	@Override
	public boolean getCanPickupItems() {
		return getRealPlayer().getCanPickupItems();
	}

	@Override
	public boolean isLeashed() {
		return getRealPlayer().isLeashed();
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		return getRealPlayer().getLeashHolder();
	}

	@Override
	public boolean setLeashHolder(Entity holder) {
		return getRealPlayer().setLeashHolder(holder);
	}

	@Override
	public Location getLocation() {
		return getRealPlayer().getLocation();
	}

	@Override
	public Location getLocation(Location loc) {
		return getRealPlayer().getLocation(loc);
	}

	@Override
	public void setVelocity(Vector velocity) {
		getRealPlayer().setVelocity(velocity);
	}

	@Override
	public Vector getVelocity() {
		return getRealPlayer().getVelocity();
	}

	@Override
	public World getWorld() {
		return getRealPlayer().getWorld();
	}

	@Override
	public boolean teleport(Location location) {
		return getRealPlayer().teleport(location);
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		return getRealPlayer().teleport(location, cause);
	}

	@Override
	public boolean teleport(Entity destination) {
		return getRealPlayer().teleport(destination);
	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		return getRealPlayer().teleport(destination, cause);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		return getRealPlayer().getNearbyEntities(x, y, z);
	}

	@Override
	public int getEntityId() {
		return getRealPlayer().getEntityId();
	}

	@Override
	public int getFireTicks() {
		return getRealPlayer().getFireTicks();
	}

	@Override
	public int getMaxFireTicks() {
		return getRealPlayer().getMaxFireTicks();
	}

	@Override
	public void setFireTicks(int ticks) {
		getRealPlayer().setFireTicks(ticks);
	}

	@Override
	public void remove() {
		getRealPlayer().remove();
	}

	@Override
	public boolean isDead() {
		return getRealPlayer().isDead();
	}

	@Override
	public boolean isValid() {
		return getRealPlayer().isValid();
	}

	@Override
	public Server getServer() {
		return getRealPlayer().getServer();
	}

	@Override
	public Entity getPassenger() {
		return getRealPlayer().getPassenger();
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		return getRealPlayer().setPassenger(passenger);
	}

	@Override
	public boolean isEmpty() {
		return getRealPlayer().isEmpty();
	}

	@Override
	public boolean eject() {
		return getRealPlayer().eject();
	}

	@Override
	public float getFallDistance() {
		return getRealPlayer().getFallDistance();
	}

	@Override
	public void setFallDistance(float distance) {
		getRealPlayer().setFallDistance(distance);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent event) {
		getRealPlayer().setLastDamageCause(event);
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return getRealPlayer().getLastDamageCause();
	}

	@Override
	public UUID getUniqueId() {
		return getRealPlayer().getUniqueId();
	}

	@Override
	public int getTicksLived() {
		return getRealPlayer().getTicksLived();
	}

	@Override
	public void setTicksLived(int value) {
		getRealPlayer().setTicksLived(value);
	}

	@Override
	public void playEffect(EntityEffect type) {
		getRealPlayer().playEffect(type);
	}

	@Override
	public EntityType getType() {
		return getRealPlayer().getType();
	}

	@Override
	public boolean isInsideVehicle() {
		return getRealPlayer().isInsideVehicle();
	}

	@Override
	public boolean leaveVehicle() {
		return getRealPlayer().leaveVehicle();
	}

	@Override
	public Entity getVehicle() {
		return getRealPlayer().getVehicle();
	}

	@Override
	public void setCustomName(String name) {
		getRealPlayer().setCustomName(name);
	}

	@Override
	public String getCustomName() {
		return getRealPlayer().getCustomName();
	}

	@Override
	public void setCustomNameVisible(boolean flag) {
		getRealPlayer().setCustomNameVisible(flag);
	}

	@Override
	public boolean isCustomNameVisible() {
		return getRealPlayer().isCustomNameVisible();
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		getRealPlayer().setMetadata(metadataKey, newMetadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return getRealPlayer().getMetadata(metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return getRealPlayer().hasMetadata(metadataKey);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		getRealPlayer().removeMetadata(metadataKey, owningPlugin);
	}

	@Override
	public void sendMessage(String message) {
		getRealPlayer().sendMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		getRealPlayer().sendMessage(messages);
	}

	@Override
	public boolean isPermissionSet(String name) {
		return getRealPlayer().isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return getRealPlayer().isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(String name) {
		return getRealPlayer().hasPermission(name);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return getRealPlayer().hasPermission(perm);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value) {
		return getRealPlayer().addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return getRealPlayer().addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value, int ticks) {
		return getRealPlayer().addAttachment(plugin, name, value, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return getRealPlayer().addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		getRealPlayer().removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		getRealPlayer().recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return getRealPlayer().getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return getRealPlayer().isOp();
	}

	@Override
	public void setOp(boolean value) {
		getRealPlayer().setOp(value);
	}

	@Override
	public void damage(double amount) {
		getRealPlayer().damage(amount);
	}

	@Override
	public void _INVALID_damage(int amount) {
		getRealPlayer().damage(amount);
	}

	@Override
	public void damage(double amount, Entity source) {
		getRealPlayer().damage(amount, source);
	}

	@Override
	public void _INVALID_damage(int amount, Entity source) {
		getRealPlayer().damage(amount, source);
	}

	@Override
	public double getHealth() {
		return getRealPlayer().getHealth();
	}

	@Override
	public int _INVALID_getHealth() {
		return (int) getRealPlayer().getHealth();
	}

	@Override
	public void setHealth(double health) {
		getRealPlayer().setHealth(health);
	}

	@Override
	public void _INVALID_setHealth(int health) {
		getRealPlayer().setHealth(health);
	}

	@Override
	public double getMaxHealth() {
		return getRealPlayer().getMaxHealth();
	}

	@Override
	public int _INVALID_getMaxHealth() {
		return (int) getRealPlayer().getMaxHealth();
	}

	@Override
	public void setMaxHealth(double health) {
		getRealPlayer().setMaxHealth(health);
	}

	@Override
	public void _INVALID_setMaxHealth(int health) {
		getRealPlayer().setMaxHealth(health);
	}

	@Override
	public void resetMaxHealth() {
		getRealPlayer().resetMaxHealth();
	}

	@Override
	public <T extends Projectile> T launchProjectile(
			Class<? extends T> projectile) {
		return getRealPlayer().launchProjectile(projectile);
	}

	@Override
	public <T extends Projectile> T launchProjectile(
			Class<? extends T> projectile, Vector velocity) {
		return getRealPlayer().launchProjectile(projectile, velocity);
	}

	@Override
	public boolean isConversing() {
		return getRealPlayer().isConversing();
	}

	@Override
	public void acceptConversationInput(String input) {
		getRealPlayer().acceptConversationInput(input);
	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return getRealPlayer().beginConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation) {
		getRealPlayer().abandonConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation,
			ConversationAbandonedEvent details) {
		getRealPlayer().abandonConversation(conversation, details);
	}

	@Override
	public boolean isOnline() {
		return getRealPlayer().isOnline();
	}

	@Override
	public boolean isBanned() {
		return getRealPlayer().isBanned();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBanned(boolean banned) {
		getRealPlayer().setBanned(banned);
	}

	@Override
	public boolean isWhitelisted() {
		return getRealPlayer().isWhitelisted();
	}

	@Override
	public void setWhitelisted(boolean value) {
		getRealPlayer().setWhitelisted(value);
	}

	@Override
	public Player getPlayer() {
		return getRealPlayer().getPlayer();
	}

	@Override
	public long getFirstPlayed() {
		return getRealPlayer().getFirstPlayed();
	}

	@Override
	public long getLastPlayed() {
		return getRealPlayer().getLastPlayed();
	}

	@Override
	public boolean hasPlayedBefore() {
		return getRealPlayer().hasPlayedBefore();
	}

	@Override
	public Map<String, Object> serialize() {
		return getRealPlayer().serialize();
	}

	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		getRealPlayer().sendPluginMessage(source, channel, message);
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return getRealPlayer().getListeningPluginChannels();
	}

	@Override
	public String getDisplayName() {
		return getRealPlayer().getDisplayName();
	}

	@Override
	public void setDisplayName(String name) {
		getRealPlayer().setDisplayName(name);
	}

	@Override
	public String getPlayerListName() {
		return getRealPlayer().getPlayerListName();
	}

	@Override
	public void setPlayerListName(String name) {
		getRealPlayer().setPlayerListName(name);
	}

	@Override
	public void setCompassTarget(Location loc) {
		getRealPlayer().setCompassTarget(loc);
	}

	@Override
	public Location getCompassTarget() {
		return getRealPlayer().getCompassTarget();
	}

	@Override
	public InetSocketAddress getAddress() {
		return getRealPlayer().getAddress();
	}

	@Override
	public void sendRawMessage(String message) {
		getRealPlayer().sendRawMessage(message);
	}

	@Override
	public void kickPlayer(String message) {
		getRealPlayer().kickPlayer(message);
	}

	@Override
	public void chat(String msg) {
		getRealPlayer().chat(msg);
	}

	@Override
	public boolean performCommand(String command) {
		return getRealPlayer().performCommand(command);
	}

	@Override
	public boolean isSneaking() {
		return getRealPlayer().isSneaking();
	}

	@Override
	public void setSneaking(boolean sneak) {
		getRealPlayer().setSneaking(sneak);
	}

	@Override
	public boolean isSprinting() {
		return getRealPlayer().isSprinting();
	}

	@Override
	public void setSprinting(boolean sprinting) {
		getRealPlayer().setSprinting(sprinting);
	}

	@Override
	public void saveData() {
		getRealPlayer().saveData();
	}

	@Override
	public void loadData() {
		getRealPlayer().loadData();
	}

	@Override
	public void setSleepingIgnored(boolean isSleeping) {
		getRealPlayer().setSleepingIgnored(isSleeping);
	}

	@Override
	public boolean isSleepingIgnored() {
		return getRealPlayer().isSleepingIgnored();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void playNote(Location loc, byte instrument, byte note) {
		getRealPlayer().playNote(loc, instrument, note);
	}

	@Override
	public void playNote(Location loc, Instrument instrument, Note note) {
		getRealPlayer().playNote(loc, instrument, note);
	}

	@Override
	public void playSound(Location location, Sound sound, float volume,
			float pitch) {
		getRealPlayer().playSound(location, sound, volume, pitch);
	}

	@Override
	public void playSound(Location location, String sound, float volume,
			float pitch) {
		getRealPlayer().playSound(location, sound, volume, pitch);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void playEffect(Location loc, Effect effect, int data) {
		getRealPlayer().playEffect(loc, effect, data);
	}

	@Override
	public <T> void playEffect(Location loc, Effect effect, T data) {
		getRealPlayer().playEffect(loc, effect, data);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendBlockChange(Location loc, Material material, byte data) {
		getRealPlayer().sendBlockChange(loc, material, data);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean sendChunkChange(Location loc, int sx, int sy, int sz,
			byte[] data) {
		return getRealPlayer().sendChunkChange(loc, sx, sy, sz, data);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendBlockChange(Location loc, int material, byte data) {
		getRealPlayer().sendBlockChange(loc, material, data);
	}

	@Override
	public void sendSignChange(Location loc, String[] lines)
			throws IllegalArgumentException {
		getRealPlayer().sendSignChange(loc, lines);
	}

	@Override
	public void sendMap(MapView map) {
		getRealPlayer().sendMap(map);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateInventory() {
		getRealPlayer().updateInventory();
	}

	@Override
	public void awardAchievement(Achievement achievement) {
		getRealPlayer().awardAchievement(achievement);
	}

	@Override
	public void removeAchievement(Achievement achievement) {
		getRealPlayer().removeAchievement(achievement);
	}

	@Override
	public boolean hasAchievement(Achievement achievement) {
		return getRealPlayer().hasAchievement(achievement);
	}

	@Override
	public void incrementStatistic(Statistic statistic)
			throws IllegalArgumentException {
		getRealPlayer().incrementStatistic(statistic);
	}

	@Override
	public void decrementStatistic(Statistic statistic)
			throws IllegalArgumentException {
		getRealPlayer().decrementStatistic(statistic);
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount)
			throws IllegalArgumentException {
		getRealPlayer().incrementStatistic(statistic, amount);
	}

	@Override
	public void decrementStatistic(Statistic statistic, int amount)
			throws IllegalArgumentException {
		getRealPlayer().decrementStatistic(statistic, amount);
	}

	@Override
	public void setStatistic(Statistic statistic, int newValue)
			throws IllegalArgumentException {
		getRealPlayer().setStatistic(statistic, newValue);
	}

	@Override
	public int getStatistic(Statistic statistic)
			throws IllegalArgumentException {
		return getRealPlayer().getStatistic(statistic);
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material)
			throws IllegalArgumentException {
		getRealPlayer().incrementStatistic(statistic, material);
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material)
			throws IllegalArgumentException {
		getRealPlayer().decrementStatistic(statistic, material);
	}

	@Override
	public int getStatistic(Statistic statistic, Material material)
			throws IllegalArgumentException {
		return getRealPlayer().getStatistic(statistic, material);
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material,
			int amount) throws IllegalArgumentException {
		getRealPlayer().incrementStatistic(statistic, material, amount);
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material,
			int amount) throws IllegalArgumentException {
		getRealPlayer().decrementStatistic(statistic, material, amount);
	}

	@Override
	public void setStatistic(Statistic statistic, Material material,
			int newValue) throws IllegalArgumentException {
		getRealPlayer().setStatistic(statistic, material, newValue);
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType)
			throws IllegalArgumentException {
		getRealPlayer().incrementStatistic(statistic, entityType);
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType)
			throws IllegalArgumentException {
		getRealPlayer().decrementStatistic(statistic, entityType);
	}

	@Override
	public int getStatistic(Statistic statistic, EntityType entityType)
			throws IllegalArgumentException {
		return getRealPlayer().getStatistic(statistic, entityType);
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType,
			int amount) throws IllegalArgumentException {
		getRealPlayer().incrementStatistic(statistic, entityType, amount);
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType,
			int amount) {
		getRealPlayer().decrementStatistic(statistic, entityType, amount);
	}

	@Override
	public void setStatistic(Statistic statistic, EntityType entityType,
			int newValue) {
		getRealPlayer().setStatistic(statistic, entityType, newValue);
	}

	@Override
	public void setPlayerTime(long time, boolean relative) {
		getRealPlayer().setPlayerTime(time, relative);
	}

	@Override
	public long getPlayerTime() {
		return getRealPlayer().getPlayerTime();
	}

	@Override
	public long getPlayerTimeOffset() {
		return getRealPlayer().getPlayerTimeOffset();
	}

	@Override
	public boolean isPlayerTimeRelative() {
		return getRealPlayer().isPlayerTimeRelative();
	}

	@Override
	public void resetPlayerTime() {
		getRealPlayer().resetPlayerTime();
	}

	@Override
	public void setPlayerWeather(WeatherType type) {
		getRealPlayer().setPlayerWeather(type);
	}

	@Override
	public WeatherType getPlayerWeather() {
		return getRealPlayer().getPlayerWeather();
	}

	@Override
	public void resetPlayerWeather() {
		getRealPlayer().resetPlayerWeather();
	}

	@Override
	public void giveExp(int amount) {
		getRealPlayer().giveExp(amount);
	}

	@Override
	public void giveExpLevels(int amount) {
		getRealPlayer().giveExpLevels(amount);
	}

	@Override
	public float getExp() {
		return getRealPlayer().getExp();
	}

	@Override
	public void setExp(float exp) {
		getRealPlayer().setExp(exp);
	}

	@Override
	public int getLevel() {
		return getRealPlayer().getLevel();
	}

	@Override
	public void setLevel(int level) {
		getRealPlayer().setLevel(level);
	}

	@Override
	public int getTotalExperience() {
		return getRealPlayer().getTotalExperience();
	}

	@Override
	public void setTotalExperience(int exp) {
		getRealPlayer().setTotalExperience(exp);
	}

	@Override
	public float getExhaustion() {
		return getRealPlayer().getExhaustion();
	}

	@Override
	public void setExhaustion(float value) {
		getRealPlayer().setExhaustion(value);
	}

	@Override
	public float getSaturation() {
		return getRealPlayer().getSaturation();
	}

	@Override
	public void setSaturation(float value) {
		getRealPlayer().setSaturation(value);
	}

	@Override
	public int getFoodLevel() {
		return getRealPlayer().getFoodLevel();
	}

	@Override
	public void setFoodLevel(int value) {
		getRealPlayer().setFoodLevel(value);
	}

	@Override
	public Location getBedSpawnLocation() {
		return getRealPlayer().getBedSpawnLocation();
	}

	@Override
	public void setBedSpawnLocation(Location location) {
		getRealPlayer().setBedSpawnLocation(location);
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean force) {
		getRealPlayer().setBedSpawnLocation(location, force);
	}

	@Override
	public boolean getAllowFlight() {
		return getRealPlayer().getAllowFlight();
	}

	@Override
	public void setAllowFlight(boolean flight) {
		getRealPlayer().setAllowFlight(flight);
	}

	@Override
	public void hidePlayer(Player player) {
		getRealPlayer().hidePlayer(player);
	}

	@Override
	public void showPlayer(Player player) {
		getRealPlayer().showPlayer(player);
	}

	@Override
	public boolean canSee(Player player) {
		return getRealPlayer().canSee(player);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOnGround() {
		return getRealPlayer().isOnGround();
	}

	@Override
	public boolean isFlying() {
		return getRealPlayer().isFlying();
	}

	@Override
	public void setFlying(boolean value) {
		getRealPlayer().setFlying(value);
	}

	@Override
	public void setFlySpeed(float value) throws IllegalArgumentException {
		getRealPlayer().setFlySpeed(value);
	}

	@Override
	public void setWalkSpeed(float value) throws IllegalArgumentException {
		getRealPlayer().setWalkSpeed(value);
	}

	@Override
	public float getFlySpeed() {
		return getRealPlayer().getFlySpeed();
	}

	@Override
	public float getWalkSpeed() {
		return getRealPlayer().getWalkSpeed();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setTexturePack(String url) {
		getRealPlayer().setTexturePack(url);
	}

	@Override
	public void setResourcePack(String url) {
		getRealPlayer().setResourcePack(url);
	}

	@Override
	public Scoreboard getScoreboard() {
		return getRealPlayer().getScoreboard();
	}

	@Override
	public void setScoreboard(Scoreboard scoreboard)
			throws IllegalArgumentException, IllegalStateException {
		getRealPlayer().setScoreboard(scoreboard);
	}

	@Override
	public boolean isHealthScaled() {
		return getRealPlayer().isHealthScaled();
	}

	@Override
	public void setHealthScaled(boolean scale) {
		getRealPlayer().setHealthScaled(scale);
	}

	@Override
	public void setHealthScale(double scale) throws IllegalArgumentException {
		getRealPlayer().setHealthScale(scale);
	}

	@Override
	public double getHealthScale() {
		return getRealPlayer().getHealthScale();
	}

	@Override
	public Entity getSpectatorTarget() {
		return getRealPlayer().getSpectatorTarget();
	}

	@Override
	public void setSpectatorTarget(Entity entity) {
		getRealPlayer().setSpectatorTarget(entity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendTitle(String title, String subtitle) {
		getRealPlayer().sendTitle(title, subtitle);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void resetTitle() {
		getRealPlayer().resetTitle();
	}

	
	
	//////////////
	///MC 1.9 ////
	//////////////
	

	@Override
	public void spawnParticle(org.bukkit.Particle particle, Location location, int count) {
		getRealPlayer().spawnParticle(particle, location , count);
	}


	@Override
	public void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count) {
		getRealPlayer().spawnParticle(particle, x,y,z , count);
	}


	@Override
	public <T> void spawnParticle(org.bukkit.Particle particle, Location location, int count, T data) {
		getRealPlayer().spawnParticle(particle, location , count, data);
	}


	@Override
	public <T> void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, T data) {
		getRealPlayer().spawnParticle(particle, x,y,z , count, data);
	}


	@Override
	public void spawnParticle(org.bukkit.Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ) {
		getRealPlayer().spawnParticle(particle, location , count, offsetX, offsetY, offsetZ);
	}


	@Override
	public void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ) {
		getRealPlayer().spawnParticle(particle, x,y,z , count, offsetX, offsetY, offsetZ);
	}


	@Override
	public <T> void spawnParticle(org.bukkit.Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, T data) {
		getRealPlayer().spawnParticle(particle, location , count, offsetX, offsetY, offsetZ, data);
	}


	@Override
	public <T> void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, T data) {
		getRealPlayer().spawnParticle(particle, x,y,z , count, offsetX, offsetY, offsetZ, data);
	}


	@Override
	public void spawnParticle(org.bukkit.Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, double extra) {
		getRealPlayer().spawnParticle(particle, location , count, offsetX, offsetY, offsetZ, extra);
	}


	@Override
	public void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra) {
		getRealPlayer().spawnParticle(particle, x,y,z , count, offsetX, offsetY, offsetZ, extra);
	}


	@Override
	public <T> void spawnParticle(org.bukkit.Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, double extra, T data) {
		getRealPlayer().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
	}


	@Override
	public <T> void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra, T data) {
		getRealPlayer().spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
	}


	@Override
	public InventoryView openMerchant(Villager trader, boolean force) {
		return getRealPlayer().openMerchant(trader, force);
	}


	@Override
	public org.bukkit.attribute.AttributeInstance getAttribute(org.bukkit.attribute.Attribute attribute) {
		return getRealPlayer().getAttribute(attribute);
	}


	@Override
	public void setGlowing(boolean flag) {
		getRealPlayer().setGlowing(flag);
	}


	@Override
	public boolean isGlowing() {
		return getRealPlayer().isGlowing();
	}

	
	
}

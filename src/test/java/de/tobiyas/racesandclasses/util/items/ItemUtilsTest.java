package de.tobiyas.racesandclasses.util.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.bukkit.Material.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import de.tobiyas.racesandclasses.util.items.ItemUtils.ArmorSlot;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;


public class ItemUtilsTest {

	
	@Test
	public void check_item_value_for_armor(){
		//Leather
		ItemQuality toCheck = ItemQuality.Leather;
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(LEATHER_HELMET)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(LEATHER_CHESTPLATE)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(LEATHER_LEGGINGS)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(LEATHER_BOOTS)));
		
		//Chain
		toCheck = ItemQuality.Chain;
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(CHAINMAIL_HELMET)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(CHAINMAIL_CHESTPLATE)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(CHAINMAIL_LEGGINGS)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(CHAINMAIL_BOOTS)));
		
		//Iron
		toCheck = ItemQuality.Iron;
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(IRON_HELMET)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(IRON_CHESTPLATE)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(IRON_LEGGINGS)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(IRON_BOOTS)));
		
		//Diamond
		toCheck = ItemQuality.Diamond;
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(DIAMOND_HELMET)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(DIAMOND_CHESTPLATE)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(DIAMOND_LEGGINGS)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(DIAMOND_BOOTS)));
		
		//Gold
		toCheck = ItemQuality.Gold;
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(GOLD_HELMET)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(GOLD_CHESTPLATE)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(GOLD_LEGGINGS)));
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(GOLD_BOOTS)));		
		
		//Other
		toCheck = ItemQuality.None;
		assertEquals(toCheck, ItemUtils.getItemValue(new ItemStack(BOW)));
	}
	
	
	@Test
	public void check_armor_values(){
		
		//LeatherArmor stuff
		assertEquals(1, ItemUtils.getArmorValueOfItem(new ItemStack(Material.LEATHER_BOOTS)));
		assertEquals(1, ItemUtils.getArmorValueOfItem(new ItemStack(Material.LEATHER_HELMET)));
		assertEquals(2, ItemUtils.getArmorValueOfItem(new ItemStack(Material.LEATHER_LEGGINGS)));
		assertEquals(3, ItemUtils.getArmorValueOfItem(new ItemStack(Material.LEATHER_CHESTPLATE)));
		
		//GoldArmor stuff
		assertEquals(1, ItemUtils.getArmorValueOfItem(new ItemStack(Material.GOLD_BOOTS)));
		assertEquals(2, ItemUtils.getArmorValueOfItem(new ItemStack(Material.GOLD_HELMET)));
		assertEquals(3, ItemUtils.getArmorValueOfItem(new ItemStack(Material.GOLD_LEGGINGS)));
		assertEquals(5, ItemUtils.getArmorValueOfItem(new ItemStack(Material.GOLD_CHESTPLATE)));
		
		//ChainArmor stuff
		assertEquals(1, ItemUtils.getArmorValueOfItem(new ItemStack(Material.CHAINMAIL_BOOTS)));
		assertEquals(2, ItemUtils.getArmorValueOfItem(new ItemStack(Material.CHAINMAIL_HELMET)));
		assertEquals(4, ItemUtils.getArmorValueOfItem(new ItemStack(Material.CHAINMAIL_LEGGINGS)));
		assertEquals(5, ItemUtils.getArmorValueOfItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE)));
		
		//IronArmor stuff
		assertEquals(2, ItemUtils.getArmorValueOfItem(new ItemStack(Material.IRON_BOOTS)));
		assertEquals(2, ItemUtils.getArmorValueOfItem(new ItemStack(Material.IRON_HELMET)));
		assertEquals(5, ItemUtils.getArmorValueOfItem(new ItemStack(Material.IRON_LEGGINGS)));
		assertEquals(6, ItemUtils.getArmorValueOfItem(new ItemStack(Material.IRON_CHESTPLATE)));
		
		//DiamondArmor stuff
		assertEquals(3, ItemUtils.getArmorValueOfItem(new ItemStack(Material.DIAMOND_BOOTS)));
		assertEquals(3, ItemUtils.getArmorValueOfItem(new ItemStack(Material.DIAMOND_HELMET)));
		assertEquals(6, ItemUtils.getArmorValueOfItem(new ItemStack(Material.DIAMOND_LEGGINGS)));
		assertEquals(8, ItemUtils.getArmorValueOfItem(new ItemStack(Material.DIAMOND_CHESTPLATE)));
		
		//Other
		assertEquals(0, ItemUtils.getArmorValueOfItem(new ItemStack(Material.APPLE)));
		assertEquals(0, ItemUtils.getArmorValueOfItem(new ItemStack(Material.AIR)));
		assertEquals(0, ItemUtils.getArmorValueOfItem(null));		
	}
	
	
	@Test
	public void check_armor_slot(){
		//LeatherArmor stuff
		assertEquals(ArmorSlot.BOOTS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.LEATHER_BOOTS)));
		assertEquals(ArmorSlot.HELMET, ItemUtils.getItemSlotEquiping(new ItemStack(Material.LEATHER_HELMET)));
		assertEquals(ArmorSlot.LEGGINGS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.LEATHER_LEGGINGS)));
		assertEquals(ArmorSlot.CHESTPLATE, ItemUtils.getItemSlotEquiping(new ItemStack(Material.LEATHER_CHESTPLATE)));
		
		//GoldArmor stuff
		assertEquals(ArmorSlot.BOOTS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.GOLD_BOOTS)));
		assertEquals(ArmorSlot.HELMET, ItemUtils.getItemSlotEquiping(new ItemStack(Material.GOLD_HELMET)));
		assertEquals(ArmorSlot.LEGGINGS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.GOLD_LEGGINGS)));
		assertEquals(ArmorSlot.CHESTPLATE, ItemUtils.getItemSlotEquiping(new ItemStack(Material.GOLD_CHESTPLATE)));
		
		//ChainArmor stuff
		assertEquals(ArmorSlot.BOOTS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.CHAINMAIL_BOOTS)));
		assertEquals(ArmorSlot.HELMET, ItemUtils.getItemSlotEquiping(new ItemStack(Material.CHAINMAIL_HELMET)));
		assertEquals(ArmorSlot.LEGGINGS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.CHAINMAIL_LEGGINGS)));
		assertEquals(ArmorSlot.CHESTPLATE, ItemUtils.getItemSlotEquiping(new ItemStack(Material.CHAINMAIL_CHESTPLATE)));
		
		//IronArmor stuff
		assertEquals(ArmorSlot.BOOTS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.IRON_BOOTS)));
		assertEquals(ArmorSlot.HELMET, ItemUtils.getItemSlotEquiping(new ItemStack(Material.IRON_HELMET)));
		assertEquals(ArmorSlot.LEGGINGS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.IRON_LEGGINGS)));
		assertEquals(ArmorSlot.CHESTPLATE, ItemUtils.getItemSlotEquiping(new ItemStack(Material.IRON_CHESTPLATE)));
		
		//DiamondArmor stuff
		assertEquals(ArmorSlot.BOOTS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.DIAMOND_BOOTS)));
		assertEquals(ArmorSlot.HELMET, ItemUtils.getItemSlotEquiping(new ItemStack(Material.DIAMOND_HELMET)));
		assertEquals(ArmorSlot.LEGGINGS, ItemUtils.getItemSlotEquiping(new ItemStack(Material.DIAMOND_LEGGINGS)));
		assertEquals(ArmorSlot.CHESTPLATE, ItemUtils.getItemSlotEquiping(new ItemStack(Material.DIAMOND_CHESTPLATE)));
		
		//Other
		assertEquals(ArmorSlot.NONE, ItemUtils.getItemSlotEquiping(new ItemStack(Material.APPLE)));
		assertEquals(ArmorSlot.NONE, ItemUtils.getItemSlotEquiping(null));		
	}
	
	
	@Test
	public void get_armor_slot_items_from_player_works(){
		Player player = mock(Player.class, RETURNS_DEEP_STUBS);

		
		ItemStack boots = mock(ItemStack.class, RETURNS_DEEP_STUBS);
		when(boots.getType()).thenReturn(Material.LEATHER_BOOTS);
		
		ItemStack leggings = mock(ItemStack.class, RETURNS_DEEP_STUBS);
		when(leggings.getType()).thenReturn(Material.LEATHER_LEGGINGS);
		
		ItemStack chest = mock(ItemStack.class, RETURNS_DEEP_STUBS);
		when(chest.getType()).thenReturn(Material.LEATHER_CHESTPLATE);
		
		ItemStack helmet = mock(ItemStack.class, RETURNS_DEEP_STUBS);
		when(helmet.getType()).thenReturn(Material.LEATHER_HELMET);
		
		
		when(player.getInventory().getBoots()).thenReturn(boots);
		when(player.getInventory().getLeggings()).thenReturn(leggings);
		when(player.getInventory().getChestplate()).thenReturn(chest);
		when(player.getInventory().getHelmet()).thenReturn(helmet);
		
		assertEquals(boots, ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.BOOTS));
		assertEquals(leggings, ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.LEGGINGS));
		assertEquals(chest, ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.CHESTPLATE));
		assertEquals(helmet, ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.HELMET));
		
		assertNull(ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.NONE));
		
		assertNull(ItemUtils.getItemInArmorSlotOfPlayer(player, null));
		
		verify(player.getInventory(), times(1)).getBoots();
		verify(player.getInventory(), times(1)).getLeggings();
		verify(player.getInventory(), times(1)).getChestplate();
		verify(player.getInventory(), times(1)).getHelmet();
	}
	
	
	@Test
	public void test_enum_ItemQuality(){
		assertEquals(-1, ItemUtils.ItemQuality.None.getValue());
		assertEquals(0, ItemUtils.ItemQuality.Leather.getValue());
		assertEquals(1, ItemUtils.ItemQuality.Iron.getValue());
		assertEquals(2, ItemUtils.ItemQuality.Gold.getValue());
		assertEquals(3, ItemUtils.ItemQuality.Diamond.getValue());
		assertEquals(4, ItemUtils.ItemQuality.Chain.getValue());
	}
	
	@Test
	public void needless(){
		//Just to cover up the whole class
		assertNotNull(new ItemUtils());
	}
	
}

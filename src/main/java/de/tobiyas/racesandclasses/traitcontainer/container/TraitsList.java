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
package de.tobiyas.racesandclasses.traitcontainer.container;

import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.AreaAirDropTrait.AreaAirDropTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.AreaDamageTrait.AreaDamageTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.BackstabTrait.BackstabTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.GrapplingHookTrait.GrapplingHookTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.HealOthersTrait.HealOthersTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.MeleeDotTrait.MeleeDotTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.PoisonedWeaponTrait.PoisonedWeaponTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.SprintTrait.SprintTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.TauntTrait.TauntTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.ThrowItemTrait.ThrowItemTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.TrollBloodTrait.TrollbloodTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.CobWebArrowTrait.CobWebArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.ExplosiveArrowTrait.ExplosiveArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.FireArrowTrait.FireArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.MightyArrowTrait.MightyArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.MultishotArrowTrait.MultishotArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.PoisonArrowTrait.PoisonArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.PotionArrowTrait.PotionArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.QuickArrowShotTrait.QuickArrowShotTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.StunArrowTrait.StunArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.TeleportArrowTrait.TeleportArrowTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.AbsorbDamageBuffTrait.AbsorbDamageBuffTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.AreaAirDropSpellTrait.AreaAirDropSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.BlockDisguiseTrait.BlockDisguiseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ColdFeetTrait.ColdFeetTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ExplosionTrait.ExplosionTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.FireballTrait.FireballTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.FirebreathTrait.FirebreathTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.FlyingTrait.FlyingTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.InvisibleTrait.InvisibleTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ItemForManaConsumeTrait.ItemForManaConsumeTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.LifeTapTrait.LifeTapTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.LightningTrait.LightningTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicAreaHealTrait.MagicAreaHealTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicDamageTrait.MagicDamageTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicHealTrait.MagicHealTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicSpellsSpellTrait.MagicSpellsSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.PickupItemTrait.PickupItemTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.PullToSelfTrait.PullToSelfTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.PushAwayTrait.PushAwayTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.SkillAPISpellTrait.SkillAPISpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.SlowFallTrait.SlowFallTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.StunSpellTrait.StunSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.TeleportTrait.TeleportTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ThrowItemsAroundSpellTrait.ThrowItemsAroundSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.WallTrait.WallTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.WeaponNextHitDamageIncreaseBuffTrait.WeaponNextHitDamageIncreaseBuffTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.WeaponNextHitDebuffTrait.WeaponNextHitDebuffTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ArrowDamageIncreaseTrait.ArrowDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.AxeDamageIncreaseTrait.AxeDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.BashTrait.BashTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.BerserkerRageTrait.BerserkerRageTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.CommandTrait.CommandTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DamageIncreaseTrait.DamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DamageReduceTrait.DamageReduceTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DodgeTrait.DodgeTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DwarfSkinTrait.DwarfSkinTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.FasterSprintingTrait.FasterSprintingTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.HoeDamageIncreaseTrait.HoeDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.HorseRestrictionTrait.HorseRestrictionTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.HungerReplenishTrait.HungerReplenishTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.JumpHeightIncreaseTrait.JumpHeightIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.LastStandTrait.LastStandTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ManaOnHitTrait.ManaOnHitTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ManaRegenerationTrait.ManaRegenerationTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MiningSpeedTrait.MiningSpeedTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MonsterDropItemTrait.MonsterDropItemTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MovementSpeedTrait.MovementSpeedTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.NightDamageTrait.NightDamageTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.NoHungerTrait.NoHungerTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PassiveAggroTrait.PassiveAggroTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PermanentEnchantTrait.PermanentEnchantTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PermanentPotionTrait.PermanentPotionTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PermissionTrait.PermissionTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PickaxeDamageIncreaseTrait.PickaxeDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.RegenerationTrait.RegenerationTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.RegularDamageTrait.RegularDamageTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ShovelDamageIncreaseTrait.ShovelDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SpecificRegenerationTrait.SpecificRegenerationTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.StickDamageIncreaseTrait.StickDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SunDamageTrait.SunDamageTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SwimmingSpeedTrait.SwimmingSpeedTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SwordDamageIncreaseTrait.SwordDamageIncreaseTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ToolTrait.ToolTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.UnderwaterBreathTrait.UnderwaterBreathTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.VampirismTrait.VampirismTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.resistance.DrainResistanceTrait.DrainResistanceTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.resistance.FallResistanceTrait.FallResistanceTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.resistance.FireResistanceTrait.FireResistanceTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.resistance.LavaResistanceTrait.LavaResistanceTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.totem.HealTotemTrait.HealTotemTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.totem.ManaRegenerationTotemTrait.ManaRegenerationTotemTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.totem.PotionTotemTrait.PotionTotemTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.ArmorTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.DeathCheckerTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.NormalArrow;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.STDAxeDamageTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitPreChecker;
import de.tobiyas.util.collections.CaseInsenesitveMap;

public class TraitsList{
	
	private static Map<String,TraitInfoContainer> traits;
	private static HashSet<String> categorys;
	
	public static void initStaticTraits(){
		traits = new CaseInsenesitveMap<TraitInfoContainer>();
		categorys = new HashSet<String>();
		categorys.add("None");
		
		//STD
		addTraitToList("DeathCheckerTrait", DeathCheckerTrait.class, "STD", false);
		addTraitToList("STDAxeDamageTrait", STDAxeDamageTrait.class, "STD", false);
		addTraitToList("NormalArrow", NormalArrow.class, "STD", false);
		addTraitToList("ArmorTrait", ArmorTrait.class, "STD", false);
	}
	
	/**
	 * Inits the internal Traits:
	 */
	public static void initInternalTraits(){
		//TODO add new Traits here: 
		//Activate:
		registerClass(AreaAirDropTrait.class);
		registerClass(AreaDamageTrait.class);
		registerClass(BackstabTrait.class);
		registerClass(GrapplingHookTrait.class);
		registerClass(HealOthersTrait.class);
		registerClass(MeleeDotTrait.class);
		registerClass(PoisonedWeaponTrait.class);
		registerClass(SprintTrait.class);
		registerClass(TauntTrait.class);
		registerClass(ThrowItemTrait.class);
		registerClass(TrollbloodTrait.class);
		
		//Arrows:
		registerClass(CobWebArrowTrait.class);
		registerClass(ExplosiveArrowTrait.class);
		registerClass(FireArrowTrait.class);
		registerClass(MightyArrowTrait.class);
		registerClass(MultishotArrowTrait.class);
		registerClass(PoisonArrowTrait.class);
		registerClass(PotionArrowTrait.class);
		registerClass(QuickArrowShotTrait.class);
		registerClass(StunArrowTrait.class);
		registerClass(TeleportArrowTrait.class);
		
		//Magic:
		registerClass(AbsorbDamageBuffTrait.class);
		registerClass(AreaAirDropSpellTrait.class);
		registerClass(BlockDisguiseTrait.class);
		registerClass(ColdFeetTrait.class);
		registerClass(ExplosionTrait.class);
		registerClass(FireballTrait.class);
		registerClass(FirebreathTrait.class);
		registerClass(FlyingTrait.class);
		registerClass(InvisibleTrait.class);
		registerClass(ItemForManaConsumeTrait.class);
		registerClass(LifeTapTrait.class);
		registerClass(LightningTrait.class);
		registerClass(MagicAreaHealTrait.class);
		registerClass(MagicDamageTrait.class);
		registerClass(MagicHealTrait.class);
		registerClass(MagicSpellsSpellTrait.class);
		registerClass(PickupItemTrait.class);
		registerClass(PullToSelfTrait.class);
		registerClass(PushAwayTrait.class);
		registerClass(SkillAPISpellTrait.class);
		registerClass(SlowFallTrait.class);
		registerClass(StunSpellTrait.class);
		registerClass(TeleportTrait.class);
		registerClass(ThrowItemsAroundSpellTrait.class);
		registerClass(WallTrait.class);
		registerClass(WeaponNextHitDamageIncreaseBuffTrait.class);
		registerClass(WeaponNextHitDebuffTrait.class);
		
		//Passive:
		registerClass(ArrowDamageIncreaseTrait.class);
		registerClass(AxeDamageIncreaseTrait.class);
		registerClass(BashTrait.class);
		registerClass(BerserkerRageTrait.class);
		registerClass(CommandTrait.class);
		registerClass(DamageIncreaseTrait.class);
		registerClass(DamageReduceTrait.class);
		registerClass(DodgeTrait.class);
		registerClass(DwarfSkinTrait.class);
		registerClass(FasterSprintingTrait.class);
		registerClass(HoeDamageIncreaseTrait.class);
		registerClass(HorseRestrictionTrait.class);
		registerClass(HungerReplenishTrait.class);
		registerClass(JumpHeightIncreaseTrait.class);
		registerClass(LastStandTrait.class);
		registerClass(ManaRegenerationTrait.class);
		registerClass(ManaOnHitTrait.class);
		registerClass(MiningSpeedTrait.class);
		registerClass(MonsterDropItemTrait.class);
		registerClass(MovementSpeedTrait.class);
		registerClass(NightDamageTrait.class);
		registerClass(NoHungerTrait.class);
		registerClass(PassiveAggroTrait.class);
		registerClass(PermanentEnchantTrait.class);
		registerClass(PermanentPotionTrait.class);
		registerClass(PermissionTrait.class);
		registerClass(PickaxeDamageIncreaseTrait.class);
		registerClass(RegenerationTrait.class);
		registerClass(RegularDamageTrait.class);
		registerClass(ShovelDamageIncreaseTrait.class);
		registerClass(SpecificRegenerationTrait.class);
		registerClass(StickDamageIncreaseTrait.class);
		registerClass(SunDamageTrait.class);
		registerClass(SwimmingSpeedTrait.class);
		registerClass(SwordDamageIncreaseTrait.class);
		registerClass(ToolTrait.class);
		registerClass(UnderwaterBreathTrait.class);
		registerClass(VampirismTrait.class);
		
		//Resistance:
		registerClass(DrainResistanceTrait.class);
		registerClass(FallResistanceTrait.class);
		registerClass(FireResistanceTrait.class);
		registerClass(LavaResistanceTrait.class);
		
		//Totem:
		registerClass(HealTotemTrait.class);
		registerClass(ManaRegenerationTotemTrait.class);
		registerClass(PotionTotemTrait.class);
		
	}
	
	
	
	/**
	 * Registers a new Class.
	 * @param traitClass to register.
	 */
	public static void registerClass(Class<? extends Trait> traitClass){
		try{
			if (traitClass != null) {
				Trait trait = traitClass.newInstance();
		        
				boolean isPresent = trait.getClass().getMethod("importTrait").isAnnotationPresent(TraitInfos.class);
		        if(!isPresent) throw new AnnotationFormatError("Annotation: 'Import' could not be found for class: " + traitClass);

		        
		        TraitInfos annotation = trait.getClass().getMethod("importTrait").getAnnotation(TraitInfos.class);
		        
		        //Check for Versions:
		        if(TraitPreChecker.hasNeeds1_6(traitClass) && !CertainVersionChecker.isAbove1_6()) return;
		        if(TraitPreChecker.hasNeeds1_7(traitClass) && !CertainVersionChecker.isAbove1_7()) return;
		        if(TraitPreChecker.hasNeeds1_8(traitClass) && !CertainVersionChecker.isAbove1_8()) return;
		        
		        //Check for Plugins:
		        for(String plugin : TraitPreChecker.getRequiredPlugins(traitClass)){
		        	if(Bukkit.getPluginManager().getPlugin(plugin) == null) return;
		        }
		        
	        	TraitsList.addTraitToList(annotation.traitName(), traitClass, annotation.category(), annotation.visible());
	        	trait.importTrait();
			}
    	}catch(Throwable e){
    		RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
    	}
	}
	
	
	public static void addTraitToList(String trait, Class<? extends Trait> traitClass, String category){
		addTraitToList(trait, traitClass, category, true);
	}
	
	public static void addTraitToList(String trait, Class<? extends Trait> traitClass, String category, boolean visible){
		TraitInfoContainer toRegister = new TraitInfoContainer(trait, traitClass, category, visible);
		if(traits.containsKey(trait)){
			//Tell that we already have this trait!
			RacesAndClasses.getPlugin().logWarning("Tried to register already registered Trait: " + trait);			
			return;
		}
		
		
		traits.put(trait, toRegister);
		if(!categorys.contains(category)) categorys.add(category);
	}
	
	public static Class<? extends Trait> getClassOfTrait(String name){
		TraitInfoContainer container = traits.get(name);
		return container == null ? null : container.getClazz();
	}
	
	public static List<String> getAllVisibleTraits(){
		LinkedList<String> list = new LinkedList<String>();
		for(TraitInfoContainer trait : traits.values()) {
			if(trait.isVisible()) list.add(trait.getName());
		}
		
		return list;
	}
	
	public static List<String> getAllTraits(){
		return new ArrayList<String>(traits.keySet());
	}
	
	public static HashSet<String> getCategory(String category){
		if(categorys.contains(category))
			return getAllOfCategory(category);
		
		int page = 0;
		try{
			page = Integer.valueOf(category);
		}catch(NumberFormatException e){
		}
		
		int i = 0;
		if(page >= categorys.size())
			page = 0;
			
		for(String categoryName : categorys){
			if(i == page)
				return getAllOfCategory(categoryName);
			i++;
		}
		
		return null;
	}
	
	private static HashSet<String> getAllOfCategory(String category){
		HashSet<String> set = new HashSet<String>();
		for(TraitInfoContainer container : traits.values()){
			if(container.getCategory().equalsIgnoreCase(category)) set.add(container.getName());
		}
		
		return set;
	}

	public static HashSet<String> getAllCategories() {		
		return categorys;
	}
}

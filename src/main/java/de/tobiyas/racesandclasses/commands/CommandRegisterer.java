package de.tobiyas.racesandclasses.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.bind.CommandExecutor_BindTrait;
import de.tobiyas.racesandclasses.commands.bind.CommandExecutor_UseTrait;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_LocalChat;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_BroadCast;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Racechat;
import de.tobiyas.racesandclasses.commands.classes.CommandExecutor_Class;
import de.tobiyas.racesandclasses.commands.config.CommandExecutor_ConfigRegenerate;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_Edit;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_RaceDebug;
import de.tobiyas.racesandclasses.commands.force.CommandExecutor_ForceClass;
import de.tobiyas.racesandclasses.commands.force.CommandExecutor_ForceRace;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_EmptyCommand;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_PlayerInfo;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_RacesReload;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_HP;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_Mana;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceGod;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceHeal;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_ShowTraits;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_PermissionCheck;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RaceHelp;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RacesVersion;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_TraitList;
import de.tobiyas.racesandclasses.commands.level.Command_RACLevel;
import de.tobiyas.racesandclasses.commands.pets.Command_RaCPet;
import de.tobiyas.racesandclasses.commands.races.CommandExecutor_Race;
import de.tobiyas.racesandclasses.commands.racespawn.CommandExecutor_RaceSpawn;
import de.tobiyas.racesandclasses.commands.reflect.CommandMap;
import de.tobiyas.racesandclasses.commands.skilltree.CommandExecutor_SkillTree;
import de.tobiyas.racesandclasses.commands.statistics.CommandExecutor_Statistics;

public class CommandRegisterer {
	
	/**
	 * A list of commands registered.
	 */
	protected final List<CommandInterface> commands = new LinkedList<CommandInterface>();
	
	/**
	 * The Plugin to use.
	 */
	protected final RacesAndClasses plugin;
	
	
	public CommandRegisterer(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Registers the Commands.
	 */
	public void registerCommands(){
		long currentTime = System.currentTimeMillis();
		
		commands.clear();
		
		commands.add(new CommandExecutor_Race());
		commands.add(new CommandExecutor_Racechat());
		commands.add(new CommandExecutor_RaceHelp());
		commands.add(new CommandExecutor_Whisper());
		commands.add(new CommandExecutor_TraitList());
		commands.add(new CommandExecutor_RaceHeal());
		commands.add(new CommandExecutor_RaceDebug());
		commands.add(new CommandExecutor_Class());
		commands.add(new CommandExecutor_HP());
		commands.add(new CommandExecutor_Mana());
		commands.add(new CommandExecutor_Channel());
		commands.add(new CommandExecutor_RaceGod());
		commands.add(new CommandExecutor_BroadCast());
		commands.add(new CommandExecutor_LocalChat());
		commands.add(new CommandExecutor_PlayerInfo());
		commands.add(new CommandExecutor_PermissionCheck());
		
		commands.add(new CommandExecutor_RacesReload());
		commands.add(new CommandExecutor_RacesVersion());
		commands.add(new CommandExecutor_Statistics());
		commands.add(new CommandExecutor_ShowTraits());
		commands.add(new CommandExecutor_Edit());
		
		commands.add(new CommandExecutor_ForceRace());
		commands.add(new CommandExecutor_ForceClass());
		commands.add(new CommandExecutor_ConfigRegenerate());
		
		commands.add(new CommandExecutor_RaceSpawn());
		
		commands.add(new Command_RACLevel());
		commands.add(new Command_RaCPet());
		commands.add(new CommandExecutor_BindTrait());
		commands.add(new CommandExecutor_UseTrait());

		commands.add(new CommandExecutor_SkillTree());
		
		
		Map<String,String> remap = plugin.getConfigManager().getGeneralConfig().getConfig_command_remaps();
		for(CommandInterface command : commands){
			command.applyRemapping(remap);
		}
		
		//remove all disabled commands.
		List<String> disabledCommands = plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands();
		Iterator<CommandInterface> it = commands.iterator();
		while(it.hasNext()){
			CommandInterface command = it.next();
			command.filterToDisabledCommands(disabledCommands);
			
			if(!command.hasAnyCommand()) it.remove();
		}
		
		CommandMap.registerCommands(commands, plugin);		
		if(System.currentTimeMillis() - currentTime > 1000){
			plugin.log("Took too long to Init all commands! Please report this. Time taken: " + (System.currentTimeMillis() - currentTime) + " mSecs.");
		}
	}
	
	/**
	 * Registers all commands as Error.
	 */
	public void registerAllCommandsAsError() {
		for(CommandInterface command : this.commands){
			new CommandExecutor_EmptyCommand(command.getCommandNames());
		}
	}
	
}

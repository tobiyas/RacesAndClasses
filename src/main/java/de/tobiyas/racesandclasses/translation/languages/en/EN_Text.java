package de.tobiyas.racesandclasses.translation.languages.en;

import static de.tobiyas.racesandclasses.translation.languages.Keys.already_are;
import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_class;
import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_race;
import static de.tobiyas.racesandclasses.translation.languages.Keys.armor_not_allowed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.arrow_change;
import static de.tobiyas.racesandclasses.translation.languages.Keys.bow_selected_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.change_to_same_holder;
import static de.tobiyas.racesandclasses.translation.languages.Keys.channel_propertie;
import static de.tobiyas.racesandclasses.translation.languages.Keys.class_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.class_changed_to_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.class_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.cooldown_is_ready_again;
import static de.tobiyas.racesandclasses.translation.languages.Keys.failed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.healed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.healed_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.login_no_race_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_change_spells;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_dont_have_enough;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_no_spells;
import static de.tobiyas.racesandclasses.translation.languages.Keys.member_config_attribute_not_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.member_config_changed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.member_config_not_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.needs_1_arg;
import static de.tobiyas.racesandclasses.translation.languages.Keys.new_value;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_in_list;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_on_change;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_selected_use_info;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_to_select;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_healthcontainer_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_to_select;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_traits;
import static de.tobiyas.racesandclasses.translation.languages.Keys.number_not_readable;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_traits;
import static de.tobiyas.racesandclasses.translation.languages.Keys.password;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.plugin_pre;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.reload_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.send_empty_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.something_disabled;
import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_ended;
import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_still;
import static de.tobiyas.racesandclasses.translation.languages.Keys.success;
import static de.tobiyas.racesandclasses.translation.languages.Keys.target_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.time_in_seconds;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_already_running;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_error;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_no_set_at_this_state;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_not_running;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_stopped;
import static de.tobiyas.racesandclasses.translation.languages.Keys.value_0_not_allowed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wand_select_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.whisper_yourself;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your_class;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your_race;


public class EN_Text {

	public static final String en_language = ""
			//a
			+ already_are + ": '&cYou already are a &d%HOLDER%&c.'\r\n"
			+ already_have_class + ": '&cYou already have a class: &d%CLASS%&c. Use &c/class change&c to change your class.'\r\n"
			+ already_have_race + ": '&cYou already have a race: &d%RACE%&c. Use &c/race change&c to change your race.'\r\n"
			+ armor_not_allowed + ": '&cYou are not allowed to use &c%MATERIAL%&a.'\r\n"
			+ arrow_change + ": '&aSwitched arrows to: &d%TRAIT_NAME%&a.'\r\n"
			
			//b
			+ bow_selected_message + ": '&a@plugin_pre@ You have a &dBOW &ain your hand. Use &dLEFT &aClick to change through your arrows. Current arrow: &d%CURRENT_ARROW% &a.'\r\n"
			
			//c
			+ channel_propertie + ": 'channelPropertie'\r\n"
			+ change_to_same_holder + ": '&cYou are already a &d%HOLDER%&c.'\r\n"
			+ "class" + ": 'class'\r\n"
			+ class_not_exist + ": '&cThe class &d%CLASS% &cdoes not exist.'\r\n"
			+ class_changed_to + ": '&cYour Class has been changed to: &d%CLASS%&c.'\r\n"
			+ class_changed_to_other + ": '&aClass of &d%PLAYER%&a changed to: &d%CLASS%&d.'\r\n"
			+ cooldown_is_ready_again + ": '&d%TRAIT_NAME% + &ais ready again.'\r\n"
			
			//d
			
			//e
			
			//f
			+ failed + ": '&cfailed'\r\n"
			
			//g
			
			//h
			+ healed + ": '&aYou have been healed.'\r\n"
			+ healed_other + ": '&aYou have healed: &d%PLAYER%&a.'\r\n"
			
			//i
			
			//j
			
			//h
			
			//i
			
			//j
			
			//k
			
			//l
			+ login_no_race_selected + ": '&cYou have not selected a Race. Please select a race using /race select <racename>'\r\n"
			
			
			//m
			+ magic_change_spells + ": '&aChanged Spell to: &d%TRATINAME% &aCost: &d%COST% %COST_TYPE%&a.'\r\n"
			+ magic_dont_have_enough + ": '&c@plugin_pre@ You do not have enough &d%COST_TYPE% %cfor &d%TRAIT_NAME% &c.'\r\n"
			+ magic_no_spells + ": '&cYou can not cast any spells.'\r\n"
			+ member_config_not_found + ": '&cYour config could not be found. Try relogging or contact an Admin.'\r\n"
			+ member_config_changed + ": '&aThe Attribute &d%ATTRIBUTE%&a has been changed to: &d%VALUE%&a.'\r\n"
			+ member_config_attribute_not_found + ": '&cThe Attribute &d%ATTRIBUTE%&c could not be found.'\r\n"
			
			//n
			+ needs_1_arg + ": 'This command needs 1 argument: %COMMAND%'\r\n"
			+ new_value + ": 'new Value'\r\n"
			+ no_class_in_list + ": 'No Classes in the list.'\r\n"
			+ no_class_on_change + ": '@no_class_selected@. Use &d/class select &cto select a class.'\r\n"
			+ no_class_selected_use_info + ": '&cYou have no class selected. Use &d/class info <class name> &cto inspect a class.'\r\n"			
			+ no_class_selected + ": '&cYou have no class selected.'\r\n"			
			+ no_class_to_select + ": '&c@plugin_pre@ You do not have any Classes to select.'\r\n"
			+ no_healthcontainer_found + ": '&cSomething gone Wrong. No healthcontainer found for you.'\r\n"
			+ no_message + ": '&cNo message given.'\r\n"
			+ number_not_readable + ": '&cNumber could not be read.'\r\n"
			+ no_race_selected + ": '&cYou have no race selected.'\r\n"
			+ no_race_to_select + ": '&c@plugin_pre@ You do not have any Race to select.'\r\n"			
			+ no_traits + ": '&cNo Traits.'\r\n"			
			
			//o
			+ only_players + ": '&cThis command can only be used by Players.'\r\n"
			+ open_holder + ": '&2Opening %HOLDER% Selection....'\r\n"
			+ open_traits + ": '&a@plugin_pre@ Opening Traits of &d%PLAYER%&a.'\r\n"
			
			//p
			+ password + ": 'password'\r\n"
			+ player_not_exist + ": '&cPlayer &d%PLAYER%&c does not exist.'\r\n"
			+ plugin_pre + ": '[RaC]'\r\n"
			
			//q
			
			//r
			+ race + ": 'race'\r\n"
			+ race_not_exist + ": '&cThe race &d%RACE% &cdoes not exist.'\r\n"
			+ race_changed_to + ": '&cYour Race has been changed to: &d%RACE%&c.'\r\n"
			+ race_changed_to_other + ": '&aRace of &d%PLAYER%&a changed to: &d%RACE%&d.'\r\n"
			+ reload_message + ": '&aReload of &dRaces &adone successfully. Time taken: &d %TIME% &ams'\r\n"
			
			//s
			+ send_empty_message + ": '&cYou tried to send an empty Message.'\r\n"
			+ something_disabled + ": '&c%VALUE% are disabled.'\r\n"
			+ stun_ended + ": '@plugin_pre@ &cYou are not stunned any more.'\r\n"
			+ stun_message + ": '@plugin_pre@ &cYou are stunned for &d %TIME% &cSeconds.'\r\n"
			+ stun_still + ": '@plugin_pre@ &cYou are still stunned for &b%TIME% &cSeconds. You can not %ACTION%.'\r\n"
			+ success + ": '&aSuccess.'\r\n"
			

			//t
			+ target_not_exist + ": '&cTarget does not exist or is offline'\r\n"
			+ time_in_seconds + ": 'time in seconds'\r\n"
			+ tutorial_already_running + ": '&cYou already have a Tutorial Running.'\r\n"
			+ tutorial_error + ": '&cCould not execute this command at your current Step.'\r\n"
			+ tutorial_no_set_at_this_state + ": '&cCan not set state at this moment.'\r\n"
			+ tutorial_not_running + ": '&cYou have no Tutorial running.'\r\n"
			+ tutorial_stopped + ": '&cTutorial Stopped. To restart it, use &b/racestutorial start'\r\n"
			
			//u
			
			//v
			+ value_0_not_allowed + ": '&cValue of 0 is not allowed.'\r\n"
			
			//w
			+ wand_select_message + ": '&a@plugin_pre@ You have a &d WAND &ain your hand. Use &dLEFT &aClick to cast and &dRIGHT &aClick to switch spells. Current spell: &d%CURRENT_SPELL%&a.'\r\n"
			+ wrong_command_use + ": '&cWrong usage. Use the command like this: &d%COMMAND%&c.'\r\n"
			+ whisper_yourself + ": '&cYou can not whisper yourself.'\r\n"
			
			//x
			
			//y
			+ your + ": 'your'\r\n"
			+ your_class + ": 'your Class'\r\n"
			+ your_race + ": 'your Race'\r\n"
			
			//z
			
			
			
			

			+ "";

}

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
package de.tobiyas.racesandclasses.util.consts;

public class PermissionNode {

	/**
	 * The Prefix of the Plugin which all nodes extend.
	 */
	public static final String prePlugin = "RaC.";

	/**
	 * The Permission to change your race.
	 */
	public static final String changeRace = prePlugin + "race.change";
	
	/**
	 * The Permission to select your Race
	 */
	public static final String selectRace = prePlugin + "race.select"; 
	
	/**
	 * The Permissions to change your Class
	 */
	public static final String changeClass = prePlugin + "class.change"; 
	
	/**
	 * The Permission to select your Class
	 */
	public static final String selectClass = prePlugin + "class.select";
	
	/**
	 * The Permission to use /racforce
	 */
	public static final String forceChange = prePlugin + "forcechange";
	
	/**
	 * The Permission to create a private channel
	 */
	public static final String channelCreatePrivate = prePlugin + "channel.create.private";
	
	/**
	 * The Permission to create a public channel.
	 */
	public static final String channelCreatePublic = prePlugin + "channel.create.public";
	
	/**
	 * The Permission to create a password protected channel
	 */
	public static final String channelCreatePassword = prePlugin + "channel.create.password";
	
	/**
	 * The Power to ban People in the Global channel
	 */
	public static final String channelGlobalBanPower = prePlugin + "chanel.global.banpower";
	
	/**
	 * The Power to unban People in the Global channel
	 */
	public static final String channelGlobalUnbanPower = prePlugin + "chanel.global.unbanpower";
	
	/**
	 * The Power to mute people in the Global channel
	 */
	public static final String channelGlobalMutePower = prePlugin + "chanel.global.mutepower";
	
	/**
	 * The Power to unmute people in the Global channel
	 */
	public static final String channelGlobalUnmutePower = prePlugin + "chanel.global.unmutepower";
	
	/**
	 * The Permission to edit a channel without beeing Admin of this channel.
	 */
	public static final String channelEdit = prePlugin + "channel.edit";
	
	/**
	 * The permission to reload channels
	 */
	public static final String channelReload = prePlugin + "channel.reload";
	
	/**
	 * The Permission to whisper someone
	 */
	public static final String whisper = prePlugin + "whisper";
	
	/**
	 * The Permission to whisper someone
	 */
	public static final String configregen = prePlugin + "configregenerate";
	
	/**
	 * The Permission for the /raceHeal command
	 */
	public static final String healSelf = prePlugin + "heal.self";
	
	/**
	 * The Permission for the /raceHeal <otherplayer Name> command
	 */
	public static final String healOther = prePlugin + "heal.other";
	
	/**
	 * The Permission to change god mode for Players.
	 */
	public static final String god = prePlugin + "god";
	
	/**
	 * The Permissions to use the '/racesdebug [subcommand]' command.
	 */
	public static final String debug = prePlugin + "debug";
	
	/**
	 * The Permissions to use the '/racesreload' command
	 */
	public static final String reload = prePlugin + "reload";

	/**
	 * The Permission to Broadcast something to a channel.
	 * 
	 * WARNING: This Permission allows broadcasting in channels you are not in.
	 */
	public static final String broadcast = prePlugin + "channel.broadcast";
	
	/**
	 * The Permission to delete channels.
	 */
	public static final String channel_delete = prePlugin + "channel.delete";
	
	/**
	 * The Permissions to use the '/racstatistics [plugin / subcommand]' command.
	 */
	public static final String statistics = prePlugin + "statistics";
	
	/**
	 * The Permission to Add / Remove / Edit Races.
	 */
	public static final String racEdit = prePlugin + "racedit";
	
	/**
	 * The Permission to use /playerifo
	 */
	public static final String playerInfos = prePlugin + "playerinfos.command";
	
	/**
	 * The Permission to show location on /playerinfo
	 */
	public static final String playerInfosLocation = prePlugin + "playerinfos.location";
	
	/**
	 * The Permission to show Others on /playerinfo
	 */
	public static final String playerInfosOthers = prePlugin + "playerinfos.others";
	
	/**
	 * The Prefix for Race permissions
	 */
	public static final String racePermPre = prePlugin + "races.";

	/**
	 * The Prefix for Class permissions
	 */
	public static final String classPermPre = prePlugin + "classes.";
	
	/**
	 * The Prefix for Class permissions
	 */
	public static final String raceSpawnCreate = prePlugin + "racespawn.create";
	
	/**
	 * The Prefix for Class permissions
	 */
	public static final String raceSpawnUseOwn = prePlugin + "racespawn.use.own";
	
	/**
	 * The Prefix for Class permissions
	 */
	public static final String raceSpawnUseOther = prePlugin + "racespawn.use.other";

	/**
	 * The Permission to edit your level.
	 */
	public static final String levelEdit = prePlugin + "level.edit";

}

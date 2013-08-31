package de.tobiyas.racesandclasses.commands.chat.channels;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_ChannelTest {

	private CommandExecutor_Channel sut;
	
	private Player sender;
	private String playerName = "player";
	
	
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		ConfigManager configManager = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
		((MockRaCPlugin) RacesAndClasses.getPlugin() ).setConfigManager(configManager);
		
		sut = new CommandExecutor_Channel();
		sut.addObserver(RacesAndClasses.getPlugin().getTutorialManager());

		sender = mock(Player.class);
		when(sender.getName()).thenReturn(playerName);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(true);
	}
	
	
	@After
	public void teardown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	@Test
	public void registering_with_correct_executer_works(){
		String commandName = "channel";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_Channel();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	
	private void verifyHelp(CommandSender sender){
		verify(sender).sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "info " + ChatColor.AQUA + "[channelname]");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "list");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "<post/change/switch> " + ChatColor.YELLOW + "<channelname>");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "join " + ChatColor.YELLOW + "<channelname> " + 
				ChatColor.AQUA + "[password]");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "leave " + ChatColor.YELLOW + "<channelname> ");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "create " + ChatColor.YELLOW + "<channelname> " +
				   ChatColor.AQUA + "[channeltype] [password]");
		verify(sender).sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "edit " + ChatColor.YELLOW + "<channelname> " +
				ChatColor.AQUA + "<property> <newValue>");
	}
	
	
	@Test
	public void disabled_chat_gives_error_message(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(false);
		
		sut.onCommand(sender, null, "", new String[]{});
		verify(sender).sendMessage(ChatColor.RED + "Channels are disabled.");
	}
	
	
	@Test
	public void console_can_use_help(){
		sut.onCommand(sender, null, "", new String[]{});
		verifyHelp(sender);
	}
	
	
	@Test
	public void test_channel_list(){
		List<String> channels = new LinkedList<String>();
		channels.add("channel1");
		channels.add("channel2");
		
		when(RacesAndClasses.getPlugin().getChannelManager().listAllPublicChannels()).thenReturn(channels);
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.PublicChannel);
		
		when(RacesAndClasses.getPlugin().getChannelManager().isMember(playerName, "channel1")).thenReturn(false);
		when(RacesAndClasses.getPlugin().getChannelManager().isMember(playerName, "channel2")).thenReturn(true);
		
		
		sut.onCommand(sender, null, "", new String[]{"list"});
		
		verify(sender).sendMessage(ChatColor.YELLOW + "======" + ChatColor.RED + "Channel-List:" + ChatColor.YELLOW + "=====");
		verify(sender).sendMessage(ChatColor.YELLOW + "HINT: Format is: " + ChatColor.BLUE + "ChannelName: " + ChatColor.AQUA + "ChannelLevel");
		
		
		verify(sender).sendMessage(ChatColor.BLUE + "channel1" + ": " + ChatColor.AQUA + ChannelLevel.PublicChannel.name());

		verify(sender).sendMessage(ChatColor.BLUE + "channel2" + ": " + ChatColor.AQUA + ChannelLevel.PublicChannel.name() + 
				ChatColor.YELLOW + "   <-[Joined]");
	}
	
	
	@Test
	public void console_can_use_info(){
		String defaultChannel = "Global";
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName).getCurrentChannel())
			.thenReturn(defaultChannel);
		
		sut.onCommand(sender, null, "", new String[]{"info"});
		
		verify(sender).sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + " Channel Information: " + 
							ChatColor.AQUA + defaultChannel + ChatColor.YELLOW + " =====");
		
		verify(RacesAndClasses.getPlugin().getChannelManager(), times(1)).postChannelInfo(sender, defaultChannel);
	}
	
	
	@Test
	public void console_may_not_use_all_commands(){
		CommandSender sender = mock(CommandSender.class);
		when(sender.getName()).thenReturn("Console");
		
		sut.onCommand(sender, null, "", new String[]{"change"});
		
		verify(sender).sendMessage(ChatColor.RED + "This command can only be used by Players.");
	}
	
	
	@Test
	public void console_can_use_info_with_param(){
		String defaultChannel = "Global";
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName).getCurrentChannel())
			.thenReturn(defaultChannel);
		
		sut.onCommand(sender, null, "", new String[]{"info", defaultChannel});
		
		verify(sender).sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + " Channel Information: " + 
							ChatColor.AQUA + defaultChannel + ChatColor.YELLOW + " =====");
		
		verify(RacesAndClasses.getPlugin().getChannelManager(), times(1)).postChannelInfo(sender, defaultChannel);
	}
	
	
	@Test
	public void change_channel_with_valid_channel_works(){		
		String defaultChannel = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(defaultChannel)).thenReturn(ChannelLevel.GlobalChannel);
		when(RacesAndClasses.getPlugin().getChannelManager().isMember(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{"change", defaultChannel});
		
		verify(sender).sendMessage(ChatColor.GREEN + "You now write in the channel: " + ChatColor.AQUA + defaultChannel);
	}
	
	@Test
	public void change_channel_to_valid_channel_but_not_member_fails(){
		String defaultChannel = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(defaultChannel)).thenReturn(ChannelLevel.GlobalChannel);
		when(RacesAndClasses.getPlugin().getChannelManager().isMember(anyString(), anyString())).thenReturn(false);
		
		sut.onCommand(sender, null, "", new String[]{"change", defaultChannel});
		
		verify(sender).sendMessage(ChatColor.RED + "You are no member of: " + ChatColor.LIGHT_PURPLE + defaultChannel);
	}
	
	
	@Test
	public void change_channel_with_too_few_arguments_posts_error(){
		sut.onCommand(sender, null, "", new String[]{"change"});
		verify(sender).sendMessage(ChatColor.RED + "Wrong usage. Use the command like this:" + 
				ChatColor.LIGHT_PURPLE + "/channel change <channelname>");
	}
	
	
	@Test
	public void change_channel_with_invalid_channel_fails(){
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.NONE);
		
		String invalidChannel = "invalid";
		sut.onCommand(sender, null, "", new String[]{"change", invalidChannel});
		
		verify(sender).sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + invalidChannel);
	}
	
	
	@Test
	public void change_channel_to_tutorial_channel_triggers_tutorial(){
		String tutorialChannel = "tutorial";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(tutorialChannel)).thenReturn(ChannelLevel.PublicChannel);
		when(RacesAndClasses.getPlugin().getChannelManager().isMember(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{"change", tutorialChannel});
		
		verify(sender).sendMessage(ChatColor.GREEN + "You now write in the channel: " + ChatColor.AQUA + tutorialChannel);
		verify(RacesAndClasses.getPlugin().getTutorialManager()).update(any(CommandExecutor_Channel.class), any(TutorialStepContainer.class));
	}
	
	@Test
	public void change_channel_fails_because_memberconfig_is_null(){
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName)).thenReturn(null);
		
		String defaultChannel = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(defaultChannel)).thenReturn(ChannelLevel.GlobalChannel);
		when(RacesAndClasses.getPlugin().getChannelManager().isMember(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{"change", defaultChannel});
		
		verify(sender).sendMessage(ChatColor.RED + "Something gone wrong with your config. Try relogging or ask an Admin.");
	}
	
	
	@Test
	public void join_channel_fails_with_wrong_arg_number(){		
		sut.onCommand(sender, null, "", new String[]{"join"});
		sut.onCommand(sender, null, "", new String[]{"join", "arg2", "arg3", "tooMuchArg"});
		
		verify(sender, times(2)).sendMessage(ChatColor.RED + "The command: " + ChatColor.LIGHT_PURPLE + "/channel join <channelname> [password]" + 
									ChatColor.RED + " needs a channelname. And optionaly a password");
	}
	
	
	@Test
	public void join_channel_works(){
		String channelName = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(channelName)).thenReturn(ChannelLevel.GlobalChannel);
		
		sut.onCommand(sender, null, "", new String[]{"join", channelName});
		verify(RacesAndClasses.getPlugin().getChannelManager()).joinChannel(sender, channelName, "", true);
	}
	
	
	@Test
	public void join_channel_works_with_password(){
		String channelName = "Global";
		String password = "42";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(channelName)).thenReturn(ChannelLevel.PasswordChannel);
		
		sut.onCommand(sender, null, "", new String[]{"join", channelName, password});
		verify(RacesAndClasses.getPlugin().getChannelManager()).joinChannel(sender, channelName, password, true);
	}
	
	
	@Test
	public void join_channel_fails_when_channel_not_found(){
		String channelName = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(channelName)).thenReturn(ChannelLevel.NONE);
		
		sut.onCommand(sender, null, "", new String[]{"join", channelName});
		verify(sender).sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channelName);
	}
	
	
	@Test
	public void leave_channel_works(){
		String channelName = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(channelName)).thenReturn(ChannelLevel.GlobalChannel);
		
		sut.onCommand(sender, null, "", new String[]{"leave", channelName});
		verify(RacesAndClasses.getPlugin().getChannelManager()).leaveChannel(sender, channelName, true);
	}
	
	
	@Test
	public void leave_channel_fails_when_channel_not_present(){
		String channelName = "Global";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(channelName)).thenReturn(ChannelLevel.NONE);
		sut.onCommand(sender, null, "", new String[]{"leave", channelName});
		
		verify(sender).sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channelName);
	}
	
	
	@Test
	public void leave_channel_changes_to_global_channel_when_currente_output_channel_is_left(){
		String channelName = "Other";
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(channelName)).thenReturn(ChannelLevel.GlobalChannel);
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName).getCurrentChannel()).thenReturn(channelName);
		
		sut.onCommand(sender, null, "", new String[]{"leave", channelName});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).leaveChannel(sender, channelName, true);
		verify(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName))
				.setValue(MemberConfig.chatChannel, "Global");	
	}
	
	
	
	@Test
	public void leave_channel_fails_with_too_few_args(){
		sut.onCommand(sender, null, "", new String[]{"leave"});
		sut.onCommand(sender, null, "", new String[]{"leave", "arg2", "arg3"});
		
		verify(sender, times(2)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel leave <channelname>");
	}
	
	
	@Test
	public void create_channel_with_wrong_arg_amount(){
		sut.onCommand(sender, null, "", new String[]{"create"});
		sut.onCommand(sender, null, "", new String[]{"create", "arg2", "arg3", "arg4", "arg5"});
		
		verify(sender, times(2)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel create <channelname> [channelType] [password]");
	}
	
	
	@Test
	public void create_channel_with_wrong_type_fails(){
		String invalidType = "invalid";
		sut.onCommand(sender, null, "", new String[]{"create", "Test", invalidType});
		
		verify(sender).sendMessage(ChatColor.RED + "Channel Level could not be recognized: " + ChatColor.LIGHT_PURPLE + invalidType);
	}
	
	
	@Test
	public void create_channel_fails_when_creating_global_race_local_world_channel(){
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "global", ""});
		verify(sender).sendMessage(ChatColor.RED + "You can't create a new " + ChatColor.AQUA + ChannelLevel.GlobalChannel.name());
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "local", ""});
		verify(sender).sendMessage(ChatColor.RED + "You can't create a new " + ChatColor.AQUA + ChannelLevel.LocalChannel.name());
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "world", ""});
		verify(sender).sendMessage(ChatColor.RED + "You can't create a new " + ChatColor.AQUA + ChannelLevel.WorldChannel.name());
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "race", ""});
		verify(sender).sendMessage(ChatColor.RED + "You can't create a new " + ChatColor.AQUA + ChannelLevel.RaceChannel.name());		
	}
	
	
	@Test
	public void create_channel_fails_when_creating_channels_without_permissions(){
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "PasswordChannel", "123"});
		verify(RacesAndClasses.getPlugin().getPermissionManager()).checkPermissions(sender, PermissionNode.channelCreatePassword);
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test"});
		verify(RacesAndClasses.getPlugin().getPermissionManager()).checkPermissions(sender, PermissionNode.channelCreatePublic);
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "PrivateChannel"});
		verify(RacesAndClasses.getPlugin().getPermissionManager()).checkPermissions(sender, PermissionNode.channelCreatePrivate);
		
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test2", "PrivateChannel", "1324"});
		verify(sender).sendMessage(ChatColor.YELLOW + "[INFO] You try to create a non-password channel with a password. The password will be ignored.");		
	}
	
	
	@Test
	public void create_channel_fails_if_channelname_already_exists(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.PrivateChannel);
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test2", "PasswordChannel", "123"});
		
		verify(sender).sendMessage(ChatColor.RED + "This channel already exisists.");
	}
	
	
	@Test
	public void create_channel_works(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.NONE);
		
		sut.onCommand(sender, null, "", new String[]{"create", "Test", "Private", ""});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).registerChannel(ChannelLevel.PrivateChannel, "Test", "", sender);
	}
	
	//ban
	
	@Test
	public void ban_player_from_channel_with_wrong_amount_of_args_fails(){
		sut.onCommand(sender, null, "", new String[]{"ban"});
		sut.onCommand(sender, null, "", new String[]{"ban", "arg2"});
		sut.onCommand(sender, null, "", new String[]{"ban", "arg2", "arg3", "arg4" , "arg5"});
		
		verify(sender, times(3)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel ban <channelname> <playername> [time in sec]");
	}

	
	@Test
	public void ban_player_from_channel_works(){
		String channel = "channel";
		String user = "user";
		String time = "42";
		int timeAsInt = 42;
		
		sut.onCommand(sender, null, "", new String[]{"ban", channel, user, time});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).banPlayer(sender, user, channel, timeAsInt);
	}
	
	@Test
	public void ban_player_from_channel_works_when_no_time_passed(){
		String channel = "channel";
		String user = "user";
		
		sut.onCommand(sender, null, "", new String[]{"ban", channel, user});
		verify(RacesAndClasses.getPlugin().getChannelManager()).banPlayer(sender, user, channel, Integer.MAX_VALUE);
	}
	
	@Test
	public void ban_player_from_channel_works_when_incorrect_time_passed(){
		String channel = "channel";
		String user = "user";
		String time = "invalid";
		
		sut.onCommand(sender, null, "", new String[]{"ban", channel, user, time});
		verify(RacesAndClasses.getPlugin().getChannelManager()).banPlayer(sender, user, channel, Integer.MAX_VALUE);
	}
	
	@Test
	public void unban_player_from_channel_with_wrong_amount_of_args_fails(){
		sut.onCommand(sender, null, "", new String[]{"unban"});
		sut.onCommand(sender, null, "", new String[]{"unban", "arg2"});
		sut.onCommand(sender, null, "", new String[]{"unban", "arg2", "arg3", "arg4"});
		
		verify(sender, times(3)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel unban <channelname> <playername>");
	}

	
	@Test
	public void unban_player_from_channel_works(){
		String channel = "channel";
		String user = "user";
		
		sut.onCommand(sender, null, "", new String[]{"unban", channel, user});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).unbanPlayer(sender, user, channel);
	}
	
	
	//Mute
	
	@Test
	public void mute_player_from_channel_with_wrong_amount_of_args_fails(){
		sut.onCommand(sender, null, "", new String[]{"mute"});
		sut.onCommand(sender, null, "", new String[]{"mute", "arg2"});
		sut.onCommand(sender, null, "", new String[]{"mute", "arg2", "arg3", "arg4" , "arg5"});
		
		verify(sender, times(3)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel mute <channelname> <playername> [time in sec]");
	}

	
	@Test
	public void mute_player_from_channel_works(){
		String channel = "channel";
		String user = "user";
		String time = "42";
		int timeAsInt = 42;
		
		sut.onCommand(sender, null, "", new String[]{"mute", channel, user, time});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).mutePlayer(sender, user, channel, timeAsInt);
	}
	
	@Test
	public void mute_player_from_channel_works_when_no_time_passed(){
		String channel = "channel";
		String user = "user";
		
		sut.onCommand(sender, null, "", new String[]{"mute", channel, user});
		verify(RacesAndClasses.getPlugin().getChannelManager()).mutePlayer(sender, user, channel, Integer.MAX_VALUE);
	}
	
	@Test
	public void mute_player_from_channel_works_when_incorrect_time_passed(){
		String channel = "channel";
		String user = "user";
		String time = "invalid";
		
		sut.onCommand(sender, null, "", new String[]{"mute", channel, user, time});
		verify(RacesAndClasses.getPlugin().getChannelManager()).mutePlayer(sender, user, channel, Integer.MAX_VALUE);
	}
	
	@Test
	public void unmute_player_from_channel_with_wrong_amount_of_args_fails(){
		sut.onCommand(sender, null, "", new String[]{"unmute"});
		sut.onCommand(sender, null, "", new String[]{"unmute", "arg2"});
		sut.onCommand(sender, null, "", new String[]{"unmute", "arg2", "arg3", "arg4"});
		
		verify(sender, times(3)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel unmute <channelname> <playername>");
	}

	
	@Test
	public void unmute_player_from_channel_works(){
		String channel = "channel";
		String user = "user";
		
		sut.onCommand(sender, null, "", new String[]{"unmute", channel, user});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).unmutePlayer(sender, user, channel);
	}
	
	
	//EDIT
	
	@Test
	public void edit_channel_fails_for_wrong_args(){
		sut.onCommand(sender, null, "", new String[]{"edit", "arg2", "arg3"});
		sut.onCommand(sender, null, "", new String[]{"edit", "arg2", "arg3", "arg4", "arg5"});
		
		verify(sender, times(2)).sendMessage(ChatColor.RED + "Wrong usage. Use: /channel edit <channelname> <channelproperty> <newValue>");
	}
	
	@Test
	public void edit_channel_works(){
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.PrivateChannel);
		
		String channel = "channel";
		String property = "property";
		String newValue = "Banane";
		
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).editChannel(sender, channel, property, newValue);
	}
	
	@Test
	public void edit_channel_fails_on_channel_not_found(){
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.NONE);
		
		String channel = "channel";
		String property = "property";
		String newValue = "Banane";
		
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});
		
		verify(sender).sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channel);
	}

	@Test
	public void edit_channel_fails_because_no_permission_to_edit(){
		String channel = "channel";
		String property = "property";
		String newValue = "Banane";
		
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.GlobalChannel);
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});
		
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.WorldChannel);
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});

		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.RaceChannel);
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});
		
		verify(RacesAndClasses.getPlugin().getPermissionManager(), times(3)).checkPermissions(sender, PermissionNode.channelEdit);
	}
	
	@Test
	public void edit_channel_works_on_global_world_race_channel_with_permission(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.channelEdit)).thenReturn(true);
		
		String channel = "channel";
		String property = "property";
		String newValue = "Banane";
		
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.GlobalChannel);
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});
		
		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.WorldChannel);
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});

		when(RacesAndClasses.getPlugin().getChannelManager().getChannelLevel(anyString())).thenReturn(ChannelLevel.RaceChannel);
		sut.onCommand(sender, null, "", new String[]{"edit", channel, property, newValue});
		
		verify(RacesAndClasses.getPlugin().getChannelManager(), times(3)).editChannel(sender, channel, property, newValue);
	}
	
	@Test
	public void posting_help_when_no_command_found(){
		sut.onCommand(sender, null, "", new String[]{"invalid"});
		
		verifyHelp(sender);
	}
}

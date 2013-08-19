package de.tobiyas.racesandclasses.commands.classes;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.mockito.Mockito;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_ClassTest {

	private CommandExecutor_Class sut;
	
	private CommandSender consoleSender;
	private String consoleSenderName = "console";
	
	private Player player;
	private String playerName = "player1";
	
	
	@Before
	public void init(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
	
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_classes_enable()).thenReturn(true);
		
		sut = new CommandExecutor_Class();
		sut.addObserver(RacesAndClasses.getPlugin().getTutorialManager());
		
		consoleSender = mock(CommandSender.class);
		when(consoleSender.getName()).thenReturn(consoleSenderName);
		
		player = mock(Player.class);
		when(player.getName()).thenReturn(playerName);
	}
	
	
	@After
	public void tearDown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	@Test
	public void registering_command_works(){
		String commandName = "class";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_Class();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	//Classes inactive
	
	@Test
	public void test_class_inactive_gives_early_out(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_classes_enable()).thenReturn(false);
		
		sut.onCommand(consoleSender, null, "", new String[] {});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "Classes are not activated.");
	}
	
	//Send help
	
	private void verifyHelp(CommandSender sender, boolean permissions){
		verify(sender).sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		verify(sender).sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "info");
		verify(sender).sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "list");

		verify(sender, permissions ? times(1) : never()).sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<classname>");
		verify(sender, permissions ? times(1) : never()).sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<classname>");
	}
	
	
	@Test
	public void test_help_with_no_permissions_from_console_sender(){
		sut.onCommand(consoleSender, null, "", new String[] {});
		
		verifyHelp(consoleSender, false);
	}
	
	@Test
	public void test_sending_wrong_command_sends_help_from_console_sender(){
		sut.onCommand(consoleSender, null, "", new String[] {"wrongCommand"});
		
		verifyHelp(consoleSender, false);
	}
	
	@Test
	public void test_help_with_permissions_from_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(any(CommandSender.class), anyString())).thenReturn(true);
		
		sut.onCommand(consoleSender, null, "", new String[] {});
		
		verifyHelp(consoleSender, true);
	}
	
	
	//Info command
	
	@Test
	public void test_info_with_console_sender(){
		String command = "info";
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "You have no class selected. Use " + ChatColor.LIGHT_PURPLE + "/class info <class name>" 
							+ ChatColor.RED + "  to inspect a class.");
	}
	
	@Test
	public void test_info_with_unknown_class_and_console_sender(){
		String command = "info";
		String classToSelect = "Banane";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, classToSelect});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + classToSelect 
				+ ChatColor.RED + " does not exist.");
	}
	
	@Test
	public void test_info_with_classes_present_from_console_sender(){
		String command = "info";
		
		List<String> classList = new LinkedList<String>();
		classList.add("class");
		
		when(RacesAndClasses.getPlugin().getClassManager().getAllHolderNames()).thenReturn(classList);
		ClassContainer classContainer = mock(ClassContainer.class);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(consoleSenderName)).thenReturn(classContainer);
		when(classContainer.getName()).thenReturn("class");
		when(classContainer.getTag()).thenReturn("[class]");
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "ClassInfo" + ChatColor.YELLOW + " =====");
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.LIGHT_PURPLE + classContainer.getName());		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "ClassTag: " + ChatColor.LIGHT_PURPLE + classContainer.getTag());
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "==== " + ChatColor.RED + "Class Traits" + ChatColor.YELLOW +" =====");
	}
	
	
	//List command
	
	@Test
	public void test_list_with_console_sender(){
		String command = "list";
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		verify(consoleSender).sendMessage(ChatColor.RED + "No Classes in the list.");
	}

	@Test
	public void test_list_with_multiple_classes_console_sender(){
		String command = "list";
		
		List<String> classList = new LinkedList<String>();
		classList.add("class1");
		classList.add("class2");
		classList.add("class3");
		
		when(RacesAndClasses.getPlugin().getClassManager().getAllHolderNames()).thenReturn(classList);
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		for(String clazz : classList){
			verify(consoleSender).sendMessage(ChatColor.BLUE + clazz);
		}
	}
	
	@Test
	public void test_list_with_multiple_classes_and_own_class_console_sender(){
		String command = "list";
		
		List<String> classList = new LinkedList<String>();
		classList.add("class1");
		classList.add("class2");
		classList.add("class3");
		
		when(RacesAndClasses.getPlugin().getClassManager().getAllHolderNames()).thenReturn(classList);
		ClassContainer classContainer = mock(ClassContainer.class);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(consoleSenderName)).thenReturn(classContainer);
		when(classContainer.getName()).thenReturn("class1");
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
				
		verify(consoleSender).sendMessage(ChatColor.RED + "class1" + ChatColor.YELLOW + "  <-- Your Class!");
		verify(consoleSender).sendMessage(ChatColor.BLUE + "class2");
		verify(consoleSender).sendMessage(ChatColor.BLUE + "class3");
	}
	
	
	@Test
	public void test_list_with_player_triggers_observer(){
		String command = "list";
		
		sut.onCommand(player, null, "", new String[] {command});
		verify(RacesAndClasses.getPlugin().getTutorialManager()).update(any(CommandExecutor_Class.class), any(TutorialStepContainer.class));
	}
	
	//selecting
	
	@Test
	public void test_select_with_console_sender_fails(){
		String command = "select";
		String clazz = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, clazz});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "[RAC] Only players can use this command.");
	}
	
	@Test
	public void test_player_selects_class_without_permission_fails(){
		String command = "select";
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player, never()).sendMessage(anyString());
	}
	
	@Test
	public void test_player_opening_class_with_no_container_selection_screen(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect()).thenReturn(true);
		
		String command = "select";
		List<String> classList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(ChatColor.RED + "[RaC] You don't have any Classes to select.");
	}
	
	@Test
	public void test_player_opening_class_selection_screen(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect()).thenReturn(true);
		
		String command = "select";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);		
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(ChatColor.GREEN + "Opening Class Selection...");
	}
	
	@Test
	public void test_player_opening_class_selection_fails_because_already_has_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect()).thenReturn(true);
		
		String command = "select";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(ChatColor.RED + "You already have a class: " + ChatColor.AQUA + classContainer.getName()
				+ ChatColor.RED + ". Use " + ChatColor.LIGHT_PURPLE + "/class change" + ChatColor.RED + " to change your class.");
	}
	
	
	//Changing
	
	@Test
	public void test_change_with_console_sender_fails(){
		String command = "change";
		String clazz = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, clazz});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "[RAC] Only players can use this command.");
	}
	
	@Test
	public void test_player_opening_class_changer_with_no_container_selected_screen(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect()).thenReturn(true);
		
		String command = "change";
		List<String> classList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(ChatColor.RED + "You don't have any class. Use " + ChatColor.LIGHT_PURPLE + "/class select" 
				+ ChatColor.RED + " to select a class.");
	}
	
	
	
	@Test
	public void test_player_opening_class_with_no_changeable_class_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect()).thenReturn(true);
		
		String command = "change";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(ChatColor.RED + "[RaC] You don't have any Classes to select.");
	}
	
	@Test
	public void test_player_opening_class_change_success_because_already_has_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect()).thenReturn(true);
		
		String command = "change";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(ChatColor.GREEN + "Opening Class Selection...");
	}
	
	////////////////////////
	//CHAT INTERACTION    //
	////////////////////////
	
	//selecting
	
	@Test
	public void test_select_with_console_sender_fails_chat(){		
		String command = "select";
		String clazz = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, clazz});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "[RAC] Only players can use this command.");
	}
	
	
	@Test
	public void test_player_selecting_class_with_no_container_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		String command = "select";
		
		List<String> classList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verifyHelp(player, false);
	}
	
	@Test
	public void test_player_selecting_class_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		String command = "select";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);		
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		when(RacesAndClasses.getPlugin().getClassManager().addPlayerToHolder(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + className);
		
		verify(RacesAndClasses.getPlugin().getTutorialManager()).update(any(CommandExecutor_Class.class), any(TutorialStepContainer.class));
	}
	
	
	@Test
	public void test_player_selecting_class_chat_fails_because_already_has_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		String command = "select";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);		
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.RED + "You already have a class: " + ChatColor.LIGHT_PURPLE + classContainer.getName());
	}
	
	
	@Test
	public void test_player_selecting_class_chat_fails_because_not_exist_has_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.selectClass)).thenReturn(true);
		String command = "select";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + className + ChatColor.RED + " was not found.");
	}
	
	
	//changing

	@Test
	public void test_change_with_console_sender_fails_chat(){		
		String command = "change";
		String clazz = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, clazz});
		
		verify(consoleSender).sendMessage(ChatColor.RED + "[RAC] Only players can use this command.");
	}
	
	
	@Test
	public void test_player_changing_class_with_no_container_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		String command = "change";
		
		List<String> classList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verifyHelp(player, false);
	}
	
	@Test
	public void test_player_changing_class_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getClassManager().changePlayerHolder(anyString(), anyString())).thenReturn(true);
		
		String command = "change";
		String className = "class1";
		String currentClass = "class2";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);
		
		ClassContainer classContainerCurrent = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(currentClass);		
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(currentClass)).thenReturn(classContainerCurrent);
		
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainerCurrent);
		
		when(RacesAndClasses.getPlugin().getClassManager().addPlayerToHolder(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + className);
	}
	
	@Test
	public void test_player_changing_class_chat_fails_internaly(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getClassManager().changePlayerHolder(anyString(), anyString())).thenReturn(false);
		
		String command = "change";
		String className = "class1";
		String currentClass = "class2";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);
		
		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);
		
		ClassContainer classContainerCurrent = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(currentClass);		
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(currentClass)).thenReturn(classContainerCurrent);
		
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainerCurrent);
		
		when(RacesAndClasses.getPlugin().getClassManager().addPlayerToHolder(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + className + ChatColor.RED + " was not found.");
	}
	
	@Test
	public void test_player_changing_to_same_class_chat_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		String command = "change";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);		
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		when(RacesAndClasses.getPlugin().getClassManager().addPlayerToHolder(anyString(), anyString())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + className);
	}
	
	
	@Test
	public void test_player_changing_class_chat_fails_because_has_no_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		String command = "change";
		String className = "class1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(className);

		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(className);
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderByName(className)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getClassManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.RED + "You have no class you could change");
	}
	
	
	@Test
	public void test_player_changing_class_chat_fails_because_not_exist_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(player, PermissionNode.changeClass)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		String command = "change";
		String className = "class1";
		
		ClassContainer classContainer = mock(ClassContainer.class, Mockito.RETURNS_DEEP_STUBS);
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player).sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + className + ChatColor.RED + " was not found.");
	}
	
	@Test
	public void test_player_changing_class_chat_without_permissions_fails(){
		String command = "change";
		String className = "class1";
		
		when(RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(player, never()).sendMessage(anyString());
		verify(RacesAndClasses.getPlugin().getPermissionManager()).checkPermissions(player, PermissionNode.changeClass);
	}


}

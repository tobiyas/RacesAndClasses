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
package de.tobiyas.racesandclasses.commands.help;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class PostPageTest {

	private static String name = "Banane";
	
	@Before
	public void before(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		GenerateBukkitServer.generatePlayerOnServer(name);
	}
	
	@After
	public void tierdown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	
	@Test
	public void post_page1_help(){
		List<String> response = HelpPage.getPageContent(name, 1);
		assertEquals(7, response.size());

		response = HelpPage.getCategoryPage(name, "help");
		assertEquals(7, response.size());
	}
	
	
	@Test
	public void post_page2_races(){
		List<String> response = HelpPage.getPageContent(name, 2);
		assertEquals(6, response.size());
		
		response = HelpPage.getCategoryPage(name, "races");
		assertEquals(6, response.size());
	}
	
	
	@Test
	public void post_page3_classes(){
		List<String> response = HelpPage.getPageContent(name, 3);
		assertEquals(6, response.size());
		
		response = HelpPage.getCategoryPage(name, "classes");
		assertEquals(6, response.size());
	}
	
	@Test
	public void post_page4_chat(){
		List<String> response = HelpPage.getPageContent(name, 4);
		assertEquals(5, response.size());
		
		response = HelpPage.getCategoryPage(name, "chat");
		assertEquals(5, response.size());
	}
	
	
	@Test
	public void post_page5_channel(){
		List<String> response = HelpPage.getPageContent(name, 5);
		assertEquals(9, response.size());
		
		response = HelpPage.getCategoryPage(name, "channel");
		assertEquals(9, response.size());
	}
	
	
	@Test
	public void post_page6_config(){
		List<String> response = HelpPage.getPageContent(name, 6);
		assertEquals(3, response.size());
		
		response = HelpPage.getCategoryPage(name, "config");
		assertEquals(3, response.size());
	}
	
	
	@Test
	public void post_page7_general(){
		List<String> response = HelpPage.getPageContent(name, 7);
		assertEquals(2, response.size());
		
		response = HelpPage.getCategoryPage(name, "general");
		assertEquals(2, response.size());
	}
	
	@Test
	public void post_page7_general_with_all_permissions(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(any(CommandSender.class), 
				anyString())).thenReturn(true);
		
		
		List<String> response = HelpPage.getPageContent(name, 7);
		assertEquals(8, response.size());
		
		response = HelpPage.getCategoryPage(name, "general");
		assertEquals(8, response.size());
	}
	
	
	@Test
	public void post_page8_invalid_sending_page1(){
		List<String> response = HelpPage.getPageContent(name, 8);
		assertEquals(HelpPage.getPageContent(name, 1), response);
		
		response = HelpPage.getCategoryPage(name, "invalid");
		assertEquals(HelpPage.getCategoryPage(name, "help"), response);
	}
}

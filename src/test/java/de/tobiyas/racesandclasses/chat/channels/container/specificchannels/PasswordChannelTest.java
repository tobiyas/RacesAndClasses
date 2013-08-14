package de.tobiyas.racesandclasses.chat.channels.container.specificchannels;

import de.tobiyas.racesandclasses.chat.channels.container.AbstractChannelContainerTest;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelContainer;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelInvalidException;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;

public class PasswordChannelTest extends AbstractChannelContainerTest {

	@Override
	protected ChannelContainer generateSut() throws ChannelInvalidException {
		ChannelContainer container = new ChannelContainer(channelName, ChannelLevel.PasswordChannel);
		this.neededPW = "234";
		container.setPassword(neededPW);
		
		return container;
	}
	
	
	@Override
	public void setting_password_works(){
		//TODO
	}

}

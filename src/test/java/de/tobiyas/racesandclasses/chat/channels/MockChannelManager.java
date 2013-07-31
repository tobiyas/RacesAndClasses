package de.tobiyas.racesandclasses.chat.channels;

import org.mockito.Mockito;

public class MockChannelManager {

	public static void generateMock(){
		ChannelManager.instance = Mockito.mock(ChannelManager.class, Mockito.RETURNS_SMART_NULLS);
	}
	
	public static void dropMock(){
		ChannelManager.instance = null;
	}
}

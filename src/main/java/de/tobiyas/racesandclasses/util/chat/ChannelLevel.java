package de.tobiyas.racesandclasses.util.chat;

import javax.persistence.Embeddable;

@Embeddable
public enum ChannelLevel {
	PrivateChannel,
	PublicChannel,
	PasswordChannel,
	SystemChannel,
	
	WorldChannel,
	LocalChannel,
	GlobalChannel,
	RaceChannel,
	
	Other,
	NONE;
}

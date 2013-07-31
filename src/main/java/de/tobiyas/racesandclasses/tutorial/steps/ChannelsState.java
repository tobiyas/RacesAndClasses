package de.tobiyas.racesandclasses.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.tutorial.TutorialPath;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class ChannelsState extends AbstractMoreStepsStep {

	public ChannelsState(String playerName, TutorialPath writeBack){
		this.playerName = playerName;
		this.writeBack = writeBack;
		this.currentState = TutorialState.channels;
		
		this.startMessage = "";
		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very Good!";
		
		finished = false;
		currentStep = 1;
		stepStartMessage = addInternStartMessages();
		
		maxSteps = stepStartMessage.length;
		postStartMessage(currentStep);
	}
	
	private String[] addInternStartMessages(){
		//list Channel
		String step1 = ChatColor.YELLOW + "[Tutorial]: Now lets come to the Channels! The Chat system is seperated into " + ChatColor.RED +
				"incoming" + ChatColor.YELLOW + " and " + ChatColor.RED + " outgoing " + ChatColor.YELLOW + "Messages. " + 
				"First step is to look at all channels. To do so, use " + ChatColor.RED + "/channel list" + ChatColor.YELLOW + ".";;
		
		//join Channel
		String step2 = ChatColor.YELLOW + "[Tutorial]: Now I will show you how to join a channel. There is a Tutorial Channel named " + ChatColor.RED +
					" Tutorial" + ChatColor.YELLOW +". To get to the next step, you have to join this channel." +
					"To join a channel use " + ChatColor.RED + "/channel join <ChannelName>" + ChatColor.YELLOW + ".";
		
		//info for channel
		String step3 = ChatColor.YELLOW + "[Tutorial]: Now I will show you how to get infos to a specific Channel. " +
					"To get information of a channel you can use " + ChatColor.RED +"/channel info <ChannelName>" + 
					ChatColor.YELLOW + ". To get to the next step, you have to have to look at the informations of the channel " + 
					ChatColor.RED + "Tutorial" + ChatColor.YELLOW + ".";
		
		//switch Channel to tutorialChannel
		String step4 = ChatColor.YELLOW + "[Tutorial]: Now I will show you how to " + ChatColor.RED + "switch" + ChatColor.YELLOW + 
					" a channel to post into it. To change your actual output Channel, use " + ChatColor.RED + "/channel <post/switch/change> <ChannelName>" +
					ChatColor.YELLOW + ". To get to the next step switch your outputchannel to " + ChatColor.RED + "Tutorial" +
					ChatColor.YELLOW + ".";
		
		//post message in channel
		String step5 = ChatColor.YELLOW + "[Tutorial]: Now I will show you how to " + ChatColor.RED + "post" + ChatColor.YELLOW + 
				" a message in a Channel to post into it. To do so, just type in a message to the chat. It will be redirected to your current Output-Channel. " + 
				"To get to the next step send a any message to your current Output-Channel.";
		
		//leave channel
		String step6 = ChatColor.YELLOW + "[Tutorial]: Now I will show you how to " + ChatColor.RED + "leave" + ChatColor.YELLOW + 
				" a joined Channel. To leave a Channel use " + ChatColor.RED + "/channel leave <ChannelName>" + ChatColor.YELLOW + ". " + 
				"To get to the next step leave the " + ChatColor.RED + "Tutorial" + ChatColor.YELLOW + " Channel.";
		
		return new String[]{step1, step2, step3, step4, step5, step6};
	}
	
	@Override
	protected void postEndMessage(int stage) {
		this.sendMessageToPlayer(ChatColor.YELLOW + "[Tutorial]: Very Good!");
	}

}

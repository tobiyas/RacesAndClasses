package de.tobiyas.racesandclasses.util.tasks;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class DebugTask implements Runnable, Observer{
	private RacesAndClasses plugin;
	
	private int taskID = -1;
	
	private long ticksDone;
	private HashMap<AbstractRunnable, Integer> tasks;

	private static DebugTask thisTask;
	
	private int crashAndRestart = 0;

	
	private DebugTask(){
		plugin = RacesAndClasses.getPlugin();
		ticksDone = 0;
		
		tasks = new HashMap<AbstractRunnable, Integer>();
	}
	
	@Override
	public void run() {
		ticksDone++;
		for(AbstractRunnable runnable : tasks.keySet()){
			if(runnable == null)
				tasks.remove(runnable);
			int ticksTicking = runnable.tickInterval;
			if(ticksDone % ticksTicking != 0) continue;
			
			int reacted = tasks.get(runnable);
			if(reacted > 1){
				System.out.println("possible malfunction of: " + runnable.getName());
				//Handle Possible Misbehavior
				crashAndRestart++;
			}
			
			tasks.put(runnable, reacted++);
		}
	}

	@Override
	public void update(Observable runnable, Object arg) {
		if(runnable instanceof AbstractRunnable)
			tasks.put((AbstractRunnable) runnable, 0);
		
	}
	
	private void registerTaskIntern(AbstractRunnable task){
		tasks.put(task, 1);
	}
	
	private void unregisterTaskIntern(AbstractRunnable task){
		tasks.remove(task);
	}
	
	public int getCrashs(){
		return crashAndRestart;
	}
	
	public void postSenderReport(CommandSender sender){
		sender.sendMessage(ChatColor.GREEN + "Task reported, that " + ChatColor.RED + crashAndRestart + ChatColor.GREEN + " tasks have crashed.");
	}
	
	private DebugTask reInitDebugger(){
		if(taskID != -1)
			Bukkit.getScheduler().cancelTask(taskID);
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
		return this;
	}
	
	
	//STATICS
	public static void registerTask(AbstractRunnable task){
		if(thisTask == null) return;
		thisTask.registerTaskIntern(task);
	}
	
	public static void unregisterTask(AbstractRunnable task){
		if(thisTask == null) return;
		thisTask.unregisterTaskIntern(task);
	}
	
	public static void initDebugger(){
		if(thisTask == null)
			thisTask = new DebugTask().reInitDebugger();
		else
			thisTask.reInitDebugger();
	}

	public static void senderCall(CommandSender sender) {
		if(thisTask == null)
			sender.sendMessage(ChatColor.RED + "DebugTask is not running! Try reloading the plugin.");
		else
			thisTask.postSenderReport(sender);
	}

}

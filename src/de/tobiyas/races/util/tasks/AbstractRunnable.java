package de.tobiyas.races.util.tasks;

import java.util.Observable;

public abstract class AbstractRunnable extends Observable implements Runnable {

	protected int tickInterval = 20;
	protected boolean notifyDebugger = false;
	
	@Override
	public void run(){
		checkNotifyDebugger();
		normalRun();
	}
	
	protected void checkNotifyDebugger(){
		if(!notifyDebugger)
			return;
		this.notifyObservers();
		this.setChanged();
	}
	
	protected abstract void normalRun();
	
	protected abstract String getName();
	
	protected void registerRunnable(){
		DebugTask.registerTask(this);
	}
	
	protected void unregisterRunnable(){
		DebugTask.unregisterTask(this);
	}
	
	public int notifyOnTick(DebugTask task){
		notifyDebugger = true;
		this.addObserver(task);
		return tickInterval;
	}
	
	public void stopNotifying(){
		notifyDebugger = false;
		this.deleteObservers();
	}

}

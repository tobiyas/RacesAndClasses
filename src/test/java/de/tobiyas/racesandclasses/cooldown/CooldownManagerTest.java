package de.tobiyas.racesandclasses.cooldown;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.cooldown.CooldownManager.UplinkReducingTask;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CooldownManagerTest {

	private CooldownManager sut;
	
	@Before
	public void setupServerMock(){
		sut = new CooldownManager();
		GenerateBukkitServer.generateServer();
	}
	
	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
	}
	
	
	@Test
	public void addingPersonToMapAddsPerson(){
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		
		sut.setCooldown(playerName, commandName, time);
		
		Assert.assertEquals(time, sut.stillHasCooldown(playerName, commandName));
	}
	
	
	@Test
	public void playerDoesNotHaveUplinkOnOtherCommandWhenNotUsed(){
		String playerName = "test";
		String commandName = "ping";
		String otherCommandName = "hallo";
		int time = 42;
		
		sut.setCooldown(playerName, commandName, time);
		
		Assert.assertEquals(-1, sut.stillHasCooldown(playerName, otherCommandName));
	}
	
	
	@Test
	public void tickingMapReducesUplink(){
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		
		sut.setCooldown(playerName, commandName, time);
		
		sut.tick();
		
		Assert.assertEquals(time - 1, sut.stillHasCooldown(playerName, commandName));
	}
	
	
	@Test
	public void tickingMapBelowZeroSecRemovesPlayer(){
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		
		sut.setCooldown(playerName, commandName, time);
		
		time--; //just for synchronizing with first tick
		
		for(int i = time; i > 0; i--){
			sut.tick();
			Assert.assertEquals(i, sut.stillHasCooldown(playerName, commandName));
		}
		
		sut.tick(); //doing one final tick to remove
		Assert.assertEquals(-1, sut.stillHasCooldown(playerName, commandName));
	}
	
	
	@Test
	public void gettingCooldownOfAPlayerReturnsAllOwnCooldowns(){
		String correctPlayerName = "correct";
		String cooldownstring1 = "test1";
		String cooldownstring2 = "test2";
		String cooldownstring3 = "test3";
		String cooldownstring4 = "test4";
		
		String wrongPlayerName = "incorrect";
		
		sut.setCooldown(correctPlayerName, cooldownstring1, 42);
		sut.setCooldown(correctPlayerName, cooldownstring3, 42);
		sut.setCooldown(correctPlayerName, cooldownstring4, 42);

		sut.setCooldown(wrongPlayerName, cooldownstring1, 42);
		sut.setCooldown(wrongPlayerName, cooldownstring2, 42);
		sut.setCooldown(wrongPlayerName, cooldownstring4, 42);
		
		List<String> cooldownList = sut.getAllCooldownsOfPlayer(correctPlayerName);
		Assert.assertEquals(3, cooldownList.size());
		
		Assert.assertTrue(cooldownList.contains(cooldownstring1));
		Assert.assertTrue(cooldownList.contains(cooldownstring3));
		Assert.assertTrue(cooldownList.contains(cooldownstring4));
	}
	
	
	@Test
	public void testingEarlyOutOfTicker(){		
		sut.tick(); //has to be checked in code coverage if passed.
	}
	
	
	@Test
	public void registeringSchedulerTaskWorks(){
		sut.init(); //No errors should be thrown here
	}
	
	
	@Test
	public void removingSchedulerTaskWorks(){
		sut.init();
		sut.shutdown(); //No errors should be thrown
	}
	
	
	@Test
	public void schedulerTaskTicksMap(){
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		
		sut.setCooldown(playerName, commandName, time);
		
		UplinkReducingTask task = sut.new UplinkReducingTask();
		
		task.run();
		Assert.assertEquals(time - 1, sut.stillHasCooldown(playerName, commandName));
	}
	
	
	@Test
	public void synchronizeOnSetWorks() throws Exception{
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		
		Thread locket = new LockerThread();
		locket.start();
		
		Thread.sleep(2);
		
		long timeBefore = System.currentTimeMillis();
		sut.setCooldown(playerName, commandName, time);
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		Assert.assertTrue("Synchronized Block is not blocking.", timeNeeded >= timeToLock - (timeToLock / 5)); 
		//20% time lost due to java Threads being pretty unreliable
	}

	
	@Test
	public void synchronizeOnReadWorks() throws Exception{
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		sut.setCooldown(playerName, commandName, time);
		
		LockerThread locket = new LockerThread();
		locket.start();
		
		Thread.sleep(2);
		
		long timeBefore = System.currentTimeMillis();
		sut.stillHasCooldown(playerName, commandName);
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		Assert.assertTrue("Synchronized Block is not blocking.", timeNeeded >= timeToLock - (timeToLock / 5)); 
		//20% time lost due to java Threads being pretty unreliable
	}
	
	
	@Test
	public void synchronizeOnTickWorks() throws Exception{
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		sut.setCooldown(playerName, commandName, time);
		
		Thread locket = new LockerThread();
		locket.start();
		
		Thread.sleep(2);
		
		long timeBefore = System.currentTimeMillis();
		sut.tick();
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		Assert.assertTrue("Synchronized Block is not blocking.", timeNeeded >= timeToLock - (timeToLock / 5)); 
		//20% time lost due to java Threads being pretty unreliable
	}
	
	
	
	private static final int timeToLock = 30;
	
	//Locking
    private class LockerThread extends Thread {
    	
        @Override
        public void run() {
            synchronized (sut.cooldownList) {
                try {
                    Thread.sleep(timeToLock);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    };
}

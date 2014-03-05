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
package de.tobiyas.racesandclasses.cooldown;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tobiyas.racesandclasses.cooldown.CooldownManager.UplinkReducingTask;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CooldownManagerTest {

	private CooldownManager sut;
	
	@Before
	public void setupServerMock(){
		GenerateBukkitServer.generateServer();
		sut = new CooldownManager();
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
	
	
	@Ignore("Synchronized Testing not easy possible...")
	@Test
	public void synchronizeOnSetWorks() throws Exception{
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		
		Thread locket = new LockerThread(this);
		locket.start();
		
		Thread.sleep(5);
		
		long timeBefore = System.currentTimeMillis();
		sut.setCooldown(playerName, commandName, time);
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		double lossRelevantLockTime = timeToLock * 0.5;
		Assert.assertTrue("Synchronized Block is not blocking. Timings - timeNeeded: " + timeNeeded + ", Time wanted > than: " + lossRelevantLockTime, 
				timeNeeded >= lossRelevantLockTime); 
		//50% time lost due to java Threads being pretty unreliable
	}

	
	@Ignore("Synchronized Testing not easy possible...")
	@Test
	public void synchronizeOnReadWorks() throws Exception{
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		sut.setCooldown(playerName, commandName, time);
		
		LockerThread locket = new LockerThread(this);
		locket.start();
		
		Thread.sleep(5);
		
		long timeBefore = System.currentTimeMillis();
		sut.stillHasCooldown(playerName, commandName);
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		double lossRelevantLockTime = timeToLock * 0.5;
		Assert.assertTrue("Synchronized Block is not blocking. Timings - timeNeeded: " + timeNeeded + ", Time wanted > than: " + lossRelevantLockTime, 
				timeNeeded >= lossRelevantLockTime); 
		//50% time lost due to java Threads being pretty unreliable
	}
	
	
	@Ignore("Synchronized Testing not easy possible...")
	@Test
	public void synchronizeOnTickWorks() throws Exception{
		String playerName = "test";
		String commandName = "ping";
		int time = 42;
		sut.setCooldown(playerName, commandName, time);
		
		Thread locket = new LockerThread(this);
		locket.start();
		
		Thread.sleep(5);
		
		long timeBefore = System.currentTimeMillis();
		sut.tick();
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		double lossRelevantLockTime = timeToLock * 0.5;
		Assert.assertTrue("Synchronized Block is not blocking. Timings - timeNeeded: " + timeNeeded + ", Time wanted > than: " + lossRelevantLockTime, 
				timeNeeded >= lossRelevantLockTime); 
		//50% time lost due to java Threads being pretty unreliable
	}
	
	
	
	private static final int timeToLock = 30;
	
	//Locking
    private class LockerThread extends Thread {
    	
    	private LockerThread(CooldownManagerTest testToWake) {
		}
    	
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

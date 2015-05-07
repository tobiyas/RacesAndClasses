package de.tobiyas.racesandclasses.entitystatusmanager.buffs;

import java.util.Random;

public class BuffContainer {

	/**
	 * The Rand to use for IDs.
	 */
	private static final Random rand = new Random();
	
	
	/**
	 * When the buff ends.
	 */
	private final long ends;
	
	/**
	 * The Name of the Buff.
	 */
	private final String name;
	
	/**
	 * The ID of the buff.
	 */
	private final int id;
	
	
	public BuffContainer(long ends, String name) {
		this(ends, name, rand.nextInt(Integer.MAX_VALUE));
	}
	
	public BuffContainer(long ends, String name, int id) {
		this.ends = ends;
		this.name = name;
		this.id = id;
	}


	public long getEnds() {
		return ends;
	}


	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
}

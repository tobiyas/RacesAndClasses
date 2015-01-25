package de.tobiyas.racesandclasses.vollotile;

import org.bukkit.util.Vector;


public class ParticleContainer {

	private final ParticleEffects effect;
	private final Vector vec;
	private final int amount;
	private final float data;

	
	public ParticleContainer(ParticleEffects effect, Vector vec, int amount,
			float data) {
	
		this.effect = effect;
		this.vec = vec;
		this.amount = amount;
		this.data = data;
	}
	
	
	public ParticleContainer(ParticleEffects effect, int amount,
			float data) {
		
		this(effect, new Vector(0.1,0.1,0.1), amount, data);
	}


	public ParticleEffects getEffect() {
		return effect;
	}


	public Vector getVec() {
		return vec;
	}


	public int getAmount() {
		return amount;
	}


	public float getData() {
		return data;
	}
	
	
	/**
	 * Generates a {@link ParticleContainer}.
	 * Parseable: EFFECT#AMOUNT#DATA#VECTOR_X#VECTOR_Y#VECTOR_Z
	 * 
	 * @param toParse the String to parse.
	 * 
	 * @return the Particle Container.
	 */
	public static ParticleContainer generate(String toParse){
		String[] split = toParse.split("#");
		try{
			String particleEffectName = split[0];
			
			for(de.tobiyas.util.vollotile.ParticleEffects effect2 : de.tobiyas.util.vollotile.ParticleEffects.values()){
				if(effect2.getPacketArg().equalsIgnoreCase(particleEffectName)){
					particleEffectName = effect2.name();
					break;
				}
			}
			
			de.tobiyas.racesandclasses.vollotile.ParticleEffects effect = de.tobiyas.racesandclasses.vollotile.ParticleEffects.FIREWORKS_SPARK;
			if(effect == null) ParticleEffects.valueOf(split[0]);
			int amount = effect.asBukkit() != null ? 1 : 30;
			try{ amount = Integer.parseInt(split[1]); }catch(Throwable exp){}

			float data = 0;
			try{ data = Float.parseFloat(split[2]); }catch(Throwable exp){}
			
			Vector vec = new Vector(0.1,0.1,0.1);
			try{
				double x = Double.parseDouble(split[3]);
				double y = Double.parseDouble(split[4]);
				double z = Double.parseDouble(split[5]);
				vec = new Vector(x,y,z);
			}catch(Throwable exp){}
			
			return new ParticleContainer(effect, vec, amount, data);
		}catch(Throwable exp){
			return null;
		}
	}
	
}

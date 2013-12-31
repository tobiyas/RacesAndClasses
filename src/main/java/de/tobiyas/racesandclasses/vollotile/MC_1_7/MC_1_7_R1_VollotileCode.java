package de.tobiyas.racesandclasses.vollotile.MC_1_7;

import java.lang.reflect.Method;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.vollotile.VollotileCode;

public class MC_1_7_R1_VollotileCode extends VollotileCode {

	public MC_1_7_R1_VollotileCode() {
		super("v1_7_R1");
	}

	
	@Override
	public void playCriticalHitEffect(Player player, Entity toPlayEffect) {
		try{
			Object mcEntity = getMCEntityFromBukkitEntity(toPlayEffect);
			Object mcPlayer = getMCEntityFromBukkitEntity(player);
			
			Method playOutAnnimation= mcPlayer.getClass().getDeclaredMethod("b", Class.forName("net.minecraft.server." + CB_RELOCATION + ".Entity"));
			playOutAnnimation.invoke(mcPlayer, mcEntity);
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
}

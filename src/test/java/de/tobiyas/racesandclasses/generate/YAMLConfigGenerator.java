package de.tobiyas.racesandclasses.generate;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLConfigGenerator {

	/**
	 * Generates an unloaded config in a random temp place
	 * 
	 * @return the config.
	 */
	public static YAMLConfigExtended generateConfig(){
		try {
			File randomFile = File.createTempFile("random", ".yml", GenerateRaces.createTempDirectory());
			return new YAMLConfigExtended(randomFile);
		} catch (IOException e) {
			Assert.fail(e.getLocalizedMessage());
			return null; //actually dead code...
		}
	}
}
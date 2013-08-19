package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolderTest;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.util.config.YAMLConfigExtended;


public class ClassContainerTest extends AbstractTraitHolderTest {

	public ClassContainerTest() throws IOException {
		super("test", "[test]",	new ClassContainer(	new YAMLConfigExtended(File.createTempFile("classes", ".yml")), "test"));
	}
	
	

	@Override
	protected AbstractHolderManager getHolderManager() {
		return RacesAndClasses.getPlugin().getClassManager();
	}
	

	@Test
	@Override
	public void test_config_load_works_with_full_config() {
		double manabonus = 42;
		String maxHealthMod = "+42";
		
		config.set(holderName + ".config.manabonus", manabonus);
		config.set(holderName + ".config.classtag", holderTag);
		config.set(holderName + ".config.health", maxHealthMod);
			
		try{
			sut.load();
			
			ClassContainer container = (ClassContainer) sut;
			
			assertEquals(manabonus, container.getManaBonus(), 0.001);
			assertEquals(62d, container.modifyToClass(20d), 0.001);
			assertEquals(holderTag, container.getTag());
			
			assertEquals(0, sut.getTraits().size());
		}catch(HolderParsingException exp){
			fail("Config full load failed: " + exp);
		}
	}


	@Test
	@Override
	public void test_config_load_works_with_empty_config() {
		double manabonus = 0;
		
		try{
			sut.load();
			
			ClassContainer container = (ClassContainer) sut;
			
			assertEquals(manabonus, container.getManaBonus(), 0.001);
			assertEquals(20d, container.modifyToClass(20d), 0.001);
			assertEquals(holderTag, container.getTag());
			
			assertEquals(0, sut.getTraits().size());
		}catch(HolderParsingException exp){
			fail("Config full load failed: " + exp);
		}
	}
	
	@Test
	public void classHealthModify_works(){
		String mult = "*2";
		String minus = "-2";
		String plus = "+2";
		
		ClassContainer container = (ClassContainer) sut;
		
		container.classHealthModValue = container.evaluateValue(mult);
		assertEquals(2, container.classHealthModValue, 0.001);
		assertEquals("*", container.classHealthModify);
		assertEquals(40d, container.modifyToClass(20d), 0.001);
		
		
		container.classHealthModValue = container.evaluateValue(minus);
		assertEquals(2, container.classHealthModValue, 0.001);
		assertEquals("-", container.classHealthModify);
		assertEquals(18, container.modifyToClass(20d), 0.001);
		
		
		container.classHealthModValue = container.evaluateValue(plus);
		assertEquals(2, container.classHealthModValue, 0.001);
		assertEquals("+", container.classHealthModify);
		assertEquals(22d, container.modifyToClass(20d), 0.001);
	}

	@Test
	public void empty_load_equals_empty_load() throws IOException{
		try{
			sut.load();
			
			ClassContainer loadContainer = (ClassContainer) ClassContainer
					.loadClass(new YAMLConfigExtended(File.createTempFile("classes", ".yml")), holderName);
			
			ClassContainer container = (ClassContainer) sut;
			
			assertEquals(loadContainer.getManaBonus(), container.getManaBonus(), 0.001);
			assertEquals(loadContainer.modifyToClass(20d), container.modifyToClass(20d), 0.001);
			assertEquals(loadContainer.getTag(), container.getTag());
			
			assertEquals(loadContainer.getTraits().size(), sut.getTraits().size());
		}catch(HolderParsingException exp){
			fail("Config full load failed: " + exp);
		}
	}
}

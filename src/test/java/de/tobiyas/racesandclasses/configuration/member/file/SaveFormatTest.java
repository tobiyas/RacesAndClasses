package de.tobiyas.racesandclasses.configuration.member.file;


import org.junit.Assert;
import org.junit.Test;

import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption.SaveFormat;

public class SaveFormatTest {

	
	@Test
	public void parsingIntWorks(){
		String value = "000hallotest";
		
		SaveFormat format = SaveFormat.parseFromId(value.substring(0, 3));
		Assert.assertEquals(SaveFormat.INT, format);
	}

	@Test
	public void parsingDoubleWorks(){
		String value = "001hallotest";
		
		SaveFormat format = SaveFormat.parseFromId(value.substring(0, 3));
		Assert.assertEquals(SaveFormat.DOUBLE, format);
	}

	@Test
	public void parsingStringWorks(){
		String value = "002hallotest";
		
		SaveFormat format = SaveFormat.parseFromId(value.substring(0, 3));
		Assert.assertEquals(SaveFormat.STRING, format);
	}
	
	@Test
	public void parsingBooleanWorks(){
		String value = "003hallotest";
		
		SaveFormat format = SaveFormat.parseFromId(value.substring(0, 3));
		Assert.assertEquals(SaveFormat.BOOLEAN, format);
	}

}

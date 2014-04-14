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

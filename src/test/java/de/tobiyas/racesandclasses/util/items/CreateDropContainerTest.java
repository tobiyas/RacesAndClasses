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
package de.tobiyas.racesandclasses.util.items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;


public class CreateDropContainerTest {

	
	
	@Test
	public void all_in_one_test() throws IOException{
		File file = File.createTempFile("Drop-", ".yml");
		file.delete();
		
		try{
			CreateDropContainer.createAllContainers(file.getAbsolutePath());
			
			for(EntityType entity : EntityType.values()){			
				DropContainer container = CreateDropContainer.loadDropContainer(file.getAbsolutePath(), entity);
				if(container != null){
					Assert.assertEquals(new ArrayList<ItemStack>(), container.getItems());
				}
			}
		}finally{
			file.delete();
		}
		
	}
	
	@Test
	public void test_loading_container_with_no_config_path(){
		Assert.assertNull(CreateDropContainer.loadDropContainer("testlolwtd", EntityType.BLAZE));
	}
}

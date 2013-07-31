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

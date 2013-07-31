package de.tobiyas.racesandclasses.util.traitutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class DefaultTraitCopy {

	public static void copyDefaultTraits() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();

		
		try{
			String destDirPath = plugin.getDataFolder().getAbsolutePath() + File.separator + "traits" 
					+ File.separator;

			
			String jarFilePath = new File(DefaultTraitCopy.class.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI())
					.getAbsolutePath();
			
			
			copy(jarFilePath, destDirPath);
		}catch(Exception exp){
			plugin.getDebugLogger().logError("Could not copy Default traits!");
			plugin.getDebugLogger().logStackTrace(exp);
		}
	}
	
	
	private static void copy(String jarFilePath, String destDir) throws IOException{
		JarFile jar = new JarFile(jarFilePath);
		try{
			Enumeration<JarEntry> enumeration = jar.entries();
			while (enumeration.hasMoreElements()) {
			    JarEntry file = (JarEntry) enumeration.nextElement();
			    String fileName = file.getName();
			    
			    if(!fileName.contains("defaultTraits")){
			    	continue;
			    }
			    
			    File destinationFile = new File(destDir + java.io.File.separator + file.getName());
			    if (file.isDirectory()) { // if its a directory, create it
			    	destinationFile.mkdirs();
			    	continue;
			    }
	
			    if(!fileName.endsWith(".jar")){
			    	continue;
			    }
			    
			    InputStream inputStream = jar.getInputStream(file); // get the input stream
			    FileOutputStream outputStream = new FileOutputStream(destinationFile);
			   
			    
			    copyLarge(inputStream, outputStream);
			    
			    try{
			    	outputStream.close();
			    	inputStream.close();
			    }catch(IOException exp){}
			}
		}finally{
			//always close the opened jar
			jar.close();
		}
	}
	
	
	/**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    
    /**
     * This method is copied from Apache commons IO!
     * IOUtils # copyLarge(...)
     * 
     * All Rights to Apache via Apache Licence 2.0
     * http://www.apache.org/licenses/LICENSE-2.0
     * 
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	
}
	

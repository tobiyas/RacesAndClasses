package de.tobiyas.racesandclasses.standalonegui.data;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.AnnotationFormatError;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.TraitGuiConfigParser;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.util.collections.CaseInsenesitveMap;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.file.FileUtils;

public class GuiLoader {

	//TODO remove this later.
	//It's just to not load it all the time!
	static{
		File ownMCRoot = new File("D:\\Dropbox\\MinecraftLIGHT");
		File ownBukkit = new File(ownMCRoot, "spigot.jar");
		File ownRaC = new File(new File(ownMCRoot, "plugins"), "RacesAndClasses");
		
		if(ownBukkit.exists()) lastSelectedBukkitFile = ownBukkit;
		if(ownRaC.exists()) lastSelectedBaseFile = ownRaC;
 	}
	
	/**
	 * The Base file to use.
	 */
	private static File baseFile;
	
	/**
	 * The Base file to use.
	 */
	private static File lastSelectedBaseFile;
	
	/**
	 * The Bukkit file to reopen.
	 */
	private static File lastSelectedBukkitFile;
	
	/**
	 * The Path to CB / bukkit / Spigot.
	 */
	private static ClassLoader bukkitLoader;
	
	
	/**
	 * The Set of classes to use.
	 */
	private static final List<GuiClass> classes = new LinkedList<GuiClass>();
	
	
	/**
	 * The Set of races to use.
	 */
	private static final List<GuiRace> races = new LinkedList<GuiRace>();
	
	/**
	 * The Map for the Traits -> Classes.
	 */
	private static final Map<String, java.lang.Class<? extends Trait>> traitMap = new CaseInsenesitveMap<java.lang.Class<? extends Trait>>();
	
	
	
	/**
	 * Loads the Classes from the Current Directory.
	 * 
	 * @return the set of loaded Classes.
	 */
	public static List<GuiClass> getLoadedClasses(){
		Collections.sort(classes);
		return classes;
	}
	
	
	/**
	 * Loads the Races from the Current Directory.
	 * 
	 * @return the set of loaded Races.
	 */
	public static List<GuiRace> getLoadedRaces(){
		Collections.sort(races);
		return races;
	}
	
	
	public static void removeRace(GuiRace race){
		races.remove(race);
	}
	

	public static void addRace(GuiRace race){
		races.add(race);
	}
	

	public static void removeClass(GuiClass clazz){
		classes.remove(clazz);
	}
	
	public static void addClass(GuiClass clazz){
		classes.add(clazz);
	}
	
	
	
	/**
	 * Opens an Dir-Selection.
	 */
	public static void openBaseFileSelection(){
		//select Bukkit / CB / Spigot file.
		if(bukkitLoader == null){
			JOptionPane.showMessageDialog(null, "Select a CraftBukkit / Spigot distribution.");
			
			JFileChooser fileChooser = new JFileChooser();
			if(lastSelectedBukkitFile != null) fileChooser.setSelectedFile(lastSelectedBukkitFile);
			//only allow .jar files.
			fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
				
				@Override
				public String getDescription() {
					return "jar";
				}
				
				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) return true;
					
					return f.getName().endsWith(".jar");
				}
			});
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("Select a CraftBukkit / Spigot distribution");
			
			int result = fileChooser.showOpenDialog(null);
			
			//check if abort or exit.
			if(result == JFileChooser.CANCEL_OPTION) return;
			if(result == JFileChooser.ERROR_OPTION) return;

			if(result == JFileChooser.APPROVE_OPTION) {
				File selected = fileChooser.getSelectedFile();
				lastSelectedBukkitFile = selected;
				if(selected == null || !selected.getName().endsWith(".jar")){
					JOptionPane.showMessageDialog(null, "You need to select a CraftBukkit / Spigot distribution.");
					return;
				}
				
				try{
					ClassLoader loader = URLClassLoader.newInstance(
					    new URL[] { selected.toURI().toURL() },
					    GuiLoader.class.getClassLoader()
					);
					Class<?> clazz = Class.forName("org.bukkit.Bukkit", true, loader);
					if(clazz == null) {
						JOptionPane.showMessageDialog(null, "Could not load the File you selected.");
						return;
					}
					
					bukkitLoader = loader;
				}catch(Throwable exp){
					exp.printStackTrace();
					JOptionPane.showMessageDialog(null, "You need to select a CraftBukkit / Spigot distribution.");
					return;
				}
			}
		}
		
		
		//select RaC Folder.
		JOptionPane.showMessageDialog(null, "Select your RacesAndClasses folder.");
		JFileChooser fileChooser = new JFileChooser();
		if(lastSelectedBaseFile != null) fileChooser.setSelectedFile(lastSelectedBaseFile);
		fileChooser.setDialogTitle("Select your RacesAndClasses Folder");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = fileChooser.showOpenDialog(null);
		
		//check if abort or exit.
		if(result == JFileChooser.CANCEL_OPTION) return;
		if(result == JFileChooser.ERROR_OPTION) return;

		if(result == JFileChooser.APPROVE_OPTION) {
			File selected = fileChooser.getSelectedFile();
			lastSelectedBaseFile = selected;
			if(selected == null || !selected.getName().equalsIgnoreCase("RacesAndClasses")){
				JOptionPane.showMessageDialog(null, "The folder has to be 'RacesAndClasses' base directory.");
				return;
			}
			
			baseFile = selected;
			reloadEverything();
		};
		
		
		
	}



	/**
	 * Reloads everything from the Base-Dir.
	 */
	private static void reloadEverything() {
		if(baseFile == null) return;
		
		reloadTraits();
		
		File racesDir = new File(baseFile, "races");
		File classesDir = new File(baseFile, "classes");
		
		Set<YAMLConfigExtended> racesConfigs = new HashSet<YAMLConfigExtended>();
		for(File raceFile : racesDir.listFiles()){
			if(!raceFile.getName().endsWith(".yml")) continue;
			racesConfigs.add(new YAMLConfigExtended(raceFile).load());
		}
		
		Set<YAMLConfigExtended> classesConfigs = new HashSet<YAMLConfigExtended>();
		for(File classFile : classesDir.listFiles()){
			if(!classFile.getName().endsWith(".yml")) continue;
			classesConfigs.add(new YAMLConfigExtended(classFile).load());
		}
		
		//do the configs.
		for(YAMLConfigExtended raceConfig : racesConfigs){
			for(String root : raceConfig.getRootChildren()){
				//read ALL the Race.
				String displayName = raceConfig.getString(root + ".config.name", root);
				String manaBonus = raceConfig.getString(root + ".config.manabonus", "+0");
				String tag = raceConfig.getString(root + ".config.tag", "");
				String armor = raceConfig.getString(root + ".config.armor", "");
				
				Set<GuiTrait> loadTraits = loadTraits(raceConfig, root);
				GuiRace race = new GuiRace(displayName, root, tag, manaBonus, armor, loadTraits);
				
				races.add(race);
			}
		}

		for(YAMLConfigExtended classConfig : classesConfigs){
			for(String root : classConfig.getRootChildren()){
				String displayName = classConfig.getString(root + ".config.name", root);
				String manaBonus = classConfig.getString(root + ".config.manabonus", "+0");
				String tag = classConfig.getString(root + ".config.tag", "");
				String armor = classConfig.getString(root + ".config.armor", "");
				
				Set<GuiTrait> loadTraits = loadTraits(classConfig, root);
				GuiClass clazz = new GuiClass(displayName, root, tag, manaBonus, armor, loadTraits);
				
				classes.add(clazz);
			}
		}
	}


	/**
	 * loads the List of Traits from the Config.
	 * 
	 * @param raceConfig to load from.
	 * @param name to load
	 * 
	 * @return the set of Traits to load.
	 */
	private static Set<GuiTrait> loadTraits(YAMLConfigExtended config, String name) {
		Set<String> names = config.getChildren(name + ".traits");
		Set<GuiTrait> traits = new HashSet<GuiTrait>();
		
		for(String traitName : names){
			GuiTrait trait = loadTrait(config, name + ".traits", traitName);
			if(trait != null) traits.add(trait);
		}
		
		return traits;
	}
	
	
	
	/**
	 * Loads the Config from the Path.
	 * 
	 * @param config to load from
	 * @param path to load from
	 * @param traitPath the Path name to use.
	 * 
	 * @return the loaded Trait.
	 */
	private static GuiTrait loadTrait(YAMLConfigExtended config, String path, String traitPath){
		if(config == null || path == null || path.isEmpty()) return null;
		if(!config.contains(path)) return null;
		
		String traitName = traitPath;
		if(traitPath.contains(Pattern.quote("#"))) traitName = traitName.split(Pattern.quote("#"))[0];
		traitName = config.getString(path + "." + traitPath + ".trait", traitName);
		
		java.lang.Class<? extends Trait> traitClass = traitMap.get(traitName);
		if(traitClass == null) return null;
		
		GuiTrait trait = TraitGuiConfigParser.generateEmptyConfig(traitClass);
		for(TraitConfigOption option : trait.getTraitConfigurationNeeded()){
			if(config.contains(path + "." + traitPath + "." + option.getName())){
				String value = config.get(path + "." + traitPath + "." + option.getName()).toString();
				option.setCreated(true);
				option.valueSelected(value);
			}
		}
		
		return trait;
	}
	
	
	/**
	 * Reloads all Traits.
	 */
	private static void reloadTraits(){
		traitMap.clear();
		
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".jar");
			}
		};
		
		Set<File> files = FileUtils.getAllFiles(new File(baseFile, "traits"), filter);
		for(File file : files){
			loadExternalTrait(file);
		}
	}
	
	/**
	 * Loads the traits from the System.
	 */
	@SuppressWarnings("unchecked")
	private static void loadExternalTrait(File file){
		try{	
			//old but working: //TODO replace by new Classloader with no leaking of memory
			URLClassLoader clazzLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, bukkitLoader);
			
	        JarFile jarFile = new JarFile(file);
	        Enumeration<JarEntry> entries = jarFile.entries();
	        
	        Set<java.lang.Class<Trait>> clazzArray = new HashSet<java.lang.Class<Trait>>();
	        
	        while (entries.hasMoreElements()) {
	            JarEntry element = entries.nextElement();
	            if (element.getName().endsWith(".class")) {
	                try {
	                	java.lang.Class<?> clazz = clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
	                    if(clazz != null){
	                    	if(Trait.class.isAssignableFrom(clazz)){
	                    		clazzArray.add((java.lang.Class<Trait>) clazz);
	                    	}
	                    }
	                } catch (Throwable e) {
	                	System.out.println("Could not load Java Class: " 
	                			+ element.getName() + ". In: " + jarFile.getName());
	                	
	                	continue;
	                }
	            }
	        }

	        boolean hasClass = false;
	        for(java.lang.Class<? extends Trait> clazz : clazzArray){
	        	try{
					if (clazz != null) {
						boolean isPresent = clazz.getMethod("importTrait").isAnnotationPresent(TraitInfos.class);
				        if(isPresent){
				        	TraitInfos annotation = clazz.getMethod("importTrait").getAnnotation(TraitInfos.class);
				        	hasClass = true;
				        	
				        	String name = annotation.traitName();
				        	traitMap.put(name, clazz);
				        }else{
				        	throw new AnnotationFormatError("Annotation: Import could not be found for class: " + clazz);
				        }
					}
	        	}catch(AnnotationFormatError e){
	        		System.out.println(e.getLocalizedMessage());
	        	}
	        }
	        
	        jarFile.close();
	        
	        if(!hasClass){
	        	throw new AnnotationFormatError("Annotation: Import could not be found for file: " + file.getName());
	        }
	        
		}catch (NoClassDefFoundError e) {
			String message = "Unable to load " + file.getName() + ". Probably it was written for a previous Races version!";
			System.out.println(message);
			return;
		} catch (AnnotationFormatError e){
			System.out.println(e.getLocalizedMessage());
		} catch (Throwable e) {
			String message = "The trait " + file.getName() + " failed to load for an unknown reason.";
			System.out.println(message);
			e.printStackTrace();
		} 
	}
	
}

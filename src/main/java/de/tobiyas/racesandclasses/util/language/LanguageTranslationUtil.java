package de.tobiyas.racesandclasses.util.language;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;

public class LanguageTranslationUtil {
	
	/**
	 * Language to use
	 */
	private String language;
	
	/**
	 * default language to use if wanted language is not found
	 */
	private final String stdLanguage = "en";
	private final File stdLanguageDir = new File(RacesAndClasses.getPlugin().getDataFolder() + File.separator 
			+ "language" + File.separator + stdLanguage);
	private List<YAMLConfigExtended> stdLanguageFiles = getYAMLOfFile(stdLanguageDir);
	
	
	/**
	 * Files of the language
	 */
	private List<YAMLConfigExtended> languageConfiguration;
	
	/**
	 * plugin to call static stuff on
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * The singleton instance for this struct
	 */
	private static LanguageTranslationUtil instance;
	
	
	/**
	 * Inits the Tranlation. 
	 * The language is the passed language String
	 * 
	 * @param language
	 */
	private LanguageTranslationUtil(String language) {
		this.language = language;
		plugin = RacesAndClasses.getPlugin();
	}
	
	/**
	 * Inits the language system
	 */
	public LanguageTranslationUtil init(){
		String languagePath = plugin.getDataFolder() + File.separator + "language" + File.separator
				+ language;
		
		File languageDir = new File(languagePath);
		if(!languageDir.exists()){
			plugin.log("ERROR: Language with the name: " + language + " was not found. Loading English instead.");
			language = stdLanguage;
			languageDir = stdLanguageDir;
		}
		
		languageConfiguration = new LinkedList<YAMLConfigExtended>();
		languageConfiguration.addAll(getYAMLOfFile(languageDir));
				
		return this;
	}
	
	
	/**
	 * Reads recursively all .yml files from the
	 * file given and loads them.
	 * 
	 * @param file to read
	 * @return list of all loaded {@link YAMLConfigExtended} files.
	 */
	private List<YAMLConfigExtended> getYAMLOfFile(File file){
		List<YAMLConfigExtended> returnList = new LinkedList<YAMLConfigExtended>();
		if(file.isDirectory()){
			for(File subFile : file.listFiles()){
				returnList.addAll(getYAMLOfFile(subFile));
			}
		}
		
		if(file.getName().endsWith(".yml")){
			YAMLConfigExtended config = new YAMLConfigExtended(file).load();
			if(config.getValidLoad()){
				returnList.add(config);
			}else{
				plugin.log("ERROR on loading Language File: " + file.getAbsolutePath());
			}
		}
		
		return returnList;
	}
	
	
	/**
	 * Tries to translate the given tag into the current language.
	 * <br>Also creates the structure if not present
	 * <br>
	 * <br> 
	 * @param tag that shall be translated
	 * @param tryInStdLanguageIfFails if true, tries to translate in the STD language 
	 * if translation in the current language fails.
	 * 
	 * @return the wanted translation, or an {@link TranslationNotFoundException}
	 * 
	 * @throws TranslationNotFoundException if the tag was not found in the translation table.
	 * This includes the STD translation table.
	 */
	public static String tryTranslate(String tag, boolean tryInStdLanguageIfFails) throws TranslationNotFoundException{
		lazyInit(false);
		
		try{
			return tryPreferedTranslate(tag);
		}catch(TranslationNotFoundException exp){
			RacesAndClasses.getPlugin().log("triing to translate: '" + exp.getTagNotFound() + "' in language: " + exp.getLanguage());
			
			if(!tryInStdLanguageIfFails){
				throw exp;
			}
		}
		
		return trySTDTranslate(tag);
	}
	
	
	/**
	 * Tries to translate the tag to the STD language.
	 * 
	 * @param tag to translate
	 * @return the translated String or
	 * 
	 * @throws TranslationNotFoundException if translation is not found
	 */
	private static String trySTDTranslate(String tag) throws TranslationNotFoundException{
		String translation = readFromYAMLList(instance.languageConfiguration, tag);
		
		if("".equals(translation)){
			throw new TranslationNotFoundException(getCurrentLanguage(), tag);
		}else{
			return translation;
		}
	}

	/**
	 * Tries to translate the tag to the language in the {@link #instance}.
	 * 
	 * @param tag to translate
	 * @return the translated String or
	 * 
	 * @throws TranslationException if translation is not found
	 */
	private static String tryPreferedTranslate(String tag) throws TranslationNotFoundException{
		String translation = readFromYAMLList(instance.stdLanguageFiles, tag);
		
		if("".equals(translation)){
			throw new TranslationNotFoundException(instance.stdLanguage, tag);
		}else{
			return translation;
		}
	}
	
	
	/**
	 * tries to find the given Tag in all config files.
	 * <br>If found, it is returned.
	 * <br>If not found, "" (an empty String) is returned.
	 * <br>
	 * <br>
	 * 
	 * @param configList to search in
	 * @param tag to search for
	 * @return the searched translation or "" on not found.
	 */
	private static String readFromYAMLList(List<YAMLConfigExtended> configList, String tag){
		for(YAMLConfigExtended config : configList){
			if(config.contains(tag)){
				return String.valueOf(config.get(tag));
			}
		}
		
		return "";
	}

	/**
	 * returns the current language
	 */
	public static String getCurrentLanguage(){
		lazyInit(false);
		return instance.language;
	}
	
	
	/**
	 * inits The Singleton instace.
	 * 
	 * @param clearBefore if set to true, it clears the previous instance
	 */
	public static void lazyInit(boolean clearBefore){
		if(clearBefore){
			instance = null;
		}
		
		if(instance == null){
			String language = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_usedLanguage();
			instance = new LanguageTranslationUtil(language);
		}
	}
}
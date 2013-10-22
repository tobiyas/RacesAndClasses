package de.tobiyas.racesandclasses.util.language;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.language.en.EN_Text;
import de.tobiyas.util.config.YAMLConfigExtended;

public class LanguageTranslationUtil {
	
	/**
	 * plugin to call static stuff on
	 */
	private final RacesAndClasses plugin = RacesAndClasses.getPlugin();

	/**
	 * Language to use
	 */
	private String language;
	
	/**
	 * default language to use if wanted language is not found
	 */
	private static final String stdLanguage = "en";
	private static final File stdLanguageDir = new File(RacesAndClasses.getPlugin().getDataFolder() + File.separator 
			+ "language" + File.separator + stdLanguage + File.separator);
	
	private List<YAMLConfigExtended> stdLanguageFiles;
	
	
	/**
	 * Files of the language
	 */
	private List<YAMLConfigExtended> languageConfiguration;
	
	
	
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
		
		stdLanguageFiles = new LinkedList<YAMLConfigExtended>();
		stdLanguageFiles.addAll(getYAMLOfFile(stdLanguageDir));
				
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
		if(file == null || !file.exists()) return returnList;
		
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
	 * logged error to console
	 */
	private static boolean loggedError = false;
	
	
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
	public static Translator tryTranslate(String tag, boolean tryInStdLanguageIfFails) throws TranslationNotFoundException{
		lazyInit(false);
		
		try{
			return tryPreferedTranslate(tag);
		}catch(TranslationNotFoundException exp){
			if(!loggedError){
				RacesAndClasses.getPlugin().log("Translate failed! Check the debug.log . There can be more translation errors.");
				loggedError = true;
			}
			
			RacesAndClasses.getPlugin().getDebugLogger().logWarning("tried to translate: '" 
					+ exp.getTagNotFound() + "' in language: '" + exp.getLanguage() + "' but it was not found.");
			
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
	private static Translator trySTDTranslate(String tag) throws TranslationNotFoundException{
		String translation = readFromYAMLList(instance.stdLanguageFiles, tag);
		
		if("".equals(translation)){
			throw new TranslationNotFoundException(stdLanguage, tag);
		}else{
			return new Translator(translation);
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
	private static Translator tryPreferedTranslate(String tag) throws TranslationNotFoundException{
		String translation = readFromYAMLList(instance.languageConfiguration, tag);
		
		if("".equals(translation)){
			throw new TranslationNotFoundException(getCurrentLanguage(), tag);
		}else{
			return new Translator(translation);
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
			instance.init();
		}
	}
	
	
	/**
	 * "Reloads" the language.
	 */
	public static void reload(){
		instance = null;
	}
	
	
	/**
	 * Checks if the EN Language is present.
	 */
	public static void check_EN_isPresent(){ //TODO do some versioning or something...
		if(!stdLanguageDir.exists()){
			stdLanguageDir.mkdirs();
		}
		
		File enDefaultFile = new File(stdLanguageDir + File.separator + "en.yml");
		try {
			FileUtils.write(enDefaultFile, EN_Text.en_language);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
package de.tobiyas.racesandclasses.translation;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.translation.exception.TranslationException;
import de.tobiyas.racesandclasses.translation.exception.TranslationNotFoundException;
import de.tobiyas.racesandclasses.translation.languages.en.EN_Text;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DefaultTranslationManager implements TranslationManager {

	/**
	 * plugin to call static stuff on
	 */
	private final RacesAndClasses plugin = RacesAndClasses.getPlugin();

	/**
	 * Language to use
	 */
	private String language;
	private List<YAMLConfigExtended> languageConfiguration;
	
	/**
	 * default language to use if wanted language is not found
	 */
	private final String stdLanguage = "en";
	
	private final List<YAMLConfigExtended> stdLanguageFiles 
		= Arrays.asList(new YAMLConfigExtended().loadSafeFromString(EN_Text.en_language));
	
	
	/**
	 * Files of the language
	 */
	
	
	
	/**
	 * Reads recursively all .yml files from the
	 * file given and loads them.
	 * 
	 * @param file to read
	 * @return list of all loaded {@link YAMLConfigExtended} files.
	 */
	protected List<YAMLConfigExtended> getYAMLOfFile(File file){
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
	

	/////////////////////////////////////////////////
	//new
	/////////////////////////////////////////////////
	
	/**
	 * Creates a new TranslationManager with the passed language.
	 * 
	 * @param language to pass
	 */
	public DefaultTranslationManager(String language) {
		this.language = language;
	}
	
	/**
	 * logged error to console
	 */
	private static boolean loggedError = false;
	
	
	@Override
	public Translator translate(String key, boolean tryInStdLanguageIfFails) throws TranslationNotFoundException {
		try{
			return tryPreferedTranslate(key);
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
		
		return trySTDTranslate(key);
	}
	
	/**
	 * Tries to translate the tag to the STD language.
	 * 
	 * @param tag to translate
	 * @return the translated String or
	 * 
	 * @throws TranslationNotFoundException if translation is not found
	 */
	private Translator trySTDTranslate(String tag) throws TranslationNotFoundException{
		String translation = readFromYAMLList(stdLanguageFiles, tag);
		
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
	private Translator tryPreferedTranslate(String tag) throws TranslationNotFoundException{
		String translation = readFromYAMLList(languageConfiguration, tag);
		
		if("".equals(translation)){
			throw new TranslationNotFoundException(getCurrentLanguage(), tag);
		}else{
			return new Translator(translation);
		}
	}
	
	
	@Override
	public TranslationManager init(){
		check_EN_isPresent();
		
		String languagePath = plugin.getDataFolder() + File.separator + "language" + File.separator
				+ language;
		
		File languageDir = new File(languagePath);
		if(!languageDir.exists()){
			plugin.log("ERROR: Language with the name: " + language + " was not found. Loading English instead.");
			language = stdLanguage;
		}
		
		languageConfiguration = new LinkedList<YAMLConfigExtended>();
		languageConfiguration.addAll(getYAMLOfFile(languageDir));
		
		return this;
	}
	
	/**
	 * Checks if the EN Language is present.
	 */
	private void check_EN_isPresent(){
		File stdLanguageDir = new File(plugin.getDataFolder() + File.separator + "language" + File.separator + "en");
		if(!stdLanguageDir.exists()){
			stdLanguageDir.mkdirs();
		}
		
		File enDefaultFile = new File(stdLanguageDir + File.separator + "en.yml");
		YAMLConfigExtended enConfig = new YAMLConfigExtended(enDefaultFile).load();
		
		
		YAMLConfigExtended defaultENConfig = new YAMLConfigExtended();
		try{
			defaultENConfig.loadFromString(EN_Text.en_language);
		}catch(InvalidConfigurationException exp){
			plugin.getDebugLogger().logStackTrace(exp);
			return;
		}
		
		boolean hasToSave = false;
		for(String key : defaultENConfig.getRootChildren()){
			if(!enConfig.isString(key)){
				enConfig.set(key, defaultENConfig.get(key));
				hasToSave = true;
			}
		}
		
		if(hasToSave){
			enConfig.save();
		}
	}

	@Override
	public void reload() {
		//nothing needed I suppose...
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String getCurrentLanguage() {
		return language;
	}

}

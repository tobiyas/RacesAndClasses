package de.tobiyas.racesandclasses.translation;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class TranslationManagerHolder {

	/**
	 * The TranslationManager that is hold
	 */
	private static TranslationManager manager;
	
	
	/**
	 * gets the Language Translation manager.
	 * 
	 * @return TranslationManager of the Plugin.
	 */
	public static TranslationManager getTranslationManager(){
		if(manager == null){
			String language = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_usedLanguage();
			manager = new DefaultTranslationManager(language);
			manager.init();
		}
		
		return manager;
	}
	
	/**
	 * Forces to accept the new manager.
	 * 
	 * @param manager to force to
	 */
	public static void forceManager(TranslationManager manager){
		shutdown();
		TranslationManagerHolder.manager = manager;
	}

	/**
	 * Closes the Translation Manager.
	 */
	public static void shutdown() {
		if(manager != null){
			manager.shutdown();
			manager = null;
		}
	}
}

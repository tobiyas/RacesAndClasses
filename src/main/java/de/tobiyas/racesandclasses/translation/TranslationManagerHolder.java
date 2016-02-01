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
		init();
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

	/**
	 * Inits if not inited.
	 */
	public static void init() {
		if(manager == null){
			String language = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_usedLanguage();
			manager = new DefaultTranslationManager(language);
			manager.init();
		}
	}
}

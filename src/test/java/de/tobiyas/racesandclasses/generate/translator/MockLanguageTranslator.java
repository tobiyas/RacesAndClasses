package de.tobiyas.racesandclasses.generate.translator;

import de.tobiyas.racesandclasses.translation.TranslationManager;
import de.tobiyas.racesandclasses.translation.Translator;
import de.tobiyas.racesandclasses.translation.exception.TranslationNotFoundException;

public class MockLanguageTranslator implements TranslationManager {

	@Override
	public Translator translate(String key, boolean tryInStdLanguageIfFails)
			throws TranslationNotFoundException {
		
		return new Translator(key);
	}

	@Override
	public TranslationManager init() {
		return this;
	}

	@Override
	public void reload() {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String getCurrentLanguage() {
		return "MOCK";
	}

	
}

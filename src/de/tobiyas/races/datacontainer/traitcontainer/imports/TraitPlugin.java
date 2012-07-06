package de.tobiyas.races.datacontainer.traitcontainer.imports;

import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public interface TraitPlugin extends Trait {
	
	@interface Import{
		
	}
	public void importTrait();

}

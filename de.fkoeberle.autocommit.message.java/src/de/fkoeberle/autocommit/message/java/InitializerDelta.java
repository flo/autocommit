package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.Initializer;

public final class InitializerDelta extends DeclarationDelta {

	public InitializerDelta(Initializer oldDeclaration,
			Initializer newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		return EnumSet.noneOf(BodyDeclarationChangeType.class);
	}

}

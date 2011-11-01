package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.EnumConstantDeclaration;

public final class EnumConstantDelta extends
		DeclarationDelta<EnumConstantDeclaration> {

	public EnumConstantDelta(EnumConstantDeclaration oldDeclaration,
			EnumConstantDeclaration newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		return EnumSet.noneOf(BodyDeclarationChangeType.class);
	}

}

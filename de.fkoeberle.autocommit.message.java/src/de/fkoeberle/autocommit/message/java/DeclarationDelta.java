package de.fkoeberle.autocommit.message.java;

import org.eclipse.jdt.core.dom.BodyDeclaration;

public final class DeclarationDelta {
	private final BodyDeclaration oldDeclaration;
	private final BodyDeclaration newDeclaration;

	public DeclarationDelta(BodyDeclaration oldDeclaration,
			BodyDeclaration newDeclaration) {
		this.oldDeclaration = oldDeclaration;
		this.newDeclaration = newDeclaration;
	}

	public BodyDeclaration getOldDeclaration() {
		return oldDeclaration;
	}

	public BodyDeclaration getNewDeclaration() {
		return newDeclaration;
	}

}

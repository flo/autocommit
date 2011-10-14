package de.fkoeberle.autocommit.message.java;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;

public class TypeDelta {
	private final AbstractTypeDeclaration oldType;
	private final AbstractTypeDeclaration newType;

	public TypeDelta(AbstractTypeDeclaration oldType,
			AbstractTypeDeclaration newType) {
		super();
		this.oldType = oldType;
		this.newType = newType;
	}

	public AbstractTypeDeclaration getOldType() {
		return oldType;
	}

	public AbstractTypeDeclaration getNewType() {
		return newType;
	}

}

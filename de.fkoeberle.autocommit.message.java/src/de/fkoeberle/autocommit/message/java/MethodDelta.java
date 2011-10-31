package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class MethodDelta extends DeclarationDelta {
	private final MethodDeclaration oldMethodDeclaration;
	private final MethodDeclaration newMethodDeclaration;

	/**
	 * Both method declarations must have the same name and parameter list.
	 * 
	 * @param oldMethodDeclaration
	 * @param newMethodDeclaration
	 */
	MethodDelta(MethodDeclaration oldMethodDeclaration,
			MethodDeclaration newMethodDeclaration) {
		super(oldMethodDeclaration, newMethodDeclaration);
		this.oldMethodDeclaration = oldMethodDeclaration;
		this.newMethodDeclaration = newMethodDeclaration;
	}

	public MethodDeclaration getOldMethodDeclaration() {
		return oldMethodDeclaration;
	}

	public MethodDeclaration getNewMethodDeclaration() {
		return newMethodDeclaration;
	}

	private final AbstractTypeDeclaration getNewParentType() {
		ASTNode parent = getNewMethodDeclaration().getParent();
		return (AbstractTypeDeclaration) parent;
	}

	public String getMethodName() {
		return TypeUtil.nameOfMethod(getNewMethodDeclaration());
	}

	public String getFullTypeName() {
		return TypeUtil.fullTypeNameOf(getNewParentType());
	}

	public String getParameterTypes() {
		return TypeUtil.parameterTypesOf(getNewMethodDeclaration());
	}

	public String getSimpleTypeName() {
		return TypeUtil.nameOf(getNewParentType());
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = EnumSet
				.noneOf(BodyDeclarationChangeType.class);

		/*
		 * Name and parameters can be assumed to be the same as the constructor
		 * requires it
		 */

		if (containsReturnTypeChanges()) {
			result.add(BodyDeclarationChangeType.RETURN_TYPE);
		}
		if (containsBodyChanges()) {
			result.add(BodyDeclarationChangeType.METHOD_BODY);
		}
		if (oldMethodDeclaration.getExtraDimensions() != newMethodDeclaration
				.getExtraDimensions()) {
			result.add(BodyDeclarationChangeType.METHOD_EXTRA_DIMENSIONS);
		}
		return result;
	}

	private boolean containsReturnTypeChanges() {
		Type oldType = oldMethodDeclaration.getReturnType2();
		Type newType = newMethodDeclaration.getReturnType2();
		if (oldType == null || newType == null) {
			return oldType != newType;
		}
		boolean sameReturnType = (oldType.subtreeMatch(new ASTMatcher(true),
				newType));
		return !sameReturnType;
	}

	private boolean containsBodyChanges() {
		Block oldBody = oldMethodDeclaration.getBody();
		Block newBody = newMethodDeclaration.getBody();
		if (oldBody == null || newBody == null) {
			return oldBody != newBody;
		}
		boolean sameReturnType = (oldBody.subtreeMatch(new ASTMatcher(true),
				newBody));
		return !sameReturnType;
	}
}

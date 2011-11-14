package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;

public final class MethodDelta extends DeclarationDelta<MethodDeclaration> {

	/**
	 * Both method declarations must have the same name and parameter list.
	 * 
	 * @param oldMethodDeclaration
	 * @param newMethodDeclaration
	 */
	MethodDelta(MethodDeclaration oldMethodDeclaration,
			MethodDeclaration newMethodDeclaration) {
		super(oldMethodDeclaration, newMethodDeclaration);
	}

	private final AbstractTypeDeclaration getNewParentType() {
		ASTNode parent = newDeclaration.getParent();
		return (AbstractTypeDeclaration) parent;
	}

	public String getMethodName() {
		return TypeUtil.nameOfMethod(newDeclaration);
	}

	public String getFullTypeName() {
		return TypeUtil.fullTypeNameOf(getNewParentType());
	}

	public String getParameterTypes() {
		return TypeUtil.parameterTypesOf(newDeclaration);
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
		if (oldDeclaration.getExtraDimensions() != newDeclaration
				.getExtraDimensions()) {
			result.add(BodyDeclarationChangeType.METHOD_EXTRA_DIMENSIONS);
		}
		return result;
	}

	private boolean containsReturnTypeChanges() {
		Type oldType = oldDeclaration.getReturnType2();
		Type newType = newDeclaration.getReturnType2();
		return ASTCompareUtil.astNodesDiffer(oldType, newType);
	}

	private boolean containsBodyChanges() {
		Block oldBody = oldDeclaration.getBody();
		Block newBody = newDeclaration.getBody();
		return ASTCompareUtil.astNodesDiffer(oldBody, newBody);
	}
}

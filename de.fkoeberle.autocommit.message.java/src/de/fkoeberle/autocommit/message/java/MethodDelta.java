package de.fkoeberle.autocommit.message.java;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDelta {
	private final MethodDeclaration oldMethodDeclaration;
	private final MethodDeclaration newMethodDeclaration;

	public MethodDelta(MethodDeclaration oldMethodDeclaration,
			MethodDeclaration newMethodDeclaration) {
		super();
		this.oldMethodDeclaration = oldMethodDeclaration;
		this.newMethodDeclaration = newMethodDeclaration;
	}

	public MethodDeclaration getOldMethodDeclaration() {
		return oldMethodDeclaration;
	}

	public MethodDeclaration getNewMethodDeclaration() {
		return newMethodDeclaration;
	}

	/**
	 * 
	 * @return a {@link MethodDelta} or null if the specified delta is not about
	 *         methods.
	 */
	public static MethodDelta valueOf(DeclarationDelta declarationDelta) {
		BodyDeclaration oldDeclaration = declarationDelta.getOldDeclaration();
		BodyDeclaration newDeclaration = declarationDelta.getNewDeclaration();
		if (!(oldDeclaration instanceof MethodDeclaration)) {
			return null;
		}
		assert newDeclaration instanceof MethodDeclaration : "since it has same type as oldDeclaration based on how DeclarationDelta gets created"; //$NON-NLS-1$
		MethodDeclaration oldMethodDeclaration = (MethodDeclaration) oldDeclaration;
		MethodDeclaration newMethodDeclaration = (MethodDeclaration) newDeclaration;
		return new MethodDelta(oldMethodDeclaration, newMethodDeclaration);
	}

	private final AbstractTypeDeclaration getNewParentType() {
		ASTNode parent = getNewMethodDeclaration().getParent();
		return (AbstractTypeDeclaration) parent;
	}

	public String getMethodName() {
		return TypeUtil.nameOfMethod(getNewMethodDeclaration());
	}

	public String getFullTypeName() {
		return TypeUtil.nameOf(getNewParentType());
	}

	public String getParameterTypes() {
		return TypeUtil.parameterTypesOf(getNewMethodDeclaration());
	}

	public String getSimpleTypeName() {
		return TypeUtil.nameOf(getNewParentType());
	}

}

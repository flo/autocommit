package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Type;

public final class AnnotationTypeMemberDelta extends
		DeclarationDelta<AnnotationTypeMemberDeclaration> {

	/**
	 * 
	 * @param oldDeclaration
	 *            must have the same name as newDeclaration.
	 * @param newDeclaration
	 *            must have the same name as oldDeclaration.
	 */
	public AnnotationTypeMemberDelta(
			AnnotationTypeMemberDeclaration oldDeclaration,
			AnnotationTypeMemberDeclaration newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = EnumSet.noneOf(BodyDeclarationChangeType.class);
		if (containsDefaultChanges()) {
			result.add(BodyDeclarationChangeType.ANNOTATION_MEMBER_DEFAULT);
		}
		if (containsTypeChanges()) {
			result.add(BodyDeclarationChangeType.ANNOTATION_MEMBER_TYPE);
		}
		return result;
	}

	private boolean containsTypeChanges() {
		Type oldType = oldDeclaration.getType();
		Type newType = newDeclaration.getType();
		return astNodesDiffer(oldType, newType);
	}

	private boolean containsDefaultChanges() {
		Expression oldExpression = oldDeclaration.getDefault();
		Expression newExpression = newDeclaration.getDefault();
		return astNodesDiffer(oldExpression, newExpression);
	}

}

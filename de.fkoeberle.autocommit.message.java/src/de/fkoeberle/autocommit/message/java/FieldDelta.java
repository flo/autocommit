package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class FieldDelta extends DeclarationDelta {
	private final FieldDeclaration oldDeclaration;
	private final FieldDeclaration newDeclaration;

	/**
	 * 
	 * @param oldDeclaration
	 *            the old declaration which must have the same fragments as the
	 *            new declaration.
	 * @param newDeclaration
	 *            the new declaration which must have the same fragments as the
	 *            old declaration.
	 */
	public FieldDelta(FieldDeclaration oldDeclaration,
			FieldDeclaration newDeclaration) {
		super(oldDeclaration, newDeclaration);
		this.oldDeclaration = oldDeclaration;
		this.newDeclaration = newDeclaration;
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = EnumSet
				.noneOf(BodyDeclarationChangeType.class);
		if (containsTypeChange()) {
			result.add(BodyDeclarationChangeType.FIELD_TYPE);
		}
		return result;
	}

	private boolean containsTypeChange() {
		Type oldType = oldDeclaration.getType();
		Type newType = newDeclaration.getType();
		boolean sameType = oldType.subtreeMatch(new ASTMatcher(true), newType);
		return !sameType;
	}

}

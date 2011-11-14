package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;

public abstract class DeclarationDelta<T extends BodyDeclaration> implements
		IDelta {
	protected final T oldDeclaration;
	protected final T newDeclaration;
	private EnumSet<BodyDeclarationChangeType> changeTypes;

	public DeclarationDelta(T oldDeclaration, T newDeclaration) {
		this.oldDeclaration = oldDeclaration;
		this.newDeclaration = newDeclaration;
	}

	public T getOldDeclaration() {
		return oldDeclaration;
	}

	public T getNewDeclaration() {
		return newDeclaration;
	}

	private final boolean containsModifierChanges() {
		if (oldDeclaration.getModifiers() != newDeclaration.getModifiers()) {
			return true;
		}
		List<?> oldModifieres = oldDeclaration.modifiers();
		List<?> newModifieres = newDeclaration.modifiers();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldModifieres,
				newModifieres);
	}

	/**
	 * 
	 * @return true if the javadoc element of this declaration has been changed
	 *         and false otherwise. Ignores javadoc on child elements.
	 */
	private final boolean containsJavaDocChanges() {
		Javadoc oldJavaDoc = oldDeclaration.getJavadoc();
		Javadoc newJavaDoc = newDeclaration.getJavadoc();
		if (oldJavaDoc == null || newJavaDoc == null) {
			return (oldJavaDoc != newJavaDoc);
		} else {
			return (!oldJavaDoc.subtreeMatch(new ASTMatcher(true), newJavaDoc));
		}
	}

	private EnumSet<BodyDeclarationChangeType> determineChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = determineOtherChangeTypes();
		if (containsJavaDocChanges()) {
			result.add(BodyDeclarationChangeType.JAVADOC);
		}
		if (containsModifierChanges()) {
			result.add(BodyDeclarationChangeType.MODIFIERS);
		}
		return result;
	}

	/**
	 * 
	 * @return a list of all changes between old and new declaration excluding
	 *         changes related to the javadoc tag and to the modifier list.
	 */
	protected abstract EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes();

	@Override
	public EnumSet<BodyDeclarationChangeType> getChangeTypes() {
		if (changeTypes == null) {
			changeTypes = determineChangeTypes();
		}
		return changeTypes;
	}

}

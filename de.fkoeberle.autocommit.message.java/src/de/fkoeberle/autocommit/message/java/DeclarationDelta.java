package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;

public abstract class DeclarationDelta {
	private final BodyDeclaration oldDeclaration;
	private final BodyDeclaration newDeclaration;
	private EnumSet<BodyDeclarationChangeType> changeTypes;

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

	protected static boolean listsOfASTNodesDiffer(List<?> oldList,
			List<?> newList) {
		if (oldList.size() != newList.size()) {
			return true;
		}
		int size = oldList.size();
		for (int i = 0; i < size; i++) {
			ASTNode oldInterface = (ASTNode) (oldList.get(i));
			ASTNode newInterface = (ASTNode) (newList.get(i));
			boolean matches = oldInterface.subtreeMatch(new ASTMatcher(true),
					newInterface);
			if (!matches) {
				return true;
			}
		}
		return false;
	}

	private final boolean containsModifierChanges() {
		if (oldDeclaration.getModifiers() != newDeclaration.getModifiers()) {
			return true;
		}
		List<?> oldModifieres = oldDeclaration.modifiers();
		List<?> newModifieres = newDeclaration.modifiers();
		return listsOfASTNodesDiffer(oldModifieres, newModifieres);
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

	/**
	 * 
	 * @return which kind of changes this delta represents. The result must not
	 *         be modified.
	 */
	public EnumSet<BodyDeclarationChangeType> getChangeTypes() {
		if (changeTypes == null) {
			changeTypes = determineChangeTypes();
		}
		return changeTypes;
	}

	public static DeclarationDelta valueOf(BodyDeclaration oldDeclaration,
			BodyDeclaration newDeclaration) {
		return new DeclarationDelta(oldDeclaration, newDeclaration) {

			@Override
			protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
				throw new UnsupportedOperationException();
			}
		};
	}
}

package de.fkoeberle.autocommit.message.java;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;

public class DeclarationDelta {
	private final BodyDeclaration oldDeclaration;
	private final BodyDeclaration newDeclaration;
	private Boolean modifiersChanged;
	private Boolean javaDocChanged;

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

	public final boolean containsModifierChanges() {
		if (modifiersChanged == null) {
			if (oldDeclaration.getModifiers() != newDeclaration.getModifiers()) {
				return true;
			}
			List<?> oldModifieres = oldDeclaration.modifiers();
			List<?> newModifieres = newDeclaration.modifiers();
			boolean listsDiffer = listsOfASTNodesDiffer(oldModifieres,
					newModifieres);
			modifiersChanged = Boolean.valueOf(listsDiffer);
		}
		return modifiersChanged.booleanValue();
	}

	/**
	 * 
	 * @return true if the javadoc element of this declaration has been changed
	 *         and false otherwise. Ignores javadoc on child elements.
	 */
	public final boolean containsJavaDocChanges() {
		if (javaDocChanged == null) {
			Javadoc oldJavaDoc = oldDeclaration.getJavadoc();
			Javadoc newJavaDoc = newDeclaration.getJavadoc();
			if (oldJavaDoc == null || newJavaDoc == null) {
				javaDocChanged = Boolean.valueOf(oldJavaDoc != newJavaDoc);
			} else {
				javaDocChanged = Boolean.valueOf(!oldJavaDoc.subtreeMatch(
						new ASTMatcher(true), newJavaDoc));
			}
		}

		return javaDocChanged.booleanValue();
	}

}

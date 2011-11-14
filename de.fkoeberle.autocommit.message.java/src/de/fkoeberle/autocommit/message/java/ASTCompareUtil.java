package de.fkoeberle.autocommit.message.java;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;

public final class ASTCompareUtil {
	private ASTCompareUtil() {
		// Utility class
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

	/**
	 * 
	 * @param oldNode
	 *            can be null.
	 * @param newNode
	 *            can be null.
	 * @return true if and only if the nodes match when compared with a
	 *         {@link ASTMatcher}.
	 */
	protected static boolean astNodesDiffer(ASTNode oldNode, ASTNode newNode) {
		if (oldNode == null || newNode == null) {
			return oldNode != newNode;
		}
		boolean sameReturnType = (oldNode.subtreeMatch(new ASTMatcher(true),
				newNode));
		return !sameReturnType;
	}
}

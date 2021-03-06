/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Utility class for comparing {@link ASTNode} objects.
 * 
 */
public final class ASTCompareUtil {
	private ASTCompareUtil() {
		// Utility class
	}

	/**
	 * 
	 * @param oldList
	 *            must be a list of {@link ASTNode} objects.
	 * @param newList
	 *            must be a list of {@link ASTNode} objects.
	 * @return true if the two list of abstract syntax trees don't match.
	 */
	public static boolean listsOfASTNodesDiffer(List<?> oldList, List<?> newList) {
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
	 * @return false if and only if the nodes match when compared with a
	 *         {@link ASTMatcher}. Otherwise true gets returned
	 */
	public static boolean astNodesDiffer(ASTNode oldNode, ASTNode newNode) {
		if (oldNode == null || newNode == null) {
			return oldNode != newNode;
		}
		boolean sameReturnType = (oldNode.subtreeMatch(new ASTMatcher(true),
				newNode));
		return !sameReturnType;
	}
}

/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

/**
 * Describes the type of a headline of a {@link OutlineNode} object.
 * 
 */
public enum OutlineNodeType {
	DOCUMENT, CHAPTER, SECTION, SUBSECTION, SUBSUBSECTION;

	/**
	 * 
	 * @param commandName
	 *            the name of the command without \ and {}
	 * @return the {@link OutlineNodeType} which represents the given
	 *         commandName.
	 * @throws IllegalArgumentException
	 *             if commandName is not a
	 */
	public static OutlineNodeType typeOfCommand(String commandName) {
		if (commandName.equals("chapter")) {
			return CHAPTER;
		} else if (commandName.equals("section")) {
			return SECTION;
		} else if (commandName.equals("subsection")) {
			return SUBSECTION;
		} else if (commandName.equals("subsubsection")) {
			return SUBSUBSECTION;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean causesTheEndOf(OutlineNodeType other) {
		return (this.compareTo(other) <= 0);
	}
}

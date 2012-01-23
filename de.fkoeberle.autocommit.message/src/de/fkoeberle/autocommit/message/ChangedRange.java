/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

public final class ChangedRange {
	private final int firstIndex;
	private final int exclusiveEndOfNew;
	private final int exclusiveEndOfOld;

	public ChangedRange(int firstIndex, int exclusiveEndOfOld,
			int exclusiveEndOfNew) {
		this.firstIndex = firstIndex;
		this.exclusiveEndOfNew = exclusiveEndOfNew;
		this.exclusiveEndOfOld = exclusiveEndOfOld;
	}

	public int getFirstIndex() {
		return firstIndex;
	}

	public int getExlusiveEndOfNew() {
		return exclusiveEndOfNew;
	}

	public int getExlusiveEndOfOld() {
		return exclusiveEndOfOld;
	}

}
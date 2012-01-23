/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

public class ChangedTextFile {
	private final String path;
	private final String oldContent;
	private final String newContent;
	private ChangedRange latestChangedRange;
	private ChangedRange earliestChangedRange;

	public ChangedTextFile(String path, String oldContent, String newContent) {
		this.path = path;
		this.oldContent = oldContent;
		this.newContent = newContent;
	}

	public String getPath() {
		return path;
	}

	public String getOldContent() {
		return oldContent;
	}

	public String getNewContent() {
		return newContent;
	}

	/**
	 * 
	 * @return {@link ChangedRange} which describes a possible replacement which
	 *         could have transforemed the old content to the new content. It
	 *         tries to find the {@link ChangeRange} which is the smallest and
	 *         most right.
	 */
	public ChangedRange getLatestChangedRange() {
		if (latestChangedRange == null) {
			latestChangedRange = determineChangedRange();
		}
		return latestChangedRange;
	}

	/**
	 * 
	 * @return {@link ChangedRange} which describes a possible replacement which
	 *         could have transforemed the old content to the new content. It
	 *         tries to find the {@link ChangeRange} which is the smallest and
	 *         most left.
	 */
	public ChangedRange getEarliestChangedRange() {
		if (earliestChangedRange == null) {
			earliestChangedRange = determineEarliestChangedRange();
		}
		return earliestChangedRange;
	}

	private boolean contentDiffersOrBothEndedAt(int index) {
		if ((index >= oldContent.length()) || (index >= newContent.length())) {
			return true;
		}
		char oldChar = oldContent.charAt(index);
		char newChar = newContent.charAt(index);
		return oldChar != newChar;
	}

	private ChangedRange determineChangedRange() {
		int start = 0;

		while (!contentDiffersOrBothEndedAt(start)) {
			start++;
		}

		int exclusiveEndOfOld = oldContent.length();
		int exclusiveEndOfNew = newContent.length();
		while ((exclusiveEndOfNew > start)
				&& (exclusiveEndOfOld > start)
				&& (oldContent.charAt(exclusiveEndOfOld - 1) == newContent
						.charAt(exclusiveEndOfNew - 1))) {
			exclusiveEndOfOld--;
			exclusiveEndOfNew--;
		}
		return new ChangedRange(start, exclusiveEndOfOld, exclusiveEndOfNew);
	}

	private ChangedRange determineEarliestChangedRange() {
		ChangedRange latestChangedRange = getLatestChangedRange();
		int exclusiveEndOfOld = latestChangedRange.getExlusiveEndOfOld();
		int exclusiveEndOfNew = latestChangedRange.getExlusiveEndOfNew();
		int firstIndex = latestChangedRange.getFirstIndex();

		while (firstIndex > 0
				&& newContent.charAt(exclusiveEndOfNew - 1) == newContent
						.charAt(firstIndex - 1)
				&& oldContent.charAt(exclusiveEndOfOld - 1) == newContent
						.charAt(firstIndex - 1)) {
			firstIndex--;
			exclusiveEndOfNew--;
			exclusiveEndOfOld--;
		}
		return new ChangedRange(firstIndex, exclusiveEndOfOld,
				exclusiveEndOfNew);
	}
}

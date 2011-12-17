package de.fkoeberle.autocommit.message;

public class ChangedTextFile {
	private final String path;
	private final String oldContent;
	private final String newContent;
	private ChangedRange changedRange;

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

	public ChangedRange getChangedRange() {
		if (changedRange == null) {
			changedRange = determineChangedRange();
		}
		return changedRange;
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
		return new ChangedRange(start, exclusiveEndOfNew, exclusiveEndOfOld);
	}
}

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
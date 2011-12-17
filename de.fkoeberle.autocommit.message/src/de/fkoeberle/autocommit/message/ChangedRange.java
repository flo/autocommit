package de.fkoeberle.autocommit.message;

public final class ChangedRange {
	private final int start;
	private final int exclusiveEndOfNew;
	private final int exclusiveEndOfOld;

	public ChangedRange(int start, int exclusiveEndOfNew, int exclusiveEndOfOld) {
		this.start = start;
		this.exclusiveEndOfNew = exclusiveEndOfNew;
		this.exclusiveEndOfOld = exclusiveEndOfOld;
	}

	public int getFirstIndex() {
		return start;
	}

	public int getExlusiveEndOfNew() {
		return exclusiveEndOfNew;
	}

	public int getExlusiveEndOfOld() {
		return exclusiveEndOfOld;
	}

}
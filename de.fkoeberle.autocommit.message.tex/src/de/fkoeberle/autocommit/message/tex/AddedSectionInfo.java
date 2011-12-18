package de.fkoeberle.autocommit.message.tex;

public final class AddedSectionInfo {
	private final OutlineNodeDelta parentDelta;
	private final int addedSectionIndex;
	private final String charactersAddedBefore;
	private final String charactersAddedAfter;
	private final String charactersRemoved;

	public AddedSectionInfo(OutlineNodeDelta parentDelta,
			int addedSectionIndex, String textAddedBefore,
			String textAddedAfter, String textRemoved) {
		super();
		this.parentDelta = parentDelta;
		this.addedSectionIndex = addedSectionIndex;
		this.charactersAddedBefore = textAddedBefore;
		this.charactersAddedAfter = textAddedAfter;
		this.charactersRemoved = textRemoved;
	}

	public OutlineNodeDelta getParentDelta() {
		return parentDelta;
	}

	public int getAddedSectionIndex() {
		return addedSectionIndex;
	}

	public String getCharactersAddedBefore() {
		return charactersAddedBefore;
	}

	public String getCharactersAddedAfter() {
		return charactersAddedAfter;
	}

	public String getCharactersRemoved() {
		return charactersRemoved;
	}

	public OutlineNode getAddedSection() {
		return parentDelta.getNewOutlineNode().getChildNodes()
				.get(addedSectionIndex);
	}
}
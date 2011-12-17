package de.fkoeberle.autocommit.message.tex;

public final class AddedSectionInfo {
	private final OutlineNodeDelta parentDelta;
	private final int addedSectionIndex;
	private final int charactersAddedBefore;
	private final int charactersAddedAfter;
	private final int charactersRemoved;

	public AddedSectionInfo(OutlineNodeDelta parentDelta,
			int addedSectionIndex, int charactersAddedBefore,
			int charactersAddedAfter, int charactersRemoved) {
		super();
		this.parentDelta = parentDelta;
		this.addedSectionIndex = addedSectionIndex;
		this.charactersAddedBefore = charactersAddedBefore;
		this.charactersAddedAfter = charactersAddedAfter;
		this.charactersRemoved = charactersRemoved;
	}

	public OutlineNodeDelta getParentDelta() {
		return parentDelta;
	}

	public int getAddedSectionIndex() {
		return addedSectionIndex;
	}

	public int getCharactersAddedBefore() {
		return charactersAddedBefore;
	}

	public int getCharactersAddedAfter() {
		return charactersAddedAfter;
	}

	public int getCharactersRemoved() {
		return charactersRemoved;
	}

	public OutlineNode getAddedSection() {
		return parentDelta.getNewOutlineNode().getChildNodes()
				.get(addedSectionIndex);
	}
}
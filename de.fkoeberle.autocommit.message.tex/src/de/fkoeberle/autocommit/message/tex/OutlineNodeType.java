package de.fkoeberle.autocommit.message.tex;


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

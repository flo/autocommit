package de.fkoeberle.autocommit.message;

public class ModifiedFile extends AbstractAdaptableWithCache {
	private final String path;
	private final IFileContent oldContent;
	private final IFileContent newContent;

	public ModifiedFile(String path, IFileContent oldContent,
			IFileContent newContent) {
		this.path = path;
		this.oldContent = oldContent;
		this.newContent = newContent;
	}

	public String getPath() {
		return path;
	}

	public IFileContent getOldContent() {
		return oldContent;
	}

	public IFileContent getNewContent() {
		return newContent;
	}
}

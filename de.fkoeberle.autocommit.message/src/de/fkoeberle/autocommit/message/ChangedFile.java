package de.fkoeberle.autocommit.message;

public class ChangedFile {
	private String path;
	private IFileContent oldContent;
	private IFileContent newContent;

	public ChangedFile(String path, IFileContent oldContent,
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

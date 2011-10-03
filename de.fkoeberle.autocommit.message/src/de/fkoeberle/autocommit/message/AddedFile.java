package de.fkoeberle.autocommit.message;

public class AddedFile {
	private String path;
	private IFileContent newContent;

	public AddedFile(String path, IFileContent newContent) {
		this.path = path;
		this.newContent = newContent;
	}

	public String getPath() {
		return path;
	}

	public IFileContent getNewContent() {
		return newContent;
	}
}

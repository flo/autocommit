package de.fkoeberle.autocommit.message;

public class AddedFile extends AbstractAdaptableWithCache {
	private final String path;
	private final IFileContent newContent;

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

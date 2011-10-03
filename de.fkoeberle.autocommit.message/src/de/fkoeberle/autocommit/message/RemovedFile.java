package de.fkoeberle.autocommit.message;

public class RemovedFile {
	private String path;
	private IFileContent oldContent;

	public RemovedFile(String path, IFileContent oldContent) {
		this.path = path;
		this.oldContent = oldContent;
	}

	public String getPath() {
		return path;
	}

	public IFileContent getOldContent() {
		return oldContent;
	}
}

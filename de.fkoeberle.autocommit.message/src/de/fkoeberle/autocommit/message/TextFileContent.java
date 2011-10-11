package de.fkoeberle.autocommit.message;

import java.io.IOException;

public class TextFileContent implements ITextFileContent {
	private String cachedString;
	private final IFileContent fileContent;

	public TextFileContent(IFileContent fileContent) {
		this.fileContent = fileContent;
	}

	@Override
	public String getContentAsString() throws IOException {
		if (cachedString == null) {
			byte[] bytes = fileContent.getBytesForReadOnlyPurposes();
			cachedString = new String(bytes);
		}
		return cachedString;
	}
}

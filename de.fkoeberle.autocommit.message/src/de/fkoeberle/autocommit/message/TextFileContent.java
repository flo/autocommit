package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.lang.ref.SoftReference;

public class TextFileContent implements ITextFileContent {
	private SoftReference<String> cachedContentString;
	private final IFileContent fileContent;

	public TextFileContent(IFileContent fileContent) {
		this.fileContent = fileContent;
	}

	@Override
	public String getContentAsString() throws IOException {
		String chars;
		if (cachedContentString != null) {
			chars = cachedContentString.get();
			if (chars != null) {
				return chars;
			}
		}
		byte[] bytes = fileContent.getBytesForReadOnlyPurposes();
		String string = new String(bytes);
		cachedContentString = new SoftReference<String>(string);
		return string;
	}
}

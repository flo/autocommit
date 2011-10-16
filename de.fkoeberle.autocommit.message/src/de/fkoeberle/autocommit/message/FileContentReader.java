package de.fkoeberle.autocommit.message;

import java.io.IOException;

public class FileContentReader {

	public String getStringFor(IFileContent fileContent) throws IOException {
		byte[] bytes = fileContent.getBytesForReadOnlyPurposes();
		return new String(bytes);
	}

}

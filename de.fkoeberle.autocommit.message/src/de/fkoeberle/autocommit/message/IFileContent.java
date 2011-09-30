package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.io.OutputStream;

public interface IFileContent {
	long getSize() throws IOException;
	void copyTo(OutputStream outputStream) throws IOException;
}

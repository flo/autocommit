package de.fkoeberle.autocommit.message.java.test;

import java.io.IOException;
import java.io.OutputStream;

import de.fkoeberle.autocommit.message.IFileContent;

public class FileContent implements IFileContent {
	private final byte[] buffer;
	private final String content;

	public FileContent(String content) {
		this.buffer = content.getBytes();
		this.content = content;
	}

	@Override
	public byte[] getBytesForReadOnlyPurposes() throws IOException {
		return buffer;
	}

	@Override
	public long getSize() throws IOException {
		return buffer.length;
	}

	@Override
	public void copyTo(OutputStream outputStream) throws IOException {
		outputStream.write(buffer);
	}

	@Override
	public String toString() {
		return content;
	}

}

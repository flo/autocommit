package de.fkoeberle.autocommit.message.java.test;

import java.io.IOException;
import java.io.OutputStream;

import de.fkoeberle.autocommit.message.IAdaptableWithCache;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.ITextFileContent;
import de.fkoeberle.autocommit.message.java.IJavaFileContent;
import de.fkoeberle.autocommit.message.java.JavaFileContent;
import de.fkoeberle.autocommit.message.java.JavaFileContentFactory;

public class FileContent implements IFileContent, IAdaptableWithCache,
		ITextFileContent {
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

	@Override
	public String getContentAsString() throws IOException {
		return content;
	}

	@Override
	public <T> T getSharedAdapter(Class<T> adapterClass) {
		if (adapterClass.isInstance(this)) {
			return adapterClass.cast(this);
		}
		if (adapterClass.equals(IJavaFileContent.class)) {
			JavaFileContentFactory javaFileContentFactory = new JavaFileContentFactory();
			return adapterClass.cast(javaFileContentFactory.getAdapter(this,
					JavaFileContent.class));
		}
		return null;
	}

}

package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;

import de.fkoeberle.autocommit.message.IFileContent;

class FileContent implements IFileContent{
	private ObjectId objectId;
	private ObjectReader reader;
	private ObjectLoader loader;
	
	public FileContent(ObjectId objectId, ObjectReader reader) {
		this.objectId = objectId;
		this.reader = reader;
	}
	
	private ObjectLoader getLoader() throws IOException {
		if (loader == null) {
			loader = reader.open(objectId);
		}
		return loader;
	}

	public long getSize() throws IOException {
		return getLoader().getSize();
	}
	
	public void copyTo(OutputStream outputStream) throws IOException {
		getLoader().copyTo(outputStream);
	}
	
}
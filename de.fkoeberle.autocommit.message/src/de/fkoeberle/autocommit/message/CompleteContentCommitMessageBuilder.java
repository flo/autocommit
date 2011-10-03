package de.fkoeberle.autocommit.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;



public final class CompleteContentCommitMessageBuilder implements
		ICommitMessageBuilder {
	StringBuilder stringBuilder = new StringBuilder();

	@Override
	public String buildMessage() {
		return stringBuilder.toString();
	}

	@Override
	public void addDeletedFile(String path, IFileContent oldContent) throws IOException {
		stringBuilder.append("Deleted ");
		stringBuilder.append(path);
		stringBuilder.append(":\n");
		String content = read(oldContent);
		stringBuilder.append(content);
	}

	@Override
	public void addChangedFile(String path, IFileContent oldContent,
			IFileContent newContent) throws IOException {
		stringBuilder.append("Modified ");
		stringBuilder.append(path);
		stringBuilder.append("\n");
		stringBuilder.append("Old content:");
		stringBuilder.append(read(oldContent));	
		stringBuilder.append("\nNew content:\n");
		stringBuilder.append(read(newContent));	
	}

	private String read(IFileContent oldContent) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream((int)oldContent.getSize());
		oldContent.copyTo(buffer);
		String content = new String(buffer.toByteArray());
		return content;
	}

	@Override
	public void addAddedFile(String path, IFileContent newContent) throws IOException {
		stringBuilder.append("Added ");
		stringBuilder.append(path);
		stringBuilder.append(":\n");
		String content = read(newContent);
		stringBuilder.append(content);
	}
}
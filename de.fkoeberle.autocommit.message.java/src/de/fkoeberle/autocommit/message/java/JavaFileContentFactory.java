package de.fkoeberle.autocommit.message.java;

import org.eclipse.core.runtime.IAdapterFactory;

import de.fkoeberle.autocommit.message.IFileContent;

public class JavaFileContentFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (!(adaptableObject instanceof IFileContent)) {
			return null;
		}
		IFileContent fileContent = (IFileContent) adaptableObject;
		if (!(JavaFileContent.class).equals(adapterType)) {
			return null;
		}
		return new JavaFileContent(fileContent);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { JavaFileContent.class };
	}

}

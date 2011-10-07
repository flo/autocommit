package de.fkoeberle.autocommit.message;

import org.eclipse.core.runtime.IAdapterFactory;

public class TextFileContentFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (!(adaptableObject instanceof IFileContent)) {
			return null;
		}
		IFileContent fileContent = (IFileContent) adaptableObject;
		if (!(ITextFileContent.class).equals(adapterType)) {
			return null;
		}
		return new TextFileContent(fileContent);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return new Class[] { ITextFileContent.class };
	}

}

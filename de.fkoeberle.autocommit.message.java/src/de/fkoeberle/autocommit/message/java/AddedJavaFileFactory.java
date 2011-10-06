package de.fkoeberle.autocommit.message.java;

import org.eclipse.core.runtime.IAdapterFactory;

import de.fkoeberle.autocommit.message.AddedFile;

public class AddedJavaFileFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (!(adaptableObject instanceof AddedFile)) {
			return null;
		}
		AddedFile addedFile = (AddedFile) adaptableObject;
		if (!(AddedJavaFile.class).equals(adapterType)) {
			return null;
		}
		return new AddedJavaFile(addedFile);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { AddedJavaFile.class };
	}

}

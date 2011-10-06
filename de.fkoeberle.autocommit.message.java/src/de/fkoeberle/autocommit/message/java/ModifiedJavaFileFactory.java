package de.fkoeberle.autocommit.message.java;

import org.eclipse.core.runtime.IAdapterFactory;

import de.fkoeberle.autocommit.message.ModifiedFile;

public class ModifiedJavaFileFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (!(adaptableObject instanceof ModifiedFile)) {
			return null;
		}
		ModifiedFile modifiedFile = (ModifiedFile) adaptableObject;
		if (!(ModifiedJavaFile.class).equals(adapterType)) {
			return null;
		}
		return new ModifiedJavaFile(modifiedFile);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { ModifiedJavaFile.class };
	}

}

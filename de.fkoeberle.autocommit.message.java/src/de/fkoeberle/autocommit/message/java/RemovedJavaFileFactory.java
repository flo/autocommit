package de.fkoeberle.autocommit.message.java;

import org.eclipse.core.runtime.IAdapterFactory;

import de.fkoeberle.autocommit.message.RemovedFile;

public class RemovedJavaFileFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (!(adaptableObject instanceof RemovedFile)) {
			return null;
		}
		RemovedFile removedFile = (RemovedFile) adaptableObject;
		if (!(RemovedJavaFile.class).equals(adapterType)) {
			return null;
		}
		return new RemovedJavaFile(removedFile);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { RemovedJavaFile.class };
	}

}

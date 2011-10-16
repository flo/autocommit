package de.fkoeberle.autocommit.message.java.test;

import de.fkoeberle.autocommit.message.AdapterNotFoundException;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.ISession;
import de.fkoeberle.autocommit.message.java.IJavaFileContent;
import de.fkoeberle.autocommit.message.java.JavaFileContent;
import de.fkoeberle.autocommit.message.java.JavaFileContentFactory;

public class TestSession implements ISession {

	@Override
	public <T> T getSharedAdapter(Object adaptable, Class<T> adapterClass)
			throws AdapterNotFoundException {
		if (adapterClass.isInstance(adaptable)) {
			return adapterClass.cast(adaptable);
		}
		if (adapterClass.equals(IJavaFileContent.class)
				&& (adaptable instanceof IFileContent)) {
			JavaFileContentFactory javaFileContentFactory = new JavaFileContentFactory();
			return adapterClass.cast(javaFileContentFactory.getAdapter(
					adaptable,
					JavaFileContent.class));
		}
		return null;
	}

}

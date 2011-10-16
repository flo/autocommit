package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.ISession;

public interface IJavaFileContent {

	/**
	 * 
	 * @param session TODO
	 * @return an {@link CompilationUnit} which must not be modified.
	 * @throws IOException
	 */
	public abstract CompilationUnit getCompilationUnitForReadOnlyPurposes(ISession session)
			throws IOException;

}
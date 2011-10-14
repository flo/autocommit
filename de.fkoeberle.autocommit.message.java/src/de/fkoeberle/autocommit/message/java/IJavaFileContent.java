package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;

public interface IJavaFileContent {

	/**
	 * 
	 * @return an {@link CompilationUnit} which must not be modified.
	 * @throws IOException
	 */
	public abstract CompilationUnit getCompilationUnitForReadOnlyPurposes()
			throws IOException;

}
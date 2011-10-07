package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.ITextFileContent;
public class JavaFileContent {
	private SoftReference<CompilationUnit> cachedCompilationUnit;
	private final IFileContent fileContent;

	public JavaFileContent(IFileContent fileContent) {
		this.fileContent = fileContent;
	}

	private CompilationUnit createCompilationUnit(char[] fileContent) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileContent);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}

	/**
	 * 
	 * @return an {@link CompilationUnit} which must not be modified.
	 * @throws IOException
	 */
	public CompilationUnit getCompilationUnitForReadOnlyPurposes()
			throws IOException {
		CompilationUnit compilationUnit;
		if (cachedCompilationUnit != null) {
			compilationUnit = cachedCompilationUnit.get();
			if (compilationUnit != null) {
				return compilationUnit;
			}
		}
		ITextFileContent textFileContent = fileContent
				.getAdapter(ITextFileContent.class);
		char[] chars = textFileContent.getContentAsString().toCharArray();
		compilationUnit = createCompilationUnit(chars);
		cachedCompilationUnit = new SoftReference<CompilationUnit>(compilationUnit);
		return compilationUnit;
	}


}

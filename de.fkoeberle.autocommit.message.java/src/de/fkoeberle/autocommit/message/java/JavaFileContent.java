package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.IFileContent;
public class JavaFileContent {
	private SoftReference<CompilationUnit> cachedCompilationUnit;
	private SoftReference<String> cachedContentString;
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
		char[] chars = getContentAsString().toCharArray();
		compilationUnit = createCompilationUnit(chars);
		cachedCompilationUnit = new SoftReference<CompilationUnit>(compilationUnit);
		return compilationUnit;
	}

	public String getContentAsString() throws IOException {
		String chars;
		if (cachedContentString != null) {
			chars = cachedContentString.get();
			if (chars != null) {
				return chars;
			}
		}
		byte[] bytes = fileContent.getBytesForReadOnlyPurposes();
		String string = new String(bytes);
		cachedContentString = new SoftReference<String>(string);
		return string;
	}
}

package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.ITextFileContent;

public class JavaFileContent implements IJavaFileContent {
	private CompilationUnit cachedCompilationUnit;
	private final IFileContent fileContent;

	public JavaFileContent(IFileContent fileContent) {
		this.fileContent = fileContent;
	}

	private CompilationUnit createCompilationUnit(char[] fileContent) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileContent);
		Map<?, ?> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}


	@Override
	public CompilationUnit getCompilationUnitForReadOnlyPurposes()
			throws IOException {
		if (cachedCompilationUnit == null) {
			ITextFileContent textFileContent = fileContent
					.getSharedAdapter(ITextFileContent.class);
			char[] chars = textFileContent.getContentAsString().toCharArray();
			cachedCompilationUnit = createCompilationUnit(chars);
		}
		return cachedCompilationUnit;
	}


}

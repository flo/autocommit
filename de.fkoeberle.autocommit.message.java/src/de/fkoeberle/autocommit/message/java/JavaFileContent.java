package de.fkoeberle.autocommit.message.java;

import java.lang.ref.SoftReference;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.IFileContent;
public class JavaFileContent {
	private SoftReference<AST> cachedAST;
	private SoftReference<String> cachedContentString;
	private final IFileContent fileContent;

	public JavaFileContent(IFileContent fileContent) {
		this.fileContent = fileContent;
	}

	private AST createSyntaxTree(char[] fileContent) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileContent);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit.getAST();
	}

	/**
	 * 
	 * @return an {@link AST} which must not be modified.
	 */
	public AST getASTForReadOnlyPurposes() {
		AST ast;
		if (cachedAST != null) {
			ast = cachedAST.get();
			if (ast != null) {
				return ast;
			}
		}
		char[] chars = getContentAsString().toCharArray();
		ast = createSyntaxTree(chars);
		cachedAST = new SoftReference<AST>(ast);
		return ast;
	}

	public String getContentAsString() {
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

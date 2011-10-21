package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.FileContentReader;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.SoftReferenceOrNull;

public class CachingJavaFileContentParser {
	private final WeakHashMap<IFileContent, SoftReferenceOrNull<CompilationUnit>> cache;

	@InjectedBySession
	private FileContentReader reader;

	public CachingJavaFileContentParser() {
		this.cache = new WeakHashMap<IFileContent, SoftReferenceOrNull<CompilationUnit>>();
	}

	private static CompilationUnit createCompilationUnit(char[] fileContent) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileContent);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<?, ?> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}

	public CompilationUnit getInstanceFor(IFileContent fileContent)
			throws IOException {
		SoftReferenceOrNull<CompilationUnit> softReferenceOrNull = cache
				.get(fileContent);
		CompilationUnit compUnit = null;
		if (softReferenceOrNull != null) {
			SoftReference<CompilationUnit> softReference = softReferenceOrNull
					.getSoftReference();
			if (softReference == null) {
				return null;
			} else {
				compUnit = softReference.get();
			}

		}
		if (compUnit == null) {
			String s = reader.getStringFor(fileContent);
			char[] chars = s.toCharArray();
			compUnit = createCompilationUnit(chars);
			cache.put(fileContent, new SoftReferenceOrNull<CompilationUnit>(
					compUnit));
		}
		return compUnit;
	}

}

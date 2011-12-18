package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.FileContent;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.CachingJavaFileContentParser;
import de.fkoeberle.autocommit.message.java.DeclarationListDelta;

public class DeclarationListUtil {

	static DeclarationListDelta createDelta(String oldContent,
			String newContent) {
		try {
			Session session = new Session();
			CompilationUnit oldCompilationUnit = createCompilationUnit(session,
					oldContent);
			CompilationUnit newCompilationUnit = createCompilationUnit(session,
					newContent);
			return new DeclarationListDelta(oldCompilationUnit,
					newCompilationUnit);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static CompilationUnit createCompilationUnit(Session session,
			String content)
			throws IOException {
		FileContent fileContent = new FileContent(content);
		CachingJavaFileContentParser parser = session
				.getInstanceOf(CachingJavaFileContentParser.class);
		return parser.getInstanceFor(fileContent);
	}

}

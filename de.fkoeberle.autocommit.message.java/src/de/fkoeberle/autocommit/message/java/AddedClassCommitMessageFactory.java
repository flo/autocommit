package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.AddedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;

public class AddedClassCommitMessageFactory implements ICommitMessageFactory {
	private static final Set<String> DOT_JAVA = Collections.singleton("java");

	public AddedClassCommitMessageFactory() {
	}

	@Override
	public String build(FileSetDelta delta) {
		if (!delta.getFileExtensions().equals(DOT_JAVA)) {
            return null;
        }
		if (delta.getChangedFiles().size() > 0) {
			return null;
		}
		if (delta.getRemovedFiles().size() > 0) {
			return null;
		}
		if (delta.getAddedFiles().size() != 1) {
			return null;
		}

		AddedFile addedFile = delta.getAddedFiles().get(0);
		AddedJavaFile addedJavaFile = addedFile.getAdapter(AddedJavaFile.class);
		JavaFileContent content = addedJavaFile.getNewJavaContent();
		CompilationUnit compilationUnit;
		try {
			compilationUnit = content.getCompilationUnitForReadOnlyPurposes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		IJavaElement javaElement = compilationUnit.getJavaElement();
		if (!(javaElement instanceof ICompilationUnit)) {
			return null;
		}
		ICompilationUnit javaData = ((ICompilationUnit) javaElement);
		IType primaryType = javaData.findPrimaryType();
		String primaryTypeName = primaryType.getTypeQualifiedName();
		return "Added " + primaryTypeName;
	}

}

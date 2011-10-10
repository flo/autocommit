package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.AddedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.IFileContent;

public class AddedClassCMF implements ICommitMessageFactory {
	private static final Set<String> DOT_JAVA = Collections.singleton("java");

	public AddedClassCMF() {
	}

	@Override
	public String createMessageFor(FileSetDelta delta) {
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
		IFileContent genericContent = addedFile.getNewContent();
		JavaFileContent content = genericContent
				.getAdapter(JavaFileContent.class);
		CompilationUnit compilationUnit;
		try {
			compilationUnit = content.getCompilationUnitForReadOnlyPurposes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		List<?> topLevelTypes = compilationUnit.types();
		if (topLevelTypes.size() != 1) {
			// no support for:
			// 1. empty java files because they don't contain a class obviously
			// 2. java files with more then 1 top level type
			// since it's bad practice
			return null;
		}
		AbstractTypeDeclaration topLevelType = (AbstractTypeDeclaration) (topLevelTypes
				.get(0));
		String name = topLevelType.getName().getIdentifier();
		if (topLevelType instanceof TypeDeclaration) {
			TypeDeclaration type = (TypeDeclaration) topLevelType;
			if (type.isInterface()) {
				return "Added an interface called " + name;
			} else {
				boolean stub = isClassAStub(compilationUnit, type);
				if (stub) {
					return "Added a stub version of the class " + name;
				} else {
					return "Added a class called " + name;
				}
			}
		} else if (topLevelType instanceof EnumDeclaration) {
			return "Added an enum called " + name;
		} else if (topLevelType instanceof AnnotationTypeDeclaration) {
			return "Added an annotation type called " + name;
		} else {
			// New unknown top level type can't be handled
			return null;
		}

	}

	private boolean isClassAStub(CompilationUnit compilationUnit,
			TypeDeclaration declaration) {
		if (declaration.getFields().length > 0) {
			return false;
		}
		if (declaration.getTypes().length > 0) {
			return false;
		}
		MethodDeclaration[] methods = declaration.getMethods();
		if (declaration.getMethods().length == 0) {
			return true;
		}
		for (MethodDeclaration method : methods) {
			List<?> statements = method.getBody().statements();
			if (statements.size() > 1) {
				return false;
			}
			for (Object statementObject : statements) {
				if (!(statementObject instanceof ReturnStatement)) {
					return false;
				}
				ReturnStatement returnStatement = (ReturnStatement) statementObject;
				Expression expression = returnStatement.getExpression();
				if (expression != null) {
					boolean simple = (expression instanceof NullLiteral
							|| expression instanceof NumberLiteral || expression instanceof BooleanLiteral);
					if (!simple) {
						return false;
					}
				}
			}
		}

		return true;
	}
}

/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.factories;

import java.io.IOException;
import java.util.List;

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
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.CachingJavaFileContentParser;

public class AddedTypeCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate addedInterfaceMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedStubClassMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedClassMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedEnumMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedAnotationMessage;

	@InjectedBySession
	private FileSetDelta delta;

	@InjectedBySession
	private CachingJavaFileContentParser compilationUnitProvider;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	public AddedTypeCMF() {
	}

	@Override
	public String createMessage() throws IOException {
		if (!extensions.containsOnly("java")) {
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
		CompilationUnit compilationUnit = compilationUnitProvider
				.getInstanceFor(genericContent);

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
				return addedInterfaceMessage.createMessageWithArgs(name);
			} else {
				boolean stub = isClassAStub(type);
				if (stub) {
					return addedStubClassMessage.createMessageWithArgs(name);
				} else {
					return addedClassMessage.createMessageWithArgs(name);
				}
			}
		} else if (topLevelType instanceof EnumDeclaration) {
			return addedEnumMessage.createMessageWithArgs(name);
		} else if (topLevelType instanceof AnnotationTypeDeclaration) {
			return addedAnotationMessage.createMessageWithArgs(name);
		} else {
			// New unknown top level type can't be handled
			return null;
		}

	}

	private boolean isClassAStub(TypeDeclaration declaration) {
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

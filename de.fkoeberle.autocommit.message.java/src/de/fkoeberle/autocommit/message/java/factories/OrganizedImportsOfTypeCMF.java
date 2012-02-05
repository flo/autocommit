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
import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.SingleChangedJavaFileView;
import de.fkoeberle.autocommit.message.java.helper.TypeUtil;
import de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;

public class OrganizedImportsOfTypeCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate organizedImportsOfClassMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate organizedImportsOfInterfaceMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate organizedImportsOfEnumMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate organizedImportsOfAnnotationMessage;

	@InjectedBySession
	private SingleChangedJavaFileView singleChangedJavaFileView;

	private static final EnumSet<BodyDeclarationChangeType> importsChangedOnly = EnumSet
			.of(BodyDeclarationChangeType.IMPORTS);

	@Override
	public String createMessage() throws IOException {
		JavaFileDelta javaFileDelta = singleChangedJavaFileView.getDelta();
		if (javaFileDelta == null) {
			return null;
		}

		if (!javaFileDelta.getChangeTypes().equals(importsChangedOnly)) {
			return null;
		}
		CompilationUnit compilationUnit = javaFileDelta.getNewDeclaration();
		@SuppressWarnings("unchecked")
		List<AbstractTypeDeclaration> types = compilationUnit.types();
		if (types.size() != 1) {
			return null;
		}
		AbstractTypeDeclaration type = types.get(0);
		CommitMessageTemplate message;
		if (type instanceof TypeDeclaration) {
			TypeDeclaration classOrInterface = (TypeDeclaration) type;
			if (classOrInterface.isInterface()) {
				message = organizedImportsOfInterfaceMessage;
			} else {
				message = organizedImportsOfClassMessage;
			}
		} else if (type instanceof EnumDeclaration) {
			message = organizedImportsOfEnumMessage;
		} else if (type instanceof AnnotationTypeDeclaration) {
			message = organizedImportsOfAnnotationMessage;
		} else {
			assert false : "Unhandled type"; //$NON-NLS-1$
			return null;
		}
		return message.createMessageWithArgs(TypeUtil.nameOf(type));
	}

}

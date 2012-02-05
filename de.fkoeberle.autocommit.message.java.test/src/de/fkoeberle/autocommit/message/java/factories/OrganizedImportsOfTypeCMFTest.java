/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.factories;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.factories.OrganizedImportsOfTypeCMF;

public class OrganizedImportsOfTypeCMFTest {

	private OrganizedImportsOfTypeCMF createFactory(FileSetDelta delta) {
		OrganizedImportsOfTypeCMF factory = new OrganizedImportsOfTypeCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedImportToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;import x.y.Z;\n\nclass Test {}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.organizedImportsOfClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedImportFromInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;import x.y.Z;\n\ninterface Test {}",
				"package org.example;\n\ninterface Test {}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.organizedImportsOfInterfaceMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedSecondImportToAnnotation() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;import x.y.A;\n\n@interface Test {}",
				"package org.example;import x.y.A;import x.y.B;\n\n@interface Test {}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.organizedImportsOfAnnotationMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedSecondImportFromEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;import x.y.A;import x.y.B;\n\nenum Test {X,Y}",
				"package org.example;import x.y.A;\n\nenum Test {X,Y}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.organizedImportsOfEnumMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddImportToEmptyCompilationUnit() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;", "package org.example;import x.y.A;");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddImportToCompilationUnitWithTwoTypes() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/A.java",
				"package org.example; class A {} class B {}",
				"package org.example; import x.y.Z; class A {} class B {}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testOnlyFormationChanges() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/A.java",
				"package org.example; class A {}",
				"package org.example; class A { }");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemoveImportAndPackageDeclaration() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/A.java",
				"package org.example; import x.y.Z; class A {}", "class A {}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemoveImportAndJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/A.java",
				"package org.example; import x.y.Z; /** doc*/class A {}",
				"package org.example; class A {}");

		OrganizedImportsOfTypeCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}
}

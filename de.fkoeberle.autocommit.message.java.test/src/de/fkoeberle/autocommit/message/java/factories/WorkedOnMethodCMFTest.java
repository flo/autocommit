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
import de.fkoeberle.autocommit.message.java.factories.WorkedOnMethodCMF;

public class WorkedOnMethodCMFTest {
	private WorkedOnMethodCMF createFactory(FileSetDelta delta) {
		WorkedOnMethodCMF factory = new WorkedOnMethodCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 4);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testWorkedOnMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 1;}}}");
		WorkedOnMethodCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.workedOnMethodMessage
				.createMessageWithArgs("Test.Inner", "myMethod", "String, int",
						"Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testWorkedOnConstructor() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{Inner(String s, int i) {}}",
				"package org.example;\n\nclass Test {class Inner{/** changed constructor */Inner(String s, int i) {}}");
		WorkedOnMethodCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.workedOnConstructorMessage
				.createMessageWithArgs("Test.Inner", "Inner", "String, int",
						"Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testMadeClassPublicWhileChangingMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}",
				"package org.example;\n\nclass Test {public class Inner{int myMethod(String s, int i) {return 1;}}}");
		WorkedOnMethodCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

}

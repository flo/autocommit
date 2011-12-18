package de.fkoeberle.autocommit.message.java;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class AddedMethodCMFTest {

	private AddedMethodCMF createFactory(FileSetDelta delta) {
		AddedMethodCMF factory = new AddedMethodCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 4);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedFirstMethodToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{}}",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}");
		AddedMethodCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedMethodMessage
				.createMessageWithArgs("Test.Inner", "myMethod", "String, int",
						"Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;\n\nclass Test {int y;}");
		AddedMethodCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testRemovedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int y;}}",
				"package org.example;\n\nclass Test {}");
		AddedMethodCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testAddedFirstConstructorToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{}}",
				"package org.example;\n\nclass Test {class Inner{Inner(String s, int i) {}}}");
		AddedMethodCMF factory = createFactory(builder.build());

		factory.addedConstructorMessage
				.setValue(factory.addedConstructorMessage.getValue()
						+ " [constructor|{0}|{1}|{2}|{3}]");
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedConstructorMessage
				.createMessageWithArgs("Test.Inner", "Inner", "String, int",
						"Inner");
		assertEquals(expectedMessage, actualMessage);
	}
}

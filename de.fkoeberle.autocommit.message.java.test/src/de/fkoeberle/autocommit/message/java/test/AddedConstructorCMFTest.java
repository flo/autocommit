package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.AddedConstructorCMF;

public class AddedConstructorCMFTest {
	private AddedConstructorCMF createFactory(FileSetDelta delta) {
		AddedConstructorCMF factory = new AddedConstructorCMF();
		Session session = new Session(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedFirstConstructorToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{}}",
				"package org.example;\n\nclass Test {class Inner{Inner(String s, int i) {}}}");
		AddedConstructorCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedConstructorMessage
				.createMessageWithArgs("Test.Inner");

		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;\n\nclass Test {int y;}");
		AddedConstructorCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testRemovedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int y;}}",
				"package org.example;\n\nclass Test {}");
		AddedConstructorCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testAddedFirstMethodToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{}}",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}");
		AddedConstructorCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);

	}
}

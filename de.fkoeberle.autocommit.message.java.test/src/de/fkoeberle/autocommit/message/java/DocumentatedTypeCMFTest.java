package de.fkoeberle.autocommit.message.java;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class DocumentatedTypeCMFTest {
	private DocumentedTypeCMF createFactory(FileSetDelta delta) {
		DocumentedTypeCMF factory = new DocumentedTypeCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedJavaDocToInnerClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{}}",
				"package org.example;\n\nclass Test {/** Hello World*/class Inner{}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;\n\n/** Hello World*/class Test {}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedJavaDocToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\n/** Hello World*/class Test {}",
				"package org.example;\n\nclass Test {}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocAndFieldToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;\n\n/** Hello World*/class Test {int x;}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocFirstMethodAndChangedSecondMethod()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x() {return 0;} int y() {return 0;}}",
				"package org.example;\n\n/** Hello World*/class Test {int x() {return 0;} int y() {return 1;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangedFirstMethodAndAddedJavaDocSecondMethod()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x() {return 0;} int y() {return 0;}}",
				"package org.example;\n\nclass Test {int x() {return 1;} /** Hello World*/ int y() {return 0;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedJavaDocFromFirstAndAddedJavaDocToSecondMethod()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {/** X */ int x() {return 0;} int y() {return 0;}}",
				"package org.example;\n\nclass Test {int x() {return 0;} /** Y */ int y() {return 0;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToFirstAndRemovedJavaDocFromSecondMethod()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x() {return 0;} /** Y */ int y() {return 0;}}",
				"package org.example;\n\nclass Test {/** X */ int x() {return 0;} int y() {return 0;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangedJavaDocOfConstructor() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x; /** default */Test(){x = 0;}}",
				"package org.example;\n\nclass Test {int x; /** sets x to 0 */Test(){x = 0;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangedJavaDocOfTwoMethods() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {/** X */ int x() {return 0;} /** Y */ int y() {return 0;}}",
				"package org.example;\n\nclass Test {/** X2 */ int x() {return 0;} /** Y2 */ int y() {return 0;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testTwoInnerClassesGotDocumented() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Alpha{int a;} class Beta{int b;}}",
				"package org.example;\n\nclass Test {class Alpha{/** a */int a;} class Beta{/** b */int b;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testTwoInnerInnerClassesGotDocumented() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Alpha{class A{int a;}} class Beta{class B{int b;}}}",
				"package org.example;\n\nclass Test {class Alpha{class A{/** a */int a;}} class Beta{class B{/** b */int b;}}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToTwoEnumConstants() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {enum SmallNumber{ONE, TWO, THREE;}}",
				"package org.example;\n\nclass Test {enum SmallNumber{/** 1 */ONE, /** 2 */ TWO, THREE;}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedEnumMessage
				.createMessageWithArgs("Test.SmallNumber");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToEnumMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {enum SmallNumber{ONE, TWO, THREE; String toString() {return \"n\";}}}",
				"package org.example;\n\nclass Test {enum SmallNumber{ONE,  TWO, THREE; /** returns just n */ String toString() {return \"n\";}}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedEnumMessage
				.createMessageWithArgs("Test.SmallNumber");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToAnnotationMember() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {@interface SmallNumber{int id();}}",
				"package org.example;\n\nclass Test {@interface SmallNumber{/** an unique number */int id();}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedAnnotationMessage
				.createMessageWithArgs("Test.SmallNumber");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToInterfaceMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {interface SmallNumber{int id();}}",
				"package org.example;\n\nclass Test {interface SmallNumber{/** an unique number */int id();}}");
		DocumentedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.documentedInterfaceMessage
				.createMessageWithArgs("Test.SmallNumber");
		assertEquals(expectedMessage, actualMessage);
	}

}

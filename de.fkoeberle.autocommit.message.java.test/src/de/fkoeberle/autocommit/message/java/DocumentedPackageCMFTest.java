package de.fkoeberle.autocommit.message.java;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.DocumentedPackageCMF;

public class DocumentedPackageCMFTest {
	private DocumentedPackageCMF createFactory(FileSetDelta delta) {
		DocumentedPackageCMF factory = new DocumentedPackageCMF();
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testDocumentedSingleFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyInterface.java";
		builder.addChangedFile(path,
				"package org.example;\n\ninterface MyInterface {int m();}",
				"package org.example;\n\n/** doc */interface MyInterface {int m();}");

		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedPackageInFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyInterface.java";
		builder.addChangedFile(path,
				"package org.example;\n\ninterface MyInterface {int m();}",
				"/** doc */package org.example;\n\ninterface MyInterface {int m();}");

		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAdddedJavaDocAndAnnotation() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyInterface.java";
		builder.addChangedFile(
				path,
				"package org.example;\n\ninterface MyInterface {int m();}",
				"/** doc */@MyAnnotation package org.example;\n\ninterface MyInterface {int m();}");

		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToClassAndChangedAMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/MyClass.java",
				"package org.example;\n\nclass MyClass {int m() {return 0;};}",
				"package org.example;\n\n/** doc */class MyClass {int m() {return 1;};}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToClassAndRemovedPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/MyClass.java",
				"package org.example;\n\nclass MyClass {int m() {return 0;};}",
				"/** doc */class MyClass {int m() {return 0;};}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedJavaDocToClassAndAddedImport() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/MyClass.java",
				"package org.example;\n\nclass MyClass {int m() {return 0;};}",
				"package org.example;import x.y.Nice;/** doc */class MyClass {int m() {return 0;};}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesInSamePackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/MyClass.java",
				"package org.example;\n\nclass MyClass {void m() {};}",
				"package org.example;\n\n/** doc */class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\n/** doc */interface MyInterface {int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedAMethodInTwoFilesInSamePackage()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/IClass.java",
				"package org.example;\n\nclass MyClass {void m() {};}",
				"package org.example;\n\nclass MyClass {/** doc */void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {/** doc */int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testMoveJavaDocBetweenTwoFilesOfTheSamePackage()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/IClass.java",
				"package org.example;\n\nclass MyClass {/** doc */ void m() {};}",
				"package org.example;\n\nclass MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {/** doc */int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesInSameSubPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\n/** doc */class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\n/** doc */interface MyInterface {int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInSubPackagesOfMessage
				.createMessageWithArgs("org.example");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesInNoCommonPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/com/example/test1/MyClass.java",
				"package com.example.test1;\n\nclass MyClass {void m() {};}",
				"package com.example.test1;\n\n/** doc */class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\n/** doc */interface MyInterface {int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesWithSecondInDefaultPackage()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/com/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\n/** doc */class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/src/MyInterface.java",
				"interface MyInterface {int  m();}",
				"/** doc */interface MyInterface {int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesWithFirstInDefaultPackage()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyInterface.java",
				"interface MyInterface {int  m();}",
				"/** doc */interface MyInterface {int m();}");
		builder.addChangedFile("/project1/src/com/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\n/** doc */class MyClass {void m() {\n};}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesWithFirstInDefaultPackage1b()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyInterface.java",
				"interface MyInterface {int  m();}",
				"/** doc */interface MyInterface {int m();}");
		builder.addChangedFile("/project1/src/org/example/MyClass.java",
				"package org.example;\n\nclass MyClass {void m() {};}",
				"package org.example;\n\n/** doc */class MyClass {void m() {};}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesWithFirstInDefaultPackage2()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyEnum.java",
				"\nenum MyEnum {\nNEW,OLD;\n}\n",
				"\n/** doc */enum MyEnum {\nNEW,OLD; \n}\n");
		builder.addChangedFile(
				"/project1/src/org/example/NumberProvider.java",
				"package org.example;\n\npublic class NumberProvider {\n\n\tpublic int returnANumber(Object newParam) {\n\t\t// TODO Auto-generated method stub\n\t\treturn 21;\n\n\t}\n\n}",
				"package org.example;\n\n/** doc */public class NumberProvider {\n\n\tpublic int returnANumber(Object newParam) {\n\t\t// TODO Auto-generated method stub\n\t\treturn 21;\n\t}\n\n}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesWithFirstInDefaultPackage3()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyEnum.java",
				"enum MyEnum {\nNEW,OLD;\n}\n",
				"/** doc */enum MyEnum {\nNEW,OLD;\n}\n");
		builder.addChangedFile("/project1/src/org/example/NumberProvider.java",
				"package org.example;\n\nclass NumberProvider {}",
				"package org.example;\n\n/** doc */class NumberProvider {}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedTwoFilesInDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/IClass.java",
				"class MyClass {void m() {};}",
				"/** doc */class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/MyInterface.java",
				"interface MyInterface {int  m();}",
				"/** doc */interface MyInterface {int m();}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInTheDefaultPackageMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedOneEnumInDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/MyEnum.java",
				"enum MyEnum {NEW, OLD;}", "/** doc */enum MyEnum {NEW, OLD;}");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInTheDefaultPackageMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testDocumentedOneClassInDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/MyClass.java", "class MyClass {}",
				"/** doc */class MyClass { }");
		DocumentedPackageCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.documentedSourceInTheDefaultPackageMessage
				.createMessageWithArgs();
		assertEquals(expectedMessage, actualMessage);
	}
}

package de.fkoeberle.autocommit.message.java;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class FormattedJavaFilesOfPackageCMFTest {
	private FormattedJavaFilesOfPackageCMF createFactory(FileSetDelta delta) {
		FormattedJavaFilesOfPackageCMF factory = new FormattedJavaFilesOfPackageCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testFormattedSingleFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyInterface.java";
		builder.addChangedFile(path,
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesInSamePackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/IClass.java",
				"package org.example;\n\nclass MyClass {void m() {};}",
				"package org.example;\n\nclass MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {int m();}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceInPackageMessage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesInSameSubPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\nclass MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {int m();}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceInSubPackagesOfMessage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesInNoCommonPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/com/example/test1/MyClass.java",
				"package com.example.test1;\n\nclass MyClass {void m() {};}",
				"package com.example.test1;\n\nclass MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {int m();}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesWithSecondInDefaultPackage()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/com/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\nclass MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/src/MyInterface.java",
				"interface MyInterface {int  m();}",
				"interface MyInterface {int m();}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesWithFirstInDefaultPackage()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyInterface.java",
				"interface MyInterface {int  m();}",
				"interface MyInterface {int m();}");
		builder.addChangedFile("/project1/src/com/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\nclass MyClass {void m() {\n};}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesWithFirstInDefaultPackage1b()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyInterface.java",
				"interface MyInterface {int  m();}",
				"interface MyInterface {int m();}");
		builder.addChangedFile("/project1/src/org/example/MyClass.java",
				"package org.example;\n\nclass MyClass {void m() {};}",
				"package org.example;\n\nclass MyClass {void m() {\n};}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesWithFirstInDefaultPackage2()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyEnum.java",
				"\nenum MyEnum {\nNEW,OLD;\n}\n",
				"\nenum MyEnum {\nNEW,OLD; \n}\n");
		builder.addChangedFile(
				"/project1/src/org/example/NumberProvider.java",
				"package org.example;\n\npublic class NumberProvider {\n\n\tpublic int returnANumber(Object newParam) {\n\t\t// TODO Auto-generated method stub\n\t\treturn 21;\n\n\t}\n\n}",
				"package org.example;\n\npublic class NumberProvider {\n\n\tpublic int returnANumber(Object newParam) {\n\t\t// TODO Auto-generated method stub\n\t\treturn 21;\n\t}\n\n}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesWithFirstInDefaultPackage3()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/src/MyEnum.java",
				"\nenum MyEnum {\nNEW,OLD;\n}\n",
				"\nenum MyEnum {\nNEW,OLD; \n}\n");
		builder.addChangedFile("/project1/src/org/example/NumberProvider.java",
				"package org.example;\n\nclass NumberProvider {\n}",
				"package org.example;\n\nclass NumberProvider {}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedTwoFilesInDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/IClass.java",
				"class MyClass {void m() {};}",
				"class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/MyInterface.java",
				"interface MyInterface {int  m();}",
				"interface MyInterface {int m();}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceInTheDefaultPackageMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedOneEnumInDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/MyEnum.java",
				"enum MyEnum {NEW,OLD;}", "enum MyEnum {NEW, OLD;}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceInTheDefaultPackageMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testFormattedOneClassInDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/MyClass.java", "class MyClass {}",
				"class MyClass { }");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceInTheDefaultPackageMessage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}
}

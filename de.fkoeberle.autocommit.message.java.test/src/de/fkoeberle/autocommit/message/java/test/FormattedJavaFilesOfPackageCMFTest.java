package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.FormattedJavaFilesOfPackageCMF;

public class FormattedJavaFilesOfPackageCMFTest {
	private FormattedJavaFilesOfPackageCMF createFactory(FileSetDelta delta) {
		FormattedJavaFilesOfPackageCMF factory = new FormattedJavaFilesOfPackageCMF();
		Session session = new Session(delta);
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
		builder.addChangedFile("/project1/com/example/test1/IClass.java",
				"package org.example.test1;\n\nclass MyClass {void m() {};}",
				"package org.example.test1;\n\nclass MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {int m();}");
		FormattedJavaFilesOfPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedSourceMessage
				.createMessageWithArgs("org.example");
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
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}
}

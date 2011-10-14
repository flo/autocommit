package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;

import de.fkoeberle.autocommit.message.java.DeclarationDelta;
import de.fkoeberle.autocommit.message.java.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.IJavaFileContent;

public class DeclarationListDeltaTest {

	private static DeclarationListDelta createDelta(String oldContent,
			String newContent) {
		try {
			CompilationUnit oldCompilationUnit = createCompilationUnit(oldContent);
			CompilationUnit newCompilationUnit = createCompilationUnit(newContent);
			return new DeclarationListDelta(oldCompilationUnit, newCompilationUnit);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static CompilationUnit createCompilationUnit(String content)
			throws IOException {
		FileContent fileContent = new FileContent(content);
		IJavaFileContent javaFileContent = fileContent
				.getSharedAdapter(IJavaFileContent.class);
		return javaFileContent.getCompilationUnitForReadOnlyPurposes();
	}

	@Test
	public void testAddedClass() {
		DeclarationListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}\n\n class OtherClass {\n}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());
		BodyDeclaration addedDeclaration = delta.getAddedDeclarations().get(0);
		assertTrue(addedDeclaration instanceof TypeDeclaration);
		assertEquals("OtherClass", ((TypeDeclaration) addedDeclaration)
				.getName()
				.getIdentifier());
	}

	@Test
	public void testRemoveClass() {
		DeclarationListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}\n\n class OtherClass {\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());

		BodyDeclaration removedDeclaration = delta.getRemovedDeclarations()
				.get(0);

		assertTrue(removedDeclaration instanceof TypeDeclaration);
		assertEquals("OtherClass", ((TypeDeclaration) removedDeclaration).getName()
				.getIdentifier());
		
	}

	@Test
	public void testModifyClass() {
		DeclarationListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"old value\";}\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"new value\";}\n}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		DeclarationDelta modifiedType = delta.getChangedDeclarations().get(0);

		BodyDeclaration oldDeclaration = modifiedType.getOldDeclaration();
		assertTrue(oldDeclaration instanceof TypeDeclaration);
		assertEquals("MainClass", ((TypeDeclaration) oldDeclaration).getName()
				.getIdentifier());

		BodyDeclaration newDeclaration = modifiedType.getNewDeclaration();
		assertTrue(newDeclaration instanceof TypeDeclaration);
		assertEquals("MainClass", ((TypeDeclaration) newDeclaration).getName()
				.getIdentifier());

	}

	@Test
	public void testModifyWhitespace() {
		DeclarationListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"some value\";}\n}",
				"package org.example;\n\nclass MainClass {\n\t String test() { return  \"some value\";}\n}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());
	}

	@Test
	public void testModifyWhitespaceInString() {
		DeclarationListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"some value\";}\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"some  value\";}\n}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		DeclarationDelta modifiedType = delta.getChangedDeclarations().get(0);

		BodyDeclaration oldDeclaration = modifiedType.getOldDeclaration();
		assertTrue(oldDeclaration instanceof TypeDeclaration);
		assertEquals("MainClass", ((TypeDeclaration) oldDeclaration).getName()
				.getIdentifier());

		BodyDeclaration newDeclaration = modifiedType.getNewDeclaration();
		assertTrue(newDeclaration instanceof TypeDeclaration);
		assertEquals("MainClass", ((TypeDeclaration) newDeclaration).getName()
				.getIdentifier());
	}

	// TODO test class, enum, interface and annotation body declaration changes
}

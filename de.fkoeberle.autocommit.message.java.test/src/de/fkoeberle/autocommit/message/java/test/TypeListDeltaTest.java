package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

import de.fkoeberle.autocommit.message.java.IJavaFileContent;
import de.fkoeberle.autocommit.message.java.TypeDelta;
import de.fkoeberle.autocommit.message.java.TypeListDelta;

public class TypeListDeltaTest {

	private static TypeListDelta createDelta(String oldContent,
			String newContent) {
		try {
			CompilationUnit oldCompilationUnit = createCompilationUnit(oldContent);
			CompilationUnit newCompilationUnit = createCompilationUnit(newContent);
			return new TypeListDelta(oldCompilationUnit.types(),
					newCompilationUnit.types());
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
		TypeListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}\n\n class OtherClass {\n}");

		assertEquals(1, delta.getAddedTypes().size());
		assertEquals("OtherClass", delta.getAddedTypes().get(0).getName()
				.getIdentifier());
		assertEquals(0, delta.getChangedTypes().size());
		assertEquals(0, delta.getRemovedTypes().size());
	}

	@Test
	public void testRemoveClass() {
		TypeListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}\n\n class OtherClass {\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"real value\";}\n}");

		assertEquals(0, delta.getAddedTypes().size());
		assertEquals(0, delta.getChangedTypes().size());
		assertEquals(1, delta.getRemovedTypes().size());
		assertEquals("OtherClass", delta.getRemovedTypes().get(0).getName()
				.getIdentifier());
	}

	@Test
	public void testModifyClass() {
		TypeListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"old value\";}\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"new value\";}\n}");

		assertEquals(0, delta.getAddedTypes().size());
		assertEquals(1, delta.getChangedTypes().size());
		assertEquals(0, delta.getRemovedTypes().size());
		TypeDelta modifiedType = delta.getChangedTypes().get(0);
		String oldName = modifiedType.getOldType().getName().getIdentifier();
		assertEquals("MainClass", oldName);
		String newName = modifiedType.getNewType().getName().getIdentifier();
		assertEquals("MainClass", newName);
	}

	@Test
	public void testModifyWhitespace() {
		TypeListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"some value\";}\n}",
				"package org.example;\n\nclass MainClass {\n\t String test() { return  \"some value\";}\n}");

		assertEquals(0, delta.getAddedTypes().size());
		assertEquals(0, delta.getChangedTypes().size());
		assertEquals(0, delta.getRemovedTypes().size());
	}

	@Test
	public void testModifyWhitespaceInString() {
		TypeListDelta delta = createDelta(
				"package org.example;\n\nclass MainClass { String test() { return \"some value\";}\n}",
				"package org.example;\n\nclass MainClass { String test() { return \"some  value\";}\n}");

		assertEquals(0, delta.getAddedTypes().size());
		assertEquals(1, delta.getChangedTypes().size());
		assertEquals(0, delta.getRemovedTypes().size());
		TypeDelta modifiedType = delta.getChangedTypes().get(0);
		String oldName = modifiedType.getOldType().getName().getIdentifier();
		assertEquals("MainClass", oldName);
		String newName = modifiedType.getNewType().getName().getIdentifier();
		assertEquals("MainClass", newName);
	}
}

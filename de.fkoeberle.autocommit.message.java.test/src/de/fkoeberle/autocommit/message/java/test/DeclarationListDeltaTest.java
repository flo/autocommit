package de.fkoeberle.autocommit.message.java.test;

import static de.fkoeberle.autocommit.message.java.test.DeclarationListUtil.createDelta;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.Test;

import de.fkoeberle.autocommit.message.java.DeclarationDelta;
import de.fkoeberle.autocommit.message.java.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.FieldDelta;
import de.fkoeberle.autocommit.message.java.TypeUtil;

public class DeclarationListDeltaTest {


	/**
	 * 
	 * @return the signature of the method as a string for test purposes only.
	 *         Can also return an string describing the error or null.
	 */
	private static String methodSignature(BodyDeclaration declaration) {
		if (declaration == null) {
			return null;
		}
		if (!(declaration instanceof MethodDeclaration)) {
			return "Error: NO METHOD, but " + declaration.getClass();
		}
		MethodDeclaration methodDeclaration = (MethodDeclaration) declaration;
		StringBuilder builder = new StringBuilder();

		TypeUtil.appendTypeTo(methodDeclaration.getReturnType2(), builder);
		builder.append(' ');
		builder.append(methodDeclaration.getName().getIdentifier());
		builder.append('(');
		boolean addComma = false;
		for (Object parameterObject : methodDeclaration.parameters()) {
			if (addComma) {
				builder.append(',');
			} else {
				addComma = true;
			}
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) parameterObject;
			Type type = parameter.getType();
			TypeUtil.appendTypeTo(type, builder);
		}
		builder.append(')');
		return builder.toString();
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
				.getName().getIdentifier());
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
		assertEquals("OtherClass", ((TypeDeclaration) removedDeclaration)
				.getName().getIdentifier());

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

	private DeclarationListDelta createClassDelta(String oldBodyContent,
			String newBodyContent) {
		String oldSource = String.format(
				"package org.example;\n\nclass MainClass {\n%s\n}",
				oldBodyContent);
		String newSource = String.format(
				"package org.example;\n\nclass MainClass {\n%s\n}",
				newBodyContent);
		DeclarationListDelta fileDelta = createDelta(oldSource, newSource);

		assertEquals(0, fileDelta.getAddedDeclarations().size());
		assertEquals(1, fileDelta.getChangedDeclarations().size());
		assertEquals(0, fileDelta.getRemovedDeclarations().size());

		DeclarationDelta modifiedType = fileDelta.getChangedDeclarations().get(
				0);

		BodyDeclaration oldDeclaration = modifiedType.getOldDeclaration();
		assertTrue(oldDeclaration instanceof TypeDeclaration);
		AbstractTypeDeclaration oldType = (AbstractTypeDeclaration) oldDeclaration;

		BodyDeclaration newDeclaration = modifiedType.getNewDeclaration();
		assertTrue(newDeclaration instanceof TypeDeclaration);
		AbstractTypeDeclaration newType = (AbstractTypeDeclaration) newDeclaration;

		DeclarationListDelta typeDelta = new DeclarationListDelta(oldType,
				newType);

		return typeDelta;
	}

	@Test
	public void testAddFirstMethod() {
		DeclarationListDelta delta = createClassDelta("",
				"String test() { return \"new value\";}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		BodyDeclaration addedBodyDeclaration = delta.getAddedDeclarations()
				.get(0);
		assertTrue(addedBodyDeclaration instanceof MethodDeclaration);
		MethodDeclaration addedMethod = (MethodDeclaration) addedBodyDeclaration;
		assertEquals("test", addedMethod.getName().getIdentifier());

	}

	@Test
	public void testAddSecondMethodAfterFirstMethod() {
		DeclarationListDelta delta = createClassDelta(
				"String firstMethod() { return \"value\";} ",
				"String firstMethod() { return \"value\";}\n String secondMethod() { return \"value\";}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String addedMethod = methodSignature(delta.getAddedDeclarations()
				.get(0));
		assertEquals("String secondMethod()", addedMethod);
	}

	@Test
	public void testAddSecondMethodBeforeFirstMethod() {
		DeclarationListDelta delta = createClassDelta(
				"String firstMethod() { return \"value\";} ",
				"String secondMethod() { return \"value\";} \n String firstMethod() { return \"value\";}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String addedMethod = methodSignature(delta.getAddedDeclarations()
				.get(0));
		assertEquals("String secondMethod()", addedMethod);
	}

	@Test
	public void testAddMethodWithSameNameAfterFirstMethod() {
		DeclarationListDelta delta = createClassDelta(
				"String someMethod(String x) { return \"value\";} ",
				"String someMethod(Number x) { return \"value\";}\n String someMethod(String x) { return \"value\";}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String addedMethod = methodSignature(delta.getAddedDeclarations()
				.get(0));
		assertEquals("String someMethod(Number)", addedMethod);
	}

	@Test
	public void testAddMethodWithSameNameBeforeFirstMethod() {
		DeclarationListDelta delta = createClassDelta(
				"String someMethod(String x) { return \"value\";}",
				"String someMethod(String x) { return \"value\";}\nString someMethod(Number x) { return \"value\";}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String addedMethod = methodSignature(delta.getAddedDeclarations()
				.get(0));
		assertEquals("String someMethod(Number)", addedMethod);
	}

	/**
	 * This test shows the handling of the special case that a methods parameter
	 * gets changed. It gets handled as a modification of a single method since
	 * methods can get overloaded: It would not make sense to handle the
	 * addition and removal of methods differently when there are overloaded
	 * methods with the same name.
	 * 
	 */
	@Test
	public void testChangeMethodParameters() {
		DeclarationListDelta delta = createClassDelta(
				"int someMethod(String x) { return 0;} ",
				"int someMethod(int x) { return 0;}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());

		String removedMethod = methodSignature(delta.getRemovedDeclarations()
				.get(0));
		assertEquals("int someMethod(String)", removedMethod);

		String addedMethod = methodSignature(delta.getAddedDeclarations()
				.get(0));
		assertEquals("int someMethod(int)", addedMethod);
	}

	@Test
	public void testModifyOnlyExistingMethod() {
		DeclarationListDelta delta = createClassDelta(
				"int someMethod(int x) { return 0;} ",
				"int someMethod(int x) { return 1;}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String changedMethod = methodSignature(delta.getChangedDeclarations()
				.get(0).getOldDeclaration());
		assertEquals("int someMethod(int)", changedMethod);
	}

	@Test
	public void testModifyOnlyExistingMethodsVisibility() {
		DeclarationListDelta delta = createClassDelta(
				"private int someMethod(int x) { return 0;} ",
				"public int someMethod(int x) { return 0;}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String changedMethod = methodSignature(delta.getChangedDeclarations()
				.get(0).getOldDeclaration());
		assertEquals("int someMethod(int)", changedMethod);
	}

	@Test
	public void testModifyOnlyExistingMethodsReturnType() {
		DeclarationListDelta delta = createClassDelta(
				"Object someMethod(int x) { return null;}",
				"String someMethod(int x) { return null;}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String oldMethod = methodSignature(delta.getChangedDeclarations()
				.get(0).getOldDeclaration());
		assertEquals("Object someMethod(int)", oldMethod);

		String newMethod = methodSignature(delta.getChangedDeclarations()
				.get(0).getNewDeclaration());
		assertEquals("String someMethod(int)", newMethod);
	}

	@Test
	public void testAddJavaDocToOnlyExistingMethod() {
		DeclarationListDelta delta = createClassDelta(
				"String someMethod(int x) { return null;}",
				"/**\n * nice method!\n */\nString someMethod(int x) { return null;}");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		String changedMethod = methodSignature(delta.getChangedDeclarations()
				.get(0).getOldDeclaration());
		assertEquals("String someMethod(int)", changedMethod);
	}

	@Test
	public void testAddRemoveAndChangeTwoMethods() {
		DeclarationListDelta delta = createClassDelta(
				"Object value;\n"
						+ "List<ChangeListener> listener = new ArrayList<ChangeListener>();\n"
						+ "void removeListener(ChangeListener l) {//TODO stub\n}\n"
						+ "void addListener(ChangeListener l) {//TODO stub\n}\n"
						+ "void setString(String value) {\nthis.value = value;\n}\n"
						+ "String getString() {\n return value.toString();\n}\n"
						+ "String toString() {\n return value.toString();\n}\n",
				"Object value;\n"
						+ "List<ChangeListener> listener = new ArrayList<ChangeListener>();\n"
						+ "void removeListener(ChangeListener l) {\nlistener.remove(l);\n}\n"
						+ "void addListener(ChangeListener l) {\nlistener.add(l);\n}\n"
						+ "void setValue(Object value) {\nthis.value = value;\n}\n"
						+ "Object getValue() {\n return value;\n}\n"
						+ "String toString() {\n return value.toString();\n}\n");

		assertEquals(2, delta.getAddedDeclarations().size());
		assertEquals(2, delta.getChangedDeclarations().size());
		assertEquals(2, delta.getRemovedDeclarations().size());

		assertEquals("void setValue(Object)", methodSignature(delta
				.getAddedDeclarations().get(0)));
		assertEquals("Object getValue()", methodSignature(delta
				.getAddedDeclarations().get(1)));
		assertEquals("void removeListener(ChangeListener)",
				methodSignature(delta
				.getChangedDeclarations().get(0).getOldDeclaration()));
		assertEquals("void addListener(ChangeListener)", methodSignature(delta
				.getChangedDeclarations().get(1).getOldDeclaration()));
		assertEquals("void setString(String)", methodSignature(delta
				.getRemovedDeclarations().get(0)));
		assertEquals("String getString()", methodSignature(delta
				.getRemovedDeclarations().get(1)));
	}

	@Test
	public void testChangedMethodToUseVarArgsInsteadOfAnArray()
			throws IOException {
		DeclarationListDelta delta = createClassDelta(
				"int m(String[] args) { return null;}",
				"int m(String.. args) {return null;}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());
	}

	@Test
	public void testChangedMethodToUseAnArrayInsteadOfVarArgs()
			throws IOException {
		DeclarationListDelta delta = createClassDelta(
				"int m(String... args) { return null;}",
				"int m(String[] args) {return null;}");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());
	}

	@Test
	public void testRenameField() {
		DeclarationListDelta delta = createClassDelta("int oldField;\n",
				"int newField;\n");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());

		BodyDeclaration addedDeclaration = delta.getAddedDeclarations().get(0);
		assertTrue(addedDeclaration instanceof FieldDeclaration);
		FieldDeclaration addedField = (FieldDeclaration) addedDeclaration;
		VariableDeclarationFragment fragmentOfAddedField = (VariableDeclarationFragment) addedField
				.fragments().get(0);
		assertEquals("newField", fragmentOfAddedField.getName().getIdentifier());

		BodyDeclaration removedDeclaration = delta.getRemovedDeclarations()
				.get(0);
		assertTrue(removedDeclaration instanceof FieldDeclaration);
		FieldDeclaration removedField = (FieldDeclaration) removedDeclaration;
		VariableDeclarationFragment fragmentOfRemovedField = (VariableDeclarationFragment) removedField
				.fragments().get(0);
		assertEquals("oldField", fragmentOfRemovedField.getName()
				.getIdentifier());
	}

	@Test
	public void testChangedField() {
		DeclarationListDelta delta = createClassDelta("int field;\n",
				"String field;\n");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		DeclarationDelta declaration = delta.getChangedDeclarations().get(0);
		assertTrue(declaration instanceof FieldDelta);
	}
	// TODO test class, enum, interface and annotation body declaration changes
}

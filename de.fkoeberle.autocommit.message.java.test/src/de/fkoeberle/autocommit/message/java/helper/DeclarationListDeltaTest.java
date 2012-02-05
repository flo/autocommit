/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import static de.fkoeberle.autocommit.message.java.helper.DeclarationListUtil.createDelta;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.EnumSet;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.Test;

import de.fkoeberle.autocommit.message.java.helper.TypeUtil;
import de.fkoeberle.autocommit.message.java.helper.delta.AnnotationTypeMemberDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.helper.delta.DeclarationDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.EnumConstantDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.FieldDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

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

		DeclarationDelta<?> modifiedType = delta.getChangedDeclarations()
				.get(0);

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

		DeclarationDelta<?> modifiedType = delta.getChangedDeclarations()
				.get(0);

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

		DeclarationDelta<?> modifiedType = fileDelta.getChangedDeclarations()
				.get(
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
				methodSignature(delta.getChangedDeclarations().get(0)
						.getOldDeclaration()));
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

		DeclarationDelta<?> declaration = delta.getChangedDeclarations().get(0);
		assertTrue(declaration instanceof FieldDelta);
	}

	@Test
	public void testReplacedInitializer() {
		DeclarationListDelta delta = createClassDelta(
				"static int i; { i = 0; }\n", "static int i; { i = 1; i++;}\n");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());

		BodyDeclaration removedDeclaration = delta.getRemovedDeclarations()
				.get(0);
		assertTrue(removedDeclaration instanceof Initializer);
		Initializer removedInitializer = (Initializer) removedDeclaration;
		assertEquals(1, removedInitializer.getBody().statements().size());

		BodyDeclaration addedDeclaration = delta.getAddedDeclarations().get(0);
		assertTrue(addedDeclaration instanceof Initializer);
		Initializer addedInitializer = (Initializer) addedDeclaration;
		assertEquals(2, addedInitializer.getBody().statements().size());
	}

	@Test
	public void testAddedInnerClass() {
		DeclarationListDelta delta = createClassDelta("\n", "class Hello {}\n");

		assertEquals(1, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		BodyDeclaration addedDeclaration = delta.getAddedDeclarations()
				.get(0);
		assertTrue(addedDeclaration instanceof TypeDeclaration);
		TypeDeclaration addedType = (TypeDeclaration) addedDeclaration;
		assertEquals("Hello", addedType.getName().getIdentifier());
	}

	@Test
	public void testRemovedInnerClass() {
		DeclarationListDelta delta = createClassDelta("class Hello {}\n", "\n");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(0, delta.getChangedDeclarations().size());
		assertEquals(1, delta.getRemovedDeclarations().size());

		BodyDeclaration removedDeclaration = delta.getRemovedDeclarations()
				.get(0);
		assertTrue(removedDeclaration instanceof TypeDeclaration);
		TypeDeclaration removedType = (TypeDeclaration) removedDeclaration;
		assertEquals("Hello", removedType.getName().getIdentifier());
	}

	@Test
	public void testChangedSuperClassOfInnerClass() {
		DeclarationListDelta delta = createClassDelta(
				"class Hello extends OldClass {}\n",
				"class Hello extends NewClass {}\n");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		DeclarationDelta<?> declarationDelta = delta.getChangedDeclarations()
				.get(
				0);
		assertTrue(declarationDelta instanceof TypeDelta);
		TypeDelta typeDelta = (TypeDelta) declarationDelta;
		assertEquals(EnumSet.of(BodyDeclarationChangeType.SUPER_CLASS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testChangedImplementedInterfaceOfInnerClass() {
		DeclarationListDelta delta = createClassDelta(
				"class Hello implements IOldClass {}\n",
				"class Hello implements INewClass {}\n");

		assertEquals(0, delta.getAddedDeclarations().size());
		assertEquals(1, delta.getChangedDeclarations().size());
		assertEquals(0, delta.getRemovedDeclarations().size());

		DeclarationDelta<?> declarationDelta = delta.getChangedDeclarations()
				.get(
				0);
		assertTrue(declarationDelta instanceof TypeDelta);
		TypeDelta typeDelta = (TypeDelta) declarationDelta;
		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.SUPER_INTERFACE_LIST),
				typeDelta.getChangeTypes());
	}

	private DeclarationListDelta createAnnotationDelta(String oldBodyContent,
			String newBodyContent) {
		String oldSource = String.format(
				"package org.example;\n\n@interface MyAnnotation {\n%s\n}",
				oldBodyContent);
		String newSource = String.format(
				"package org.example;\n\n@interface MyAnnotation {\n%s\n}",
				newBodyContent);
		DeclarationListDelta fileDelta = createDelta(oldSource, newSource);

		assertEquals(0, fileDelta.getAddedDeclarations().size());
		assertEquals(1, fileDelta.getChangedDeclarations().size());
		assertEquals(0, fileDelta.getRemovedDeclarations().size());

		DeclarationDelta<?> declarationDelta = fileDelta
				.getChangedDeclarations()
				.get(0);

		TypeDelta typeDelta = (TypeDelta) declarationDelta;
		return typeDelta.getDeclarationListDelta();
	}

	@Test
	public void testChangedAnnotationMemberDefaultAndJavaDoc() {
		DeclarationListDelta listDelta = createAnnotationDelta(
				"String name();",
				"/** name for annotated object */ String name() default \"no name\";");

		assertEquals(0, listDelta.getAddedDeclarations().size());
		assertEquals(1, listDelta.getChangedDeclarations().size());
		assertEquals(0, listDelta.getRemovedDeclarations().size());
		DeclarationDelta<?> declarationDelta = listDelta
				.getChangedDeclarations()
				.get(0);
		assertTrue(declarationDelta instanceof AnnotationTypeMemberDelta);

		AnnotationTypeMemberDelta annotationTypeMemberDelta = (AnnotationTypeMemberDelta) declarationDelta;
		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC,
				BodyDeclarationChangeType.ANNOTATION_MEMBER_DEFAULT),
				annotationTypeMemberDelta.getChangeTypes());
	}

	@Test
	public void testChangedAnnotationMemberType() {
		DeclarationListDelta listDelta = createAnnotationDelta("int id();",
				"long id();");

		assertEquals(0, listDelta.getAddedDeclarations().size());
		assertEquals(1, listDelta.getChangedDeclarations().size());
		assertEquals(0, listDelta.getRemovedDeclarations().size());
		DeclarationDelta<?> declarationDelta = listDelta
				.getChangedDeclarations()
				.get(0);
		assertTrue(declarationDelta instanceof AnnotationTypeMemberDelta);

		AnnotationTypeMemberDelta annotationTypeMemberDelta = (AnnotationTypeMemberDelta) declarationDelta;
		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.ANNOTATION_MEMBER_TYPE),
				annotationTypeMemberDelta.getChangeTypes());
	}

	@Test
	public void testRenamedAnnotationMember() {
		DeclarationListDelta listDelta = createAnnotationDelta("String id();",
				"String name();");

		assertEquals(1, listDelta.getAddedDeclarations().size());
		assertEquals(0, listDelta.getChangedDeclarations().size());
		assertEquals(1, listDelta.getRemovedDeclarations().size());

		BodyDeclaration removedDeclaration = listDelta.getRemovedDeclarations()
				.get(0);
		assertTrue(removedDeclaration instanceof AnnotationTypeMemberDeclaration);
		AnnotationTypeMemberDeclaration removedAnnotationTypeMember = (AnnotationTypeMemberDeclaration) removedDeclaration;
		assertEquals("id", removedAnnotationTypeMember.getName()
				.getIdentifier());

		BodyDeclaration addedDeclaration = listDelta.getAddedDeclarations()
				.get(0);
		assertTrue(addedDeclaration instanceof AnnotationTypeMemberDeclaration);
		AnnotationTypeMemberDeclaration addedAnnotationTypeMember = (AnnotationTypeMemberDeclaration) addedDeclaration;
		assertEquals("name", addedAnnotationTypeMember.getName()
				.getIdentifier());
	}

	private TypeDelta createEnumDelta(String oldBodyContent,
			String newBodyContent) {
		String oldSource = String.format(
				"package org.example;\n\nenum MyEnum {\n%s\n}", oldBodyContent);
		String newSource = String.format(
				"package org.example;\n\nenum MyEnum {\n%s\n}", newBodyContent);
		DeclarationListDelta fileDelta = createDelta(oldSource, newSource);

		assertEquals(0, fileDelta.getAddedDeclarations().size());
		assertEquals(1, fileDelta.getChangedDeclarations().size());
		assertEquals(0, fileDelta.getRemovedDeclarations().size());

		DeclarationDelta<?> declarationDelta = fileDelta
				.getChangedDeclarations()
				.get(0);

		TypeDelta typeDelta = (TypeDelta) declarationDelta;
		return typeDelta;
	}

	@Test
	public void testChangedEnumConstantJavaDoc() {
		TypeDelta typeDelta = createEnumDelta("ONE, TWO, THREE;",
				"ONE, /** is default */ TWO, THREE;");

		DeclarationListDelta bodyDeclarations = typeDelta
				.getDeclarationListDelta();
		assertEquals(0, bodyDeclarations.getAddedDeclarations().size());
		assertEquals(0, bodyDeclarations.getChangedDeclarations().size());
		assertEquals(0, bodyDeclarations.getRemovedDeclarations().size());

		DeclarationListDelta enumConstants = typeDelta.getEnumConstantsDelta();
		assertEquals(0, enumConstants.getAddedDeclarations().size());
		assertEquals(1, enumConstants.getChangedDeclarations().size());
		assertEquals(0, enumConstants.getRemovedDeclarations().size());

		DeclarationDelta<?> declarationDelta = enumConstants
				.getChangedDeclarations()
				.get(0);
		assertTrue(declarationDelta instanceof EnumConstantDelta);
		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				declarationDelta.getChangeTypes());
	}

	@Test
	public void testGetEnumConstantsDeltWithChangedEnumName() {
		TypeDelta typeDelta = createEnumDelta("ONE;", "TWO;");

		DeclarationListDelta bodyDeclarations = typeDelta
				.getDeclarationListDelta();
		assertEquals(0, bodyDeclarations.getAddedDeclarations().size());
		assertEquals(0, bodyDeclarations.getChangedDeclarations().size());
		assertEquals(0, bodyDeclarations.getRemovedDeclarations().size());

		DeclarationListDelta enumConstants = typeDelta.getEnumConstantsDelta();
		assertEquals(1, enumConstants.getAddedDeclarations().size());
		assertEquals(0, enumConstants.getChangedDeclarations().size());
		assertEquals(1, enumConstants.getRemovedDeclarations().size());

		BodyDeclaration removedDeclaration = enumConstants
				.getRemovedDeclarations()
				.get(0);
		assertTrue(removedDeclaration instanceof EnumConstantDeclaration);
		EnumConstantDeclaration removedEnumConstantDeclaration = (EnumConstantDeclaration) removedDeclaration;
		assertEquals("ONE", removedEnumConstantDeclaration.getName()
				.getIdentifier());

		BodyDeclaration addedDeclaration = enumConstants.getAddedDeclarations()
				.get(0);
		assertTrue(addedDeclaration instanceof EnumConstantDeclaration);
		EnumConstantDeclaration addedEnumConstantDeclaration = (EnumConstantDeclaration) addedDeclaration;
		assertEquals("TWO", addedEnumConstantDeclaration.getName()
				.getIdentifier());
	}
}

/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;

import de.fkoeberle.autocommit.message.FileContent;
import de.fkoeberle.autocommit.message.Session;

public class TypeUtilTest {

	private MethodDeclaration createMethodWithParameters(String paramStr)
			throws IOException {
		FileContent fileContent = new FileContent(String.format(
				"class OuterClass {\nclass InnerClass{}\nvoid m(%s) {}\n}",
				paramStr));
		Session session = new Session();
		CachingJavaFileContentParser parser = session
				.getInstanceOf(CachingJavaFileContentParser.class);
		CompilationUnit compilationUnit = parser.getInstanceFor(fileContent);
		AbstractTypeDeclaration type = (AbstractTypeDeclaration) (compilationUnit
				.types().get(0));
		MethodDeclaration methodDeclaration = (MethodDeclaration) (type
				.bodyDeclarations().get(1));
		return methodDeclaration;
	}

	@Test
	public void testNoArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("");
		assertEquals("", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testPrimitiveTypeArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("int i");
		assertEquals("int", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testClassNameArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("Hello h");
		assertEquals("Hello", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testListOfStringArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("List  <  String  >   list");
		assertEquals("List<String>", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testListOfWildcardArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("List  <  ?  >   list");
		assertEquals("List<?>", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testListOfSuperInteger() throws IOException {
		MethodDeclaration method = createMethodWithParameters("List  <  ? super Integer >   list");
		assertEquals("List<? super Integer>", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testListOfExtendsNumber() throws IOException {
		MethodDeclaration method = createMethodWithParameters("List  <  ? extends Number >   list");
		assertEquals("List<? extends Number>",
				TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testStringToSetMapArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("Map<String, Set< Integer >>   list");
		assertEquals("Map<String, Set<Integer>>",
				TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testTrippleGenericArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("Triple<One, Two, Three>   list");
		assertEquals("Triple<One, Two, Three>",
				TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testTwoArguments() throws IOException {
		MethodDeclaration method = createMethodWithParameters("int i, Two t");
		assertEquals("int, Two", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testArrayArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("int[] i");
		assertEquals("int[]", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testVarArgArrayArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("int... i");
		assertEquals("int...", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testTwoArgsOneIsVarargs() throws IOException {
		MethodDeclaration method = createMethodWithParameters("String s, int... i");
		assertEquals("String, int...", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testTwoDimensionalArgument() throws IOException {
		MethodDeclaration method = createMethodWithParameters("int[][] i");
		assertEquals("int[][]", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testTypeWithPackagePrefix() throws IOException {
		MethodDeclaration method = createMethodWithParameters("java.lang.String s");
		assertEquals("java.lang.String", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testQualifiedName() throws IOException {
		MethodDeclaration method = createMethodWithParameters("OuterClass.InnerClass inner");
		assertEquals("OuterClass.InnerClass", TypeUtil.parameterTypesOf(method));
	}

	@Test
	public void testTypeNames() throws IOException {
		FileContent fileContent = new FileContent(
				"class Outer { class Middle { class Inner {}}}");
		Session session = new Session();
		CachingJavaFileContentParser parser = session
				.getInstanceOf(CachingJavaFileContentParser.class);
		CompilationUnit compilationUnit = parser.getInstanceFor(fileContent);
		AbstractTypeDeclaration outerType = (AbstractTypeDeclaration) (compilationUnit
				.types().get(0));
		AbstractTypeDeclaration middleType = (AbstractTypeDeclaration) (outerType
				.bodyDeclarations().get(0));
		AbstractTypeDeclaration innerType = (AbstractTypeDeclaration) (middleType
				.bodyDeclarations().get(0));

		assertEquals("Outer", TypeUtil.nameOf(outerType));
		assertEquals("Middle", TypeUtil.nameOf(middleType));
		assertEquals("Inner", TypeUtil.nameOf(innerType));
		assertEquals("Outer", TypeUtil.fullTypeNameOf(outerType));
		assertEquals("Outer.Middle", TypeUtil.fullTypeNameOf(middleType));
		assertEquals("Outer.Middle.Inner", TypeUtil.fullTypeNameOf(innerType));
		assertEquals(null, TypeUtil.outerTypeNameOf(outerType));
		assertEquals("Outer", TypeUtil.outerTypeNameOf(middleType));
		assertEquals("Outer.Middle", TypeUtil.outerTypeNameOf(innerType));

	}
}

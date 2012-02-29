/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper.delta;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.java.helper.ASTCompareUtil;
import de.fkoeberle.autocommit.message.java.helper.TypeUtil;

/**
 * This class represents the result of comparison between an old and new version
 * of a {@link AbstractTypeDeclaration}.
 * 
 * 
 * This class can describe changes where the type of the declared type changes.
 * For example an enum that gets converted into an class. In such a case the
 * method {@link #getChangeTypes()} will return a set which contains
 * {@link BodyDeclarationChangeType#TYPE_OF_TYPE}.
 * 
 * When the declared type extends from another type in the new version then the
 * set returned by the method {@link #getChangeTypes()} will contain an instance
 * of {@link BodyDeclarationChangeType#SUPER_CLASS}.
 * 
 * When the implemented interface list got changed then the set returned by the
 * method {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#PACKAGE_NAME}.
 * 
 * When the list of declared enum constants got changed then the set returned by
 * the method {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#ENUM_CONSTANTS}. The method
 * {@link #getEnumConstantsDelta()} can then be used to obtain details about
 * that change.
 * 
 * When the declaration list got changed then the set returned by the method
 * {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#DECLARATION_LIST}.
 * 
 */
public final class TypeDelta extends DeclarationDelta<AbstractTypeDeclaration> {
	private DeclarationListDelta declarationListDelta;
	private final EnumSet<BodyDeclarationChangeType> declarationListChange = EnumSet
			.of(BodyDeclarationChangeType.DECLARATION_LIST);
	private DeclarationListDelta enumConstantsDelta;

	TypeDelta(AbstractTypeDeclaration oldType, AbstractTypeDeclaration newType) {
		super(oldType, newType);
	}

	/**
	 * 
	 * @return never null.
	 */
	public DeclarationListDelta getDeclarationListDelta() {
		if (declarationListDelta == null) {
			declarationListDelta = new DeclarationListDelta(oldDeclaration,
					newDeclaration);
		}
		return declarationListDelta;
	}

	/**
	 * 
	 * @return never null.
	 * @throws RuntimeException
	 *             if it's not an enum delta.
	 */
	public DeclarationListDelta getEnumConstantsDelta() throws RuntimeException {
		if (enumConstantsDelta == null) {
			// cast is allowed to result in a RuntimeException:
			EnumDeclaration oldEnum = (EnumDeclaration) oldDeclaration;
			EnumDeclaration newEnum = (EnumDeclaration) newDeclaration;
			enumConstantsDelta = new DeclarationListDelta(
					oldEnum.enumConstants(), newEnum.enumConstants());
		}
		return enumConstantsDelta;
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = EnumSet
				.noneOf(BodyDeclarationChangeType.class);
		if (isTypeOfTypeChange()) {
			result.add(BodyDeclarationChangeType.TYPE_OF_TYPE);
		}
		if (oldDeclaration instanceof TypeDeclaration) {
			assert newDeclaration instanceof TypeDeclaration : "must be true since isTypeOfTypeChange() was false";
			TypeDeclaration oldTypeDeclaration = ((TypeDeclaration) oldDeclaration);
			TypeDeclaration newTypeDeclaration = ((TypeDeclaration) newDeclaration);
			if (isSuperClassChange(oldTypeDeclaration, newTypeDeclaration)) {
				result.add(BodyDeclarationChangeType.SUPER_CLASS);
			}
			if (isSuperInterfaceListChange(oldTypeDeclaration,
					newTypeDeclaration)) {
				result.add(BodyDeclarationChangeType.SUPER_INTERFACE_LIST);
			}
		} else if (oldDeclaration instanceof EnumDeclaration) {
			assert newDeclaration instanceof EnumDeclaration : "must be true since isTypeOfTypeChange() was false";
			EnumDeclaration oldEnum = (EnumDeclaration) oldDeclaration;
			EnumDeclaration newEnum = (EnumDeclaration) newDeclaration;
			if (isSuperInterfaceListChange(oldEnum, newEnum)) {
				result.add(BodyDeclarationChangeType.SUPER_INTERFACE_LIST);
			}
			if (isConstantsChange(oldEnum, newEnum)) {
				result.add(BodyDeclarationChangeType.ENUM_CONSTANTS);
			}
		}
		if (containsDeclarationListChange()) {
			result.add(BodyDeclarationChangeType.DECLARATION_LIST);
		}
		return result;
	}

	private static boolean isConstantsChange(EnumDeclaration oldEnum,
			EnumDeclaration newEnum) {
		List<?> oldDeclarations = oldEnum.enumConstants();
		List<?> newDeclarations = newEnum.enumConstants();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldDeclarations,
				newDeclarations);
	}

	private boolean containsDeclarationListChange() {
		List<?> oldDeclarations = oldDeclaration.bodyDeclarations();
		List<?> newDeclarations = newDeclaration.bodyDeclarations();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldDeclarations,
				newDeclarations);
	}

	private boolean isTypeOfTypeChange() {
		if (oldDeclaration.getClass().equals(newDeclaration.getClass())) {
			if (oldDeclaration instanceof TypeDeclaration) {
				assert (newDeclaration instanceof TypeDeclaration) : "classes are the same";
				TypeDeclaration oldClassOrInterface = (TypeDeclaration) oldDeclaration;
				TypeDeclaration newClassOrInterface = (TypeDeclaration) newDeclaration;
				if (oldClassOrInterface.isInterface() != newClassOrInterface
						.isInterface()) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	private static boolean isSuperClassChange(
			TypeDeclaration oldTypeDeclaration,
			TypeDeclaration newTypeDeclaration) {
		Type oldSuperClass = oldTypeDeclaration.getSuperclassType();
		Type newSuperClass = newTypeDeclaration.getSuperclassType();
		return ASTCompareUtil.astNodesDiffer(oldSuperClass, newSuperClass);
	}

	private static boolean isSuperInterfaceListChange(
			TypeDeclaration oldTypeDeclaration,
			TypeDeclaration newTypeDeclaration) {
		List<?> oldInterfaces = oldTypeDeclaration.superInterfaceTypes();
		List<?> newInterfaces = newTypeDeclaration.superInterfaceTypes();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldInterfaces,
				newInterfaces);
	}

	private static boolean isSuperInterfaceListChange(
			EnumDeclaration oldTypeDeclaration,
			EnumDeclaration newTypeDeclaration) {
		List<?> oldInterfaces = oldTypeDeclaration.superInterfaceTypes();
		List<?> newInterfaces = newTypeDeclaration.superInterfaceTypes();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldInterfaces,
				newInterfaces);
	}

	public String getSimpleTypeName() {
		return TypeUtil.nameOf(oldDeclaration);
	}

	public String getOuterTypeName() {
		return TypeUtil.outerTypeNameOf(oldDeclaration);
	}

	public String getFullTypeName() {
		return TypeUtil.fullTypeNameOf(oldDeclaration);
	}

	public boolean isDeclarationListOnlyChange() {
		return getChangeTypes().equals(declarationListChange);
	}

	public TypeDeltaType getType() {
		if (oldDeclaration instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) oldDeclaration;
			if (typeDeclaration.isInterface()) {
				return TypeDeltaType.INTERFACE;
			} else {
				return TypeDeltaType.CLASS;
			}
		} else if (oldDeclaration instanceof EnumDeclaration) {
			return TypeDeltaType.ENUM;
		} else if (oldDeclaration instanceof AnnotationTypeDeclaration) {
			return TypeDeltaType.ANNOTATION;
		} else {
			return null;
		}
	}
}

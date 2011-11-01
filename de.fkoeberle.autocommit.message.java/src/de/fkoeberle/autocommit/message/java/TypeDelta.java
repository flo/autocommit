package de.fkoeberle.autocommit.message.java;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public final class TypeDelta extends DeclarationDelta {
	private final AbstractTypeDeclaration oldType;
	private final AbstractTypeDeclaration newType;
	private DeclarationListDelta declarationListDelta;
	private final EnumSet<BodyDeclarationChangeType> declarationListChange = EnumSet
			.of(BodyDeclarationChangeType.DECLARATION_LIST);
	private DeclarationListDelta enumConstantsDelta;

	TypeDelta(AbstractTypeDeclaration oldType,
			AbstractTypeDeclaration newType) {
		super(oldType, newType);
		this.oldType = oldType;
		this.newType = newType;
	}

	public AbstractTypeDeclaration getOldType() {
		return oldType;
	}

	public AbstractTypeDeclaration getNewType() {
		return newType;
	}

	/**
	 * 
	 * @return never null.
	 */
	public DeclarationListDelta getDeclarationListDelta() {
		if (declarationListDelta == null) {
			declarationListDelta = new DeclarationListDelta(oldType, newType);
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
			EnumDeclaration oldEnum = (EnumDeclaration) oldType;
			EnumDeclaration newEnum = (EnumDeclaration) newType;
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
		if (oldType instanceof TypeDeclaration) {
			assert newType instanceof TypeDeclaration : "must be true since isTypeOfTypeChange() was false";
			TypeDeclaration oldTypeDeclaration = ((TypeDeclaration) oldType);
			TypeDeclaration newTypeDeclaration = ((TypeDeclaration) newType);
			if (isSuperClassChange(oldTypeDeclaration, newTypeDeclaration)) {
				result.add(BodyDeclarationChangeType.SUPER_CLASS);
			}
			if (isSuperInterfaceListChange(oldTypeDeclaration,
					newTypeDeclaration)) {
				result.add(BodyDeclarationChangeType.SUPER_INTERFACE_LIST);
			}
		} else if (oldType instanceof EnumDeclaration) {
			assert newType instanceof EnumDeclaration : "must be true since isTypeOfTypeChange() was false";
			EnumDeclaration oldEnum = (EnumDeclaration) oldType;
			EnumDeclaration newEnum = (EnumDeclaration) newType;
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
		return listsOfASTNodesDiffer(oldDeclarations, newDeclarations);
	}

	private boolean containsDeclarationListChange() {
		List<?> oldDeclarations = oldType.bodyDeclarations();
		List<?> newDeclarations = newType.bodyDeclarations();
		return listsOfASTNodesDiffer(oldDeclarations, newDeclarations);
	}

	private boolean isTypeOfTypeChange() {
		if (oldType.getClass().equals(newType.getClass())) {
			if (oldType instanceof TypeDeclaration) {
				assert (newType instanceof TypeDeclaration) : "classes are the same";
				TypeDeclaration oldClassOrInterface = (TypeDeclaration) oldType;
				TypeDeclaration newClassOrInterface = (TypeDeclaration) newType;
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
		return astNodesDiffer(oldSuperClass, newSuperClass);
	}

	private static boolean isSuperInterfaceListChange(
			TypeDeclaration oldTypeDeclaration,
			TypeDeclaration newTypeDeclaration) {
		List<?> oldInterfaces = oldTypeDeclaration.superInterfaceTypes();
		List<?> newInterfaces = newTypeDeclaration.superInterfaceTypes();
		return listsOfASTNodesDiffer(oldInterfaces, newInterfaces);
	}

	private static boolean isSuperInterfaceListChange(
			EnumDeclaration oldTypeDeclaration,
			EnumDeclaration newTypeDeclaration) {
		List<?> oldInterfaces = oldTypeDeclaration.superInterfaceTypes();
		List<?> newInterfaces = newTypeDeclaration.superInterfaceTypes();
		return listsOfASTNodesDiffer(oldInterfaces, newInterfaces);
	}
	
	public String getSimpleTypeName() {
		return TypeUtil.nameOf(oldType);
	}

	public String getOuterTypeName() {
		return TypeUtil.outerTypeNameOf(oldType);
	}

	public String getFullTypeName() {
		return TypeUtil.fullTypeNameOf(oldType);
	}

	public boolean isDeclarationListOnlyChange() {
		return getChangeTypes().equals(declarationListChange);
	}



}

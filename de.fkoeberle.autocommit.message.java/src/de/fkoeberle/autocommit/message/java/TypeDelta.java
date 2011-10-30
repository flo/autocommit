package de.fkoeberle.autocommit.message.java;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public final class TypeDelta extends DeclarationDelta {
	private final AbstractTypeDeclaration oldType;
	private final AbstractTypeDeclaration newType;
	private DeclarationListDelta declarationListDelta;
	private Boolean typeOfTypeChange;
	private Boolean declarationListOnlyChange;

	public TypeDelta(AbstractTypeDeclaration oldType,
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

	public static TypeDelta valueOf(DeclarationDelta declarationDelta) {
		BodyDeclaration oldDeclaration = declarationDelta.getOldDeclaration();
		BodyDeclaration newDeclaration = declarationDelta.getNewDeclaration();
		if (!(oldDeclaration instanceof AbstractTypeDeclaration)) {
			return null;
		}
		if (!(newDeclaration instanceof AbstractTypeDeclaration)) {
			return null;
		}
		AbstractTypeDeclaration oldType = (AbstractTypeDeclaration) oldDeclaration;
		AbstractTypeDeclaration newType = (AbstractTypeDeclaration) newDeclaration;

		return new TypeDelta(oldType, newType);
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

	public boolean isTypeOfTypeChange() {
		if (typeOfTypeChange == null) {
			typeOfTypeChange = determineValueOfTypeOfTypeChange();
		}
		return typeOfTypeChange.booleanValue();
	}

	private Boolean determineValueOfTypeOfTypeChange() {
		if (oldType.getClass().equals(newType.getClass())) {
			if (oldType instanceof TypeDeclaration) {
				assert (newType instanceof TypeDeclaration) : "classes are the same";
				TypeDeclaration oldClassOrInterface = (TypeDeclaration) oldType;
				TypeDeclaration newClassOrInterface = (TypeDeclaration) newType;
				if (oldClassOrInterface.isInterface() != newClassOrInterface
						.isInterface()) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	public boolean isDeclarationListOnlyChange() {
		if (declarationListOnlyChange == null) {
			declarationListOnlyChange = determineValueOfDeclarationListOnlyChange();
		}
		return declarationListOnlyChange.booleanValue();
	}

	private Boolean determineValueOfDeclarationListOnlyChange() {
		if (isTypeOfTypeChange()) {
			return Boolean.FALSE;
		}
		if (containsJavaDocChanges()) {
			return Boolean.FALSE;
		}
		if (containsModifierChanges()) {
			return Boolean.FALSE;
		}

		if (oldType instanceof TypeDeclaration) {
			assert newType instanceof TypeDeclaration : "must be true since isTypeOfTypeChange() was false";
			TypeDeclaration oldTypeDeclaration = ((TypeDeclaration) oldType);
			TypeDeclaration newTypeDeclaration = ((TypeDeclaration) newType);
			if (isSuperClassChange(oldTypeDeclaration, newTypeDeclaration)) {
				return Boolean.FALSE;
			}
			if (isSuperInterfaceListChange(oldTypeDeclaration,
					newTypeDeclaration)) {
				return Boolean.FALSE;
			}
		} else if (oldType instanceof EnumDeclaration) {
			assert newType instanceof EnumDeclaration : "must be true since isTypeOfTypeChange() was false";
			EnumDeclaration oldEnum = (EnumDeclaration) oldType;
			EnumDeclaration newEnum = (EnumDeclaration) newType;
			if (isSuperInterfaceListChange(oldEnum, newEnum)) {
				return Boolean.FALSE;
			}
		}
		// There isn't a check for AnnotationTypeDeclaration since
		// they don't have anything additional to compare

		return Boolean.TRUE;
	}

	private static boolean isSuperClassChange(
			TypeDeclaration oldTypeDeclaration,
			TypeDeclaration newTypeDeclaration) {
		Type oldSuperClass = oldTypeDeclaration.getSuperclassType();
		Type newSuperClass = newTypeDeclaration.getSuperclassType();
		if ((oldSuperClass == null) != (newSuperClass == null)) {
			return true;
		}
		if (oldSuperClass != null) {
			assert newSuperClass != null;
			boolean matches = oldSuperClass.subtreeMatch(new ASTMatcher(true),
					newSuperClass);
			if (!matches) {
				return true;
			}
		}
		return false;
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



}

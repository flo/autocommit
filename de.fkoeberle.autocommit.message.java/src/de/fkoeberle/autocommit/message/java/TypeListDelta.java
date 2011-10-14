package de.fkoeberle.autocommit.message.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class TypeListDelta {
	List<AbstractTypeDeclaration> addedTypes;
	List<AbstractTypeDeclaration> removedTypes;
	List<TypeDelta> changedTypes;

	public TypeListDelta(CompilationUnit oldUnit, CompilationUnit newUnit) {
		this(oldUnit.types(), newUnit.types());
	}

	public TypeListDelta(List<?> oldTypes, List<?> newTypes) {
		Map<String, AbstractTypeDeclaration> nameToOldName = new HashMap<String, AbstractTypeDeclaration>(
				oldTypes.size());
		this.addedTypes = new ArrayList<AbstractTypeDeclaration>();
		this.changedTypes = new ArrayList<TypeDelta>();
		for (Object typeObject : oldTypes) {
			AbstractTypeDeclaration type = (AbstractTypeDeclaration) typeObject;
			String name = type.getName().getIdentifier();
			nameToOldName.put(name, type);
		}

		for (Object typeObject : newTypes) {
			AbstractTypeDeclaration newType = (AbstractTypeDeclaration) typeObject;
			String name = newType.getName().getIdentifier();
			AbstractTypeDeclaration oldType = nameToOldName.remove(name);
			if (oldType == null) {
				addedTypes.add(newType);
			} else {
				if (!oldType.subtreeMatch(new ASTMatcher(true), newType)) {
					changedTypes.add(new TypeDelta(oldType, newType));
				}
			}
		}
		this.removedTypes = new ArrayList<AbstractTypeDeclaration>(
				nameToOldName.values());
	}

	public List<AbstractTypeDeclaration> getAddedTypes() {
		return addedTypes;
	}

	public List<TypeDelta> getChangedTypes() {
		return changedTypes;
	}

	public List<AbstractTypeDeclaration> getRemovedTypes() {
		return removedTypes;
	}
}

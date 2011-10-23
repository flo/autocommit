package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;

import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedTypeView {
	private boolean invalid;
	private SoftReference<TypeDelta> typeDeltaRef;

	@InjectedBySession
	private SingleChangedJavaFileView view;

	private TypeDelta determineTypeDelta() throws IOException {
		DeclarationListDelta declarationListDelta = view
				.getDeclarationListDelta();
		if (declarationListDelta == null) {
			return null;
		}
		return findTypeDeltaAtAnyDepth(view.getDeclarationListDelta());
	}

	private TypeDelta findTypeDeltaAtAnyDepth(
			DeclarationListDelta declationListDelta) {
		if (declationListDelta.getAddedDeclarations().size() != 0) {
			return null;
		}
		if (declationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declationListDelta.getChangedDeclarations().size() != 1) {
			return null;
		}
		DeclarationDelta declarationDelta = declationListDelta
				.getChangedDeclarations().get(0);
		TypeDelta typeDelta = TypeDelta.valueOf(declarationDelta);
		if (typeDelta == null) {
			return null;
		}
		if (typeDelta.isDeclarationListOnlyChange()) {
			DeclarationListDelta subDeclarationList = typeDelta
					.getDeclarationListDelta();
			TypeDelta subTypeDelta = findTypeDeltaAtAnyDepth(subDeclarationList);
			if (subTypeDelta != null) {
				return subTypeDelta;
			}
		}

		return typeDelta;
	}

	public TypeDelta getTypeDelta() throws IOException {
		if (invalid) {
			return null;
		}
		TypeDelta typeDelta = null;
		if (typeDeltaRef != null) {
			typeDelta = typeDeltaRef.get();
		}
		if (typeDelta == null) {
			typeDelta = determineTypeDelta();
		}
		if (typeDelta == null) {
			invalid = true;
			return null;
		} else {
			typeDeltaRef = new SoftReference<TypeDelta>(typeDelta);
			return typeDelta;
		}
	}
}

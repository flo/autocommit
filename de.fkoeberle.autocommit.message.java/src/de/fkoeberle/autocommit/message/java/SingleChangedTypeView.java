package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedTypeView extends AbstractViewWithCache<TypeDelta> {

	@InjectedBySession
	private SingleChangedJavaFileView view;

	@Override
	protected TypeDelta determineCachableValue() throws IOException {
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
		return getCachableValue();
	}

}

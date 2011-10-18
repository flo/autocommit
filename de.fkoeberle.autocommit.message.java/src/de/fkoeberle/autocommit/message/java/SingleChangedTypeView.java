package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;

import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedTypeView {
	private boolean invalid;
	private SoftReference<DeclarationDelta> typeDeltaRef;

	@InjectedBySession
	private SingleChangedJavaFileView view;

	private DeclarationDelta determineTypeDelta() throws IOException {
		if (!view.isValid()) {
			return null;
		}

		DeclarationListDelta declationListDelta = view
				.getDeclarationListDelta();

		if (declationListDelta.getAddedDeclarations().size() != 0) {
			return null;
		}
		if (declationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declationListDelta.getChangedDeclarations().size() != 1) {
			return null;
		}

		return declationListDelta.getChangedDeclarations().get(0);
	}

	public DeclarationDelta getDeclarationDelta() throws IOException {
		if (invalid) {
			return null;
		}
		DeclarationDelta typeDelta = null;
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
			typeDeltaRef = new SoftReference<DeclarationDelta>(typeDelta);
			return typeDelta;
		}
	}
}

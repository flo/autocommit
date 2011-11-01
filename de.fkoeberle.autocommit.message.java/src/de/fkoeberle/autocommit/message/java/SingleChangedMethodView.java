package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedMethodView extends AbstractViewWithCache<MethodDelta> {

	@InjectedBySession
	private SingleChangedTypeView singleChangedTypeView;

	@Override
	protected MethodDelta determineCachableValue() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		if (!typeDelta.isDeclarationListOnlyChange()) {
			return null;
		}
		DeclarationListDelta declarationListDelta = typeDelta
				.getDeclarationListDelta();
		if (declarationListDelta.getAddedDeclarations().size() != 0) {
			return null;
		}
		if (declarationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declarationListDelta.getChangedDeclarations().size() != 1) {
			return null;
		}
		DeclarationDelta<?> declarationDelta = declarationListDelta
				.getChangedDeclarations().get(0);

		if (declarationDelta instanceof MethodDelta) {
			return (MethodDelta) declarationDelta;
		} else {
			return null;
		}
	}

	public MethodDelta getMethodDelta() throws IOException {
		return getCachableValue();
	}


}

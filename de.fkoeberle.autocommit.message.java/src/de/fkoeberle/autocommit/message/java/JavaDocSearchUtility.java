package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.EnumSet;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class JavaDocSearchUtility {

	@InjectedBySession
	private JavaFileDeltaProvider javaFileDeltaProvider;

	public JavaDocSearchResult search(DeclarationDelta<?> delta) {
		EnumSet<BodyDeclarationChangeType> unhandledChanges = EnumSet
				.copyOf(delta.getChangeTypes());
		boolean declarationListChanged = (unhandledChanges
				.remove(BodyDeclarationChangeType.DECLARATION_LIST));
		boolean enumConstantsChanged = (unhandledChanges
				.remove(BodyDeclarationChangeType.ENUM_CONSTANTS));
		boolean javaDocAddedChangedOrRemoved = (unhandledChanges
				.remove(BodyDeclarationChangeType.JAVADOC));
		if (unhandledChanges.size() != 0) {
			return JavaDocSearchResult.OTHER_CHANGES_FOUND;
		}
		boolean javaDocAddedOrChanged = false;
		if (declarationListChanged) {
			/*
			 * The following cast is okay since only TypeDeltas can have changed
			 * declaration lists:
			 */
			TypeDelta typeDelta = (TypeDelta) delta;
			DeclarationListDelta declarationListDelta = typeDelta
					.getDeclarationListDelta();
			JavaDocSearchResult searchResult = search(declarationListDelta);
			switch (searchResult) {
			case GOT_ADDED_OR_MODIFIED_ONLY:
				javaDocAddedOrChanged = true;
				break;
			case OTHER_CHANGES_FOUND:
				return JavaDocSearchResult.OTHER_CHANGES_FOUND;
			case NO_CHANGES_OR_JUST_JAVADOC_REMOVALS:
				// do nothing
				break;
			}
		}
		if (enumConstantsChanged) {
			/*
			 * The following cast is okay since only TypeDeltas can have changed
			 * declaration lists:
			 */
			TypeDelta typeDelta = (TypeDelta) delta;
			DeclarationListDelta declarationListDelta = typeDelta
					.getEnumConstantsDelta();
			JavaDocSearchResult searchResult = search(declarationListDelta);
			switch (searchResult) {
			case GOT_ADDED_OR_MODIFIED_ONLY:
				javaDocAddedOrChanged = true;
				break;
			case OTHER_CHANGES_FOUND:
				return JavaDocSearchResult.OTHER_CHANGES_FOUND;
			case NO_CHANGES_OR_JUST_JAVADOC_REMOVALS:
				// do nothing
				break;
			}
		}
		if (javaDocAddedChangedOrRemoved) {
			if (delta.getNewDeclaration().getJavadoc() != null) {
				javaDocAddedOrChanged = true;
			}
		}

		if (javaDocAddedOrChanged) {
			return JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY;
		} else {
			return JavaDocSearchResult.NO_CHANGES_OR_JUST_JAVADOC_REMOVALS;
		}
	}

	public JavaDocSearchResult search(DeclarationListDelta declarationListDelta) {
		boolean javaDocAddedOrChanged = false;
		if (declarationListDelta.getAddedDeclarations().size() > 0) {
			return JavaDocSearchResult.OTHER_CHANGES_FOUND;
		}
		if (declarationListDelta.getRemovedDeclarations().size() > 0) {
			return JavaDocSearchResult.OTHER_CHANGES_FOUND;
		}
		for (DeclarationDelta<?> child : declarationListDelta
				.getChangedDeclarations()) {
			JavaDocSearchResult childSearchResult = search(child);
			switch (childSearchResult) {
			case GOT_ADDED_OR_MODIFIED_ONLY:
				javaDocAddedOrChanged = true;
				break;
			case OTHER_CHANGES_FOUND:
				return JavaDocSearchResult.OTHER_CHANGES_FOUND;
			case NO_CHANGES_OR_JUST_JAVADOC_REMOVALS:
				break;
			}
		}
		if (javaDocAddedOrChanged) {
			return JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY;
		} else {
			return JavaDocSearchResult.NO_CHANGES_OR_JUST_JAVADOC_REMOVALS;
		}
	}

	private JavaDocSearchResult mergeResult(JavaDocSearchResult resultA,
			JavaDocSearchResult resultB) {
		if (resultA == JavaDocSearchResult.OTHER_CHANGES_FOUND
				|| resultB == JavaDocSearchResult.OTHER_CHANGES_FOUND) {
			return JavaDocSearchResult.OTHER_CHANGES_FOUND;
		}
		if (resultA == JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY
				|| resultB == JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY) {
			return JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY;
		}
		return JavaDocSearchResult.NO_CHANGES_OR_JUST_JAVADOC_REMOVALS;
	}

	public JavaDocSearchResult search(ChangedFile changedFile)
			throws IOException {
		JavaFileDelta javaFileDelta = javaFileDeltaProvider
				.getDeltaFor(changedFile);
		EnumSet<BodyDeclarationChangeType> unhandledChanges = javaFileDelta
				.getChangeTypes();
		boolean declarationListChanged = (unhandledChanges
				.remove(BodyDeclarationChangeType.DECLARATION_LIST));
		boolean packageChanged = (unhandledChanges
				.remove(BodyDeclarationChangeType.PACKAGE));

		if (!unhandledChanges.isEmpty()) {
			return JavaDocSearchResult.OTHER_CHANGES_FOUND;
		}
		JavaDocSearchResult resultToReturn = JavaDocSearchResult.NO_CHANGES_OR_JUST_JAVADOC_REMOVALS;

		if (declarationListChanged) {
			DeclarationListDelta declarationListDelta = javaFileDelta
					.getDeclarationListDelta();
			JavaDocSearchResult searchResult = search(declarationListDelta);
			resultToReturn = mergeResult(resultToReturn, searchResult);
			if (resultToReturn == JavaDocSearchResult.OTHER_CHANGES_FOUND) {
				return JavaDocSearchResult.OTHER_CHANGES_FOUND;
			}
		}
		if (packageChanged) {
			PackageDeclationDelta packageDelta = javaFileDelta
					.getPackageDelta();
			JavaDocSearchResult searchResult = search(packageDelta);
			resultToReturn = mergeResult(resultToReturn, searchResult);
			if (resultToReturn == JavaDocSearchResult.OTHER_CHANGES_FOUND) {
				return JavaDocSearchResult.OTHER_CHANGES_FOUND;
			}
		}

		return resultToReturn;
	}

	private JavaDocSearchResult search(PackageDeclationDelta delta) {
		EnumSet<BodyDeclarationChangeType> unhandledChanges = EnumSet
				.copyOf(delta.getChangeTypes());
		boolean javaDocAddedChangedOrRemoved = (unhandledChanges
				.remove(BodyDeclarationChangeType.JAVADOC));
		if (!unhandledChanges.isEmpty()) {
			return JavaDocSearchResult.OTHER_CHANGES_FOUND;
		}
		boolean javaDocAddedOrChanged = false;
		if (javaDocAddedChangedOrRemoved) {
			if (delta.getNewDeclaration().getJavadoc() != null) {
				javaDocAddedOrChanged = true;
			}
		}
		if (javaDocAddedOrChanged) {
			return JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY;
		} else {
			return JavaDocSearchResult.NO_CHANGES_OR_JUST_JAVADOC_REMOVALS;
		}
	}
}

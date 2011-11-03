package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.EnumSet;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class DocumentedTypeCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate documentedClassMessage = new CommitMessageTemplate(
			Translations.DocumentedTypeCMF_documentedClass);

	public final CommitMessageTemplate documentedInterfaceMessage = new CommitMessageTemplate(
			Translations.DocumentedTypeCMF_documentedInterface);

	public final CommitMessageTemplate documentedEnumMessage = new CommitMessageTemplate(
			Translations.DocumentedTypeCMF_documentedEnum);

	public final CommitMessageTemplate documentedAnnotationMessage = new CommitMessageTemplate(
			Translations.DocumentedTypeCMF_documentedAnnotation);

	@InjectedBySession
	private SingleChangedTypeView singleChangedTypeView;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		JavaDocSearchResult searchResult = search(typeDelta);
		if (searchResult != JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY) {
			return null;
		}

		switch (typeDelta.getType()) {
		case CLASS:
			return documentedClassMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		case INTERFACE:
			return documentedInterfaceMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		case ENUM:
			return documentedEnumMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		case ANNOTATION:
			return documentedAnnotationMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		default:
			return null;
		}
	}

	private enum JavaDocSearchResult {
		NO_CHANGES_OR_JUST_JAVADOC_REMOVALS, GOT_ADDED_OR_MODIFIED_ONLY, OTHER_CHANGES_FOUND
	};

	private JavaDocSearchResult search(DeclarationDelta<?> delta) {
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

	private JavaDocSearchResult search(DeclarationListDelta declarationListDelta) {
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

}

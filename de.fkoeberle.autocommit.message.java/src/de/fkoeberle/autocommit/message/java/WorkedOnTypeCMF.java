package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.Session;

public class WorkedOnTypeCMF implements ICommitMessageFactory {

	@CommitMessage
	public final CommitMessageTemplate workedOnClass = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnClass);

	@CommitMessage
	public final CommitMessageTemplate workedOnInterface = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInterface);

	@CommitMessage
	public final CommitMessageTemplate workedOnEnum = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnEnum);

	@CommitMessage
	public final CommitMessageTemplate workedOnAnnotation = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnAnnotation);

	@Override
	public String createMessageFor(FileSetDelta fileSetDelta, Session session) {
		SingleChangedJavaFileView view = session
				.getInstanceOf(SingleChangedJavaFileView.class);
		if (!view.isValid(fileSetDelta)) {
			return null;
		}
		DeclarationListDelta declationListDelta;
		try {
			declationListDelta = view.getDeclarationListDelta(session,
					fileSetDelta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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

		BodyDeclaration oldBodyDeclaration = declarationDelta
				.getOldDeclaration();
		BodyDeclaration newBodyDeclaration = declarationDelta
				.getNewDeclaration();

		if (!oldBodyDeclaration.getClass()
				.equals(newBodyDeclaration.getClass())) {
			return null;
		}

		if (oldBodyDeclaration instanceof TypeDeclaration) {
			TypeDeclaration typeDeclation = (TypeDeclaration) oldBodyDeclaration;
			if (typeDeclation.isInterface()) {
				return workedOnInterface
						.createMessageWithArgs(nameOf(typeDeclation));
			} else {
				return workedOnClass
						.createMessageWithArgs(nameOf(typeDeclation));
			}

		} else if (oldBodyDeclaration instanceof EnumDeclaration) {
			EnumDeclaration enumDeclaration = (EnumDeclaration) oldBodyDeclaration;
			return workedOnEnum.createMessageWithArgs(nameOf(enumDeclaration));

		} else if (oldBodyDeclaration instanceof AnnotationTypeDeclaration) {
			AnnotationTypeDeclaration annotationTypeDeclaration = (AnnotationTypeDeclaration) oldBodyDeclaration;
			return workedOnAnnotation
					.createMessageWithArgs(nameOf(annotationTypeDeclaration));

		} else {
			return null;
		}

	}

	private String nameOf(AbstractTypeDeclaration typeDeclation) {
		return typeDeclation.getName().getIdentifier();
	}

}

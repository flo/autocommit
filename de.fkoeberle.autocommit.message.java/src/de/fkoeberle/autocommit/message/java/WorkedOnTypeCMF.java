package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

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

	@InjectedBySession
	private SingleChangedTypeView view;

	@Override
	public String createMessage() throws IOException {
		DeclarationDelta declarationDelta = view.getDeclarationDelta();

		if (declarationDelta == null) {
			return null;
		}

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

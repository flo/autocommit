package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.FileContentReader;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class FormattedJavaTypeCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate formattedClassMessage = new CommitMessageTemplate(
			Translations.FormattedJavaTypeCMF_formattedClass);

	public final CommitMessageTemplate formattedInterfaceMessage = new CommitMessageTemplate(
			Translations.FormattedJavaTypeCMF_formattedInterface);

	public final CommitMessageTemplate formattedEnumMessage = new CommitMessageTemplate(
			Translations.FormattedJavaTypeCMF_formattedEnum);

	public final CommitMessageTemplate formattedAnnotationMessage = new CommitMessageTemplate(
			Translations.FormattedJavaTypeCMF_formattedAnnotation);

	@InjectedBySession
	private SingleChangedJavaFileView singleChangedJavaFileView;

	@InjectedBySession
	private JavaFormatationChecker formatationChecker;

	@InjectedBySession
	private FileContentReader reader;


	@Override
	public String createMessage() throws IOException {
		AbstractTypeDeclaration formattedType = determineFormattedType();
		if (formattedType == null) {
			return null;
		}
		String name = TypeUtil.fullTypeNameOf(formattedType);

		if (formattedType instanceof TypeDeclaration) {
			TypeDeclaration type = (TypeDeclaration) formattedType;
			if (type.isInterface()) {
				return formattedInterfaceMessage.createMessageWithArgs(name);
			} else {
				return formattedClassMessage.createMessageWithArgs(name);
			}
		} else if (formattedType instanceof EnumDeclaration) {
			return formattedEnumMessage.createMessageWithArgs(name);
		} else if (formattedType instanceof AnnotationTypeDeclaration) {
			return formattedAnnotationMessage.createMessageWithArgs(name);
		} else {
			// New unknown top level type can't be handled
			return null;
		}

	}

	/**
	 * 
	 * @return the most specific formatted type or null if it can't be said that
	 *         a type got formatted.
	 * @throws IOException
	 */
	private AbstractTypeDeclaration determineFormattedType() throws IOException {
		ChangedFile changedFile = singleChangedJavaFileView.getChangedFile();
		if (changedFile == null) {
			return null;
		}

		if (!formatationChecker.foundJavaFormatationChangesOnly(changedFile)) {
			return null;
		}

		String oldFile = reader.getStringFor(changedFile.getOldContent());
		String newFile = reader.getStringFor(changedFile.getNewContent());

		int firstDiff = firstIndexWithDifference(oldFile, newFile);
		int lastDiffInNewFile = lastNewFileIndexWithDifference(oldFile, newFile);

		// TODO reduce interval to non whitespace

		CompilationUnit newCompilationUnit = singleChangedJavaFileView
				.getNewCompilationUnit();
		if (newCompilationUnit == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<AbstractTypeDeclaration> typeDeclarations = newCompilationUnit
				.types();

		AbstractTypeDeclaration formattedType = findIntervalContainingType(
				firstDiff, lastDiffInNewFile, typeDeclarations);

		/*
		 * Change was outside a type definition but if a type is the only one in
		 * a file and that file got formatted it can be said that the type got
		 * formatted.
		 */
		if (formattedType == null) {
			if (typeDeclarations.size() == 1) {
				String path = changedFile.getPath();
				AbstractTypeDeclaration typeDeclaration = typeDeclarations
						.get(0);
				if (TypeUtil.typeNameMatchesFile(typeDeclaration, path)) {
					formattedType = typeDeclaration;
				}
			}
		}
		return formattedType;
	}

	private int firstIndexWithDifference(String oldFile, String newFile) {
		int lastSharedIndex = Math.min(oldFile.length() - 1,
				newFile.length() - 1);
		int firstDiff = 0;
		while (firstDiff <= lastSharedIndex
				&& ((oldFile.charAt(firstDiff) == newFile.charAt(firstDiff)))) {
			firstDiff++;
		}
		assert firstDiff <= lastSharedIndex : "since version control system won't deliver identical file contents";
		return firstDiff;
	}

	private int lastNewFileIndexWithDifference(String oldFile, String newFile) {
		int lastDiffInNewFile = newFile.length() - 1;
		int lastDiffInOldFile = oldFile.length() - 1;
		while ((lastDiffInOldFile >= 0)
				&& (lastDiffInNewFile >= 0)
				&& (oldFile.charAt(lastDiffInOldFile) == newFile
						.charAt(lastDiffInNewFile))) {
			lastDiffInOldFile--;
			lastDiffInNewFile--;
		}
		return lastDiffInNewFile;
	}

	private AbstractTypeDeclaration findIntervalContainingType(
			int firstIndexOfInterval, int lastIndexOfInterval,
			List<AbstractTypeDeclaration> typeDeclarations) {
		for (AbstractTypeDeclaration typeDeclaration : typeDeclarations) {
			int firstIndexOfDeclaration = typeDeclaration.getStartPosition();
			if (firstIndexOfDeclaration == -1) {
				continue; // information is needed, can't process otherwise
			}
			int declarationLength = typeDeclaration.getLength();
			if (declarationLength == -1) {
				continue; // information is needed, can't process otherwise
			}
			int lastIndexOfDeclaration = firstIndexOfDeclaration
					+ typeDeclaration.getLength() - 1;

			if (firstIndexOfDeclaration <= firstIndexOfInterval
					&& (lastIndexOfDeclaration >= lastIndexOfInterval)) {

				List<?> declarationsOfFormattedType = typeDeclaration
						.bodyDeclarations();
				List<AbstractTypeDeclaration> typesOfFormattedType = new ArrayList<AbstractTypeDeclaration>(
						declarationsOfFormattedType.size());
				for (Object childDeclaration : declarationsOfFormattedType) {
					if (childDeclaration instanceof AbstractTypeDeclaration) {
						typesOfFormattedType
								.add((AbstractTypeDeclaration) childDeclaration);
					}
				}

				AbstractTypeDeclaration childWithInterval = findIntervalContainingType(
						firstIndexOfInterval, lastIndexOfInterval,
						typesOfFormattedType);
				if (childWithInterval != null) {
					return childWithInterval;
				} else {
					return typeDeclaration;
				}
			}
		}
		return null;
	}
}
package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Set;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class DocumentedPackageCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate documentedSourceInTheDefaultPackageMessage = new CommitMessageTemplate(
			Translations.DocumentedPackageCMF_documentedSourceInTheDefaultPackage);

	public final CommitMessageTemplate documentedSourceInPackageMessage = new CommitMessageTemplate(
			Translations.DocumentedPackageCMF_documentedSourceInPackage);

	public final CommitMessageTemplate documentedSourceInSubPackagesOfMessage = new CommitMessageTemplate(
			Translations.DocumentedPackageCMF_documentedSourceInSubPackagesOf);

	public final CommitMessageTemplate documentedSourceMessage = new CommitMessageTemplate(
			Translations.DocumentedPackageCMF_documentedSource);

	@InjectedBySession
	private FileSetDelta fileSetDelta;

	@InjectedBySession
	private OnlyChangedFilesChecker onlyChangedFilesChecker;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	@InjectedBySession
	private JavaDocSearchUtility javaDocSearch;

	@Override
	public String createMessage() throws IOException {
		if (onlyChangedFilesChecker.checkFailed()) {
			return null;
		}
		if (!extensions.containsOnly("java")) {
			return null;
		}
		boolean javaDocAddedOrChanged = false;
		for (ChangedFile changedFile : fileSetDelta.getChangedFiles()) {
			JavaDocSearchResult searchResult = javaDocSearch
					.search(changedFile);
			switch (searchResult) {
			case GOT_ADDED_OR_MODIFIED_ONLY:
				javaDocAddedOrChanged = true;
				break;
			case OTHER_CHANGES_FOUND:
				return null;
			case NO_CHANGES_OR_JUST_JAVADOC_REMOVALS:
				// do nothing
				break;
			}
		}
		if (!javaDocAddedOrChanged) {
			/*
			 * If the user is removing javadoc he isn't documenting.
			 */
			return null;
		}
		PackageSetBuilder packageSetBuilder = new PackageSetBuilder(parser);
		packageSetBuilder.addPackagesOf(fileSetDelta);
		Set<String> packages = packageSetBuilder.getPackages();

		if (packages.size() == 1) {
			String p = packages.iterator().next();
			if (p.equals("")) { //$NON-NLS-1$
				return documentedSourceInTheDefaultPackageMessage
						.createMessageWithArgs();
			} else {
				return documentedSourceInPackageMessage
						.createMessageWithArgs(p);
			}
		}

		CommonParentPackageFinder commonParentPackageFinder = new CommonParentPackageFinder();
		commonParentPackageFinder.checkPackages(packages);
		String commonPackage = commonParentPackageFinder.getCommonPackage();
		if (commonPackage != null) {
			return documentedSourceInSubPackagesOfMessage
					.createMessageWithArgs(commonPackage);
		} else {
			return documentedSourceMessage.createMessageWithArgs();
		}
	}

}

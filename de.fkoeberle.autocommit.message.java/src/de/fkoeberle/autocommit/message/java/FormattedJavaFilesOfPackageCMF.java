package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Set;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class FormattedJavaFilesOfPackageCMF implements ICommitMessageFactory {
	public final CommitMessageTemplate formattedSourceInPackageMessage = new CommitMessageTemplate(
			Translations.FormattedJavaFilesOfPackageCMF_formattedSourceInPackage);

	public final CommitMessageTemplate formattedSourceInSubPackagesOfMessage = new CommitMessageTemplate(
			Translations.FormattedJavaFilesOfPackageCMF_formattedSourceInSubPackagesOf);

	public final CommitMessageTemplate formattedSourceInTheDefaultPackageMessage = new CommitMessageTemplate(
			Translations.FormattedJavaFilesOfPackageCMF_formattedSourceInTheDefaultPackage);

	public final CommitMessageTemplate formattedSourceMessage = new CommitMessageTemplate(
			Translations.FormattedJavaFilesOfPackageCMF_formattedSource);
	@InjectedBySession
	private JavaFormatationChecker formatationChecker;

	@InjectedBySession
	private OnlyChangedFilesChecker onlyChangedFilesChecker;

	@InjectedBySession
	private FileSetDelta fileSetDelta;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	@Override
	public String createMessage() throws IOException {
		if (onlyChangedFilesChecker.checkFailed()) {
			return null;
		}
		for (ChangedFile changedFile : fileSetDelta.getChangedFiles()) {
			if (!formatationChecker
					.foundJavaFormatationChangesOnly(changedFile)) {
				return null;
			}
		}
		PackageSetBuilder packageSetBuilder = new PackageSetBuilder(parser);
		packageSetBuilder.addPackagesOf(fileSetDelta);
		Set<String> packages = packageSetBuilder.getPackages();

		if (packages.size() == 1) {
			String p = packages.iterator().next();
			if (p.equals("")) { //$NON-NLS-1$
				return formattedSourceInTheDefaultPackageMessage
						.createMessageWithArgs();
			} else {
				return formattedSourceInPackageMessage.createMessageWithArgs(p);
			}
		}

		CommonParentPackageFinder commonParentPackageFinder = new CommonParentPackageFinder();
		commonParentPackageFinder.checkPackages(packages);
		String commonPackage = commonParentPackageFinder.getCommonPackage();
		if (commonPackage != null) {
			return formattedSourceInSubPackagesOfMessage
					.createMessageWithArgs(commonPackage);
		} else {
			return formattedSourceMessage.createMessageWithArgs();
		}
	}
}

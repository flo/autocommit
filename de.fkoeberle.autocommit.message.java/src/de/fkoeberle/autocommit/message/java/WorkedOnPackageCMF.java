package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Set;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnPackageCMF implements ICommitMessageFactory {
	@CommitMessage
	public final CommitMessageTemplate workedOnDefaultPackage = new CommitMessageTemplate(
			Translations.WorkedOnPackageCMF_workedOnDefaultPackage);

	@CommitMessage
	public final CommitMessageTemplate workedOnPackage = new CommitMessageTemplate(
			Translations.WorkedOnPackageCMF_workedOnPackage);

	@CommitMessage
	public final CommitMessageTemplate workedOnSubPackages = new CommitMessageTemplate(
			Translations.WorkedOnPackageCMF_workedOnSubPackages);

	@InjectedBySession
	private FileSetDelta delta;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	@Override
	public String createMessage() throws IOException {
		if (!extensions.containsOnly("java")) {
			return null;
		}

		PackageSetBuilder builder = new PackageSetBuilder(parser);
		boolean success = builder.addPackagesOf(delta);
		if (!success) {
			return null;
		}
		Set<String> packageNames = builder.getPackages();

		if (packageNames.size() == 0) {
			// empty commit:
			return null;
		}
		if (packageNames.size() == 1) {
			String p = packageNames.iterator().next();
			if (p.equals("")) { //$NON-NLS-1$
				return workedOnDefaultPackage.createMessageWithArgs();
			} else {
				return workedOnPackage.createMessageWithArgs(p);
			}
		}
		CommonParentPackageFinder commonParentFinder = new CommonParentPackageFinder();
		for (String packageName : packageNames) {
			commonParentFinder.checkPackage(packageName);
		}
		String commonParent = commonParentFinder.getCommonPackage();
		if (commonParent == null) {
			return null;
		}
		return workedOnSubPackages.createMessageWithArgs(commonParent);
	}
}

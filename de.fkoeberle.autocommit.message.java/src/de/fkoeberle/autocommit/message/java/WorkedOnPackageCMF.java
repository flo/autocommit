package de.fkoeberle.autocommit.message.java;

import java.util.Collections;
import java.util.Set;

import org.eclipse.osgi.util.NLS;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.Session;

public class WorkedOnPackageCMF implements ICommitMessageFactory {
	private static final Set<String> DOT_JAVA = Collections.singleton("java"); //$NON-NLS-1$

	@CommitMessage
	public final CommitMessageTemplate workedOnDefaultPackage = new CommitMessageTemplate(
			Translations.WorkedOnPackageCMF_workedOnDefaultPackage);

	@CommitMessage
	public final CommitMessageTemplate workedOnPackage = new CommitMessageTemplate(
			Translations.WorkedOnPackageCMF_workedOnPackage);

	@CommitMessage
	public final CommitMessageTemplate workedOnSubPackages = new CommitMessageTemplate(
			Translations.WorkedOnPackageCMF_workedOnSubPackages);

	@Override
	public String createMessageFor(FileSetDelta delta, Session session) {
		if (!delta.getFileExtensions().equals(DOT_JAVA)) {
			return null;
		}

		PackageSetBuilder builder = new PackageSetBuilder(session);
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
		return NLS.bind(Translations.WorkedOnPackageCMF_workedOnSubPackages,
				commonParent);
	}
}

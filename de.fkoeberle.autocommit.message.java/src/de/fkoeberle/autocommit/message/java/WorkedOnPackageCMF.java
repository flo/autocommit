package de.fkoeberle.autocommit.message.java;

import java.util.Collections;
import java.util.Set;

import org.eclipse.osgi.util.NLS;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;

public class WorkedOnPackageCMF implements ICommitMessageFactory {
	private static final Set<String> DOT_JAVA = Collections.singleton("java"); //$NON-NLS-1$

	@CommitMessage
	public String workedOnDefaultPackage = Translations.WorkedOnPackageCMF_workedOnDefaultPackage;

	@CommitMessage
	public String workedOnPackage = Translations.WorkedOnPackageCMF_workedOnPackage;

	@CommitMessage
	public String workedOnSubPackages = Translations.WorkedOnPackageCMF_workedOnSubPackages;

	@Override
	public String createMessageFor(FileSetDelta delta) {
		if (!delta.getFileExtensions().equals(DOT_JAVA)) {
			return null;
		}

		PackageSetBuilder builder = new PackageSetBuilder();
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
				return workedOnDefaultPackage;
			} else {
				return NLS.bind(workedOnPackage, p);
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

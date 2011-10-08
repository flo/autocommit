package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;


import de.fkoeberle.autocommit.message.CommonPrefixFinder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;

public class ModifiedPackagesCMF implements ICommitMessageFactory {
	private static final Set<String> DOT_JAVA = Collections.singleton("java");

	@Override
	public String build(FileSetDelta delta) {
		if (!delta.getFileExtensions().equals(DOT_JAVA)) {
			return null;
		}
		try {
			PackageSetBuilder builder = new PackageSetBuilder();
			boolean success = builder.addPackagesOf(delta);
			if (!success) {
				return null;
			}
			Set<String> packageNames = builder.getPackages();
					
			// TODO handle default package (null in set)

			CommonPrefixFinder prefixFinder = new CommonPrefixFinder();
			for (String packageName : packageNames) {
				prefixFinder.checkForShorterPrefix(packageName);
			}
			String prefix = prefixFinder.getPrefix();
			if (prefix == null) {
				return null;
			}
			// TODO Handle the prefix of the following case as "org.example":
			// org.example.test1
			// org.example.test2
			// TODO Handle the prefix of:
			// org.example.test
			// org.examplepackage.test
			// not as "org" and not "org.examplepackage"

			return "Worked on package " + prefix + "*";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// TODO rename getChangedFiles()
		// TODO iterate added and removed files
	}
}

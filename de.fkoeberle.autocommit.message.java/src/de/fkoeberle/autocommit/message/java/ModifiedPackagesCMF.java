package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import de.fkoeberle.autocommit.message.CommonPrefixFinder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.ModifiedFile;

public class ModifiedPackagesCMF implements ICommitMessageFactory {
	private static final Set<String> DOT_JAVA = Collections.singleton("java");

	@Override
	public String build(FileSetDelta delta) {
		if (!delta.getFileExtensions().equals(DOT_JAVA)) {
			return null;
		}
		Set<String> packageNames = new HashSet<String>();
		List<String> sourceFolders = new ArrayList<String>();
		for (ModifiedFile file : delta.getChangedFiles()) {
			String filePath = file.getPath();
			IFileContent fileContent = file.getNewContent();
			int lastSlash = filePath.lastIndexOf('/');
			final String directoryPath;
			if (lastSlash == -1) {
				directoryPath = "/";
			} else {
				directoryPath = filePath.substring(0,lastSlash);
			}
			String packageName = null;
			for (String sourceFolder:sourceFolders) {
				if (directoryPath.startsWith(sourceFolder)) {
					String packagePath = directoryPath.substring(sourceFolder.length());
					packageName = packagePath.replace('/', '.');
					break;
				}
			}
			
			if (packageName == null) {
				JavaFileContent javaFileContent = fileContent
						.getAdapter(JavaFileContent.class);
				try {
					packageName = extractPackage(javaFileContent);
				} catch (IOException e) {
					// TODO log warning
					return null;
				}
				if (packageName == null) {
					if (directoryPath.equals("/")) {
						sourceFolders.add("/");
					} else {
						// path contains no directories 
						// => path contains not the existing package directories
						// => can't be handled
						return null; 
					}
				} else {
					String packagePath = packageName.replace(".", "/");
					if (directoryPath.endsWith(packagePath)) {
						String sourceFolder = directoryPath.substring(0,directoryPath.length() - packagePath.length());
						sourceFolders.add(sourceFolder);
					} else {
						return null;
					}
				}
			}
			packageNames.add(packageName);
		}

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

		// TODO rename getChangedFiles()
		// TODO iterate added and removed files
	}

	private String extractPackage(JavaFileContent javaFileContent)
			throws IOException {
		CompilationUnit compilationUnit = javaFileContent
				.getCompilationUnitForReadOnlyPurposes();
		PackageDeclaration packageDeclaration = compilationUnit.getPackage();
		if (packageDeclaration == null) {
			return null;
		}
		Name packageNameObject = packageDeclaration.getName();
		return packageNameObject.getFullyQualifiedName();
	}

}

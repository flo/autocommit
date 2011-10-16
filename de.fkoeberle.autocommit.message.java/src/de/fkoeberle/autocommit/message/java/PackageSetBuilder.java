package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import de.fkoeberle.autocommit.message.AddedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.ISession;
import de.fkoeberle.autocommit.message.ModifiedFile;
import de.fkoeberle.autocommit.message.RemovedFile;

final class PackageSetBuilder {
	private final ISession session;
	private final Set<String> packageNames = new HashSet<String>();
	private final List<String> sourceFolders = new ArrayList<String>();


	public PackageSetBuilder(ISession session) {
		this.session = session;
	}

	private boolean addPackageOfFile(String filePath,
 IFileContent fileContent) {
		final String directoryPath = directoryOf(filePath);
		String packageName = determinePackageFromDirectory(directoryPath);

		if (packageName == null) {
			IJavaFileContent javaFileContent = session.getSharedAdapter(
					fileContent, IJavaFileContent.class);

			try {
				packageName = extractPackage(javaFileContent);
			} catch (Exception e) {
				// TODO better logging
				e.printStackTrace();
				return false;
			}
			if (packageName == null) {
				sourceFolders.add("/");
				packageName = "";
			} else {
				String packagePath = packageName.replace(".", "/");
				if (directoryPath.endsWith(packagePath)) {
					String sourceFolder = directoryPath.substring(0,
							directoryPath.length() - packagePath.length());
					sourceFolders.add(sourceFolder);
				} else {
					// the determined package path seems
					// not to match directory structure
					return false;
				}
			}
		}
		packageNames.add(packageName);
		return true;
	}

	private static String directoryOf(String filePath) {
		int lastSlash = filePath.lastIndexOf('/');
		final String directoryPath;
		if (lastSlash == -1) {
			directoryPath = "/";
		} else {
			directoryPath = filePath.substring(0, lastSlash);
		}
		return directoryPath;
	}

	private String determinePackageFromDirectory(final String directoryPath) {
		String packageName = null;
		for (String sourceFolder : sourceFolders) {
			if (directoryPath.startsWith(sourceFolder)) {
				String packagePath = directoryPath.substring(sourceFolder
						.length());
				packageName = packagePath.replace('/', '.');
				break;
			}
		}
		// TODO check and document: returns "" if default package
		return packageName;
	}

	/**
	 * 
	 * @return the extracted package or "" if there is no package declaration
	 *         but the file is otherwise valid.
	 */
	private String extractPackage(IJavaFileContent javaFileContent)
			throws IOException {
		CompilationUnit compilationUnit = javaFileContent
				.getCompilationUnitForReadOnlyPurposes(session);
		PackageDeclaration packageDeclaration = compilationUnit
				.getPackage();
		if (packageDeclaration == null) {
			return null;
		}
		Name packageNameObject = packageDeclaration.getName();
		return packageNameObject.getFullyQualifiedName();
	}

	public Set<String> getPackages() {
		return packageNames;
	}

	public boolean addPackagesOf(FileSetDelta delta) {
		for (ModifiedFile file : delta.getChangedFiles()) {
			boolean success = addPackageOfFile(file.getPath(),
					file.getNewContent());
			if (!success) {
				return false;
			}
		}
		for (AddedFile file : delta.getAddedFiles()) {
			boolean success = addPackageOfFile(file.getPath(),
					file.getNewContent());
			if (!success) {
				return false;
			}
		}
		for (RemovedFile file : delta.getRemovedFiles()) {
			boolean success = addPackageOfFile(file.getPath(),
					file.getOldContent());
			if (!success) {
				return false;
			}
		}
		return true;
	}
}
/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.RemovedFile;

public final class PackageSetBuilder {
	private final Set<String> packageNames = new HashSet<String>();
	private final List<String> sourceFolders = new ArrayList<String>();

	private final CachingJavaFileContentParser parser;

	public PackageSetBuilder(CachingJavaFileContentParser parser) {
		this.parser = parser;
	}

	private boolean addPackageOfFile(String filePath, IFileContent fileContent)
			throws IOException {
		final String directoryPath = directoryOf(filePath);
		String packageName = determinePackageFromDirectory(directoryPath);

		if (packageName == null) {
			CompilationUnit compilationUnit = parser
					.getInstanceFor(fileContent);

			packageName = extractPackage(compilationUnit);

			if (packageName == null) {
				sourceFolders.add(directoryPath);
				packageName = "";
			} else {
				String packagePath = packageName.replace(".", "/");
				packagePath = packagePath + "/";
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
			directoryPath = filePath.substring(0, lastSlash + 1);
		}
		return directoryPath;
	}

	private String determinePackageFromDirectory(final String directoryPath) {
		String packageName = null;
		for (String sourceFolder : sourceFolders) {
			if (directoryPath.startsWith(sourceFolder)) {
				int fromIndex = sourceFolder.length();
				int toIndex = directoryPath.length() - 1;
				String packagePath;
				if (toIndex > fromIndex) {
					packagePath = directoryPath.substring(
							sourceFolder.length(), directoryPath.length() - 1);
				} else {
					packagePath = "";
				}
				packageName = packagePath.replace('/', '.');
				break;
			}
		}
		return packageName;
	}

	/**
	 * 
	 * @return the extracted package or "" if there is no package declaration
	 *         but the file is otherwise valid.
	 */
	private String extractPackage(CompilationUnit compilationUnit) {
		PackageDeclaration packageDeclaration = compilationUnit.getPackage();
		if (packageDeclaration == null) {
			return null;
		}
		Name packageNameObject = packageDeclaration.getName();
		return packageNameObject.getFullyQualifiedName();
	}

	public Set<String> getPackages() {
		return packageNames;
	}

	public boolean addPackagesOf(FileSetDelta delta) throws IOException {
		for (ChangedFile file : delta.getChangedFiles()) {
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
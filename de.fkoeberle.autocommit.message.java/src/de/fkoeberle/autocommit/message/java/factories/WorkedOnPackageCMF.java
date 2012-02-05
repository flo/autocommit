/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.factories;

import java.io.IOException;
import java.util.Set;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.CachingJavaFileContentParser;
import de.fkoeberle.autocommit.message.java.helper.CommonParentPackageFinder;
import de.fkoeberle.autocommit.message.java.helper.PackageSetBuilder;

public class WorkedOnPackageCMF implements ICommitMessageFactory {
	@InjectedAfterConstruction
	CommitMessageTemplate workedOnDefaultPackage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnPackage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnSubPackages;

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

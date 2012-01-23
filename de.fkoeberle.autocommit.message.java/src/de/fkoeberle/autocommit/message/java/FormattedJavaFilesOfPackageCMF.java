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
import java.util.Set;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class FormattedJavaFilesOfPackageCMF implements ICommitMessageFactory {
	@InjectedAfterConstruction
	CommitMessageTemplate formattedSourceInPackageMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate formattedSourceInSubPackagesOfMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate formattedSourceInTheDefaultPackageMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate formattedSourceMessage;

	@InjectedBySession
	private JavaFormatationChecker formatationChecker;

	@InjectedBySession
	private OnlyChangedFilesChecker onlyChangedFilesChecker;

	@InjectedBySession
	private FileSetDelta fileSetDelta;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	@InjectedBySession
	private JavaFileDeltaProvider javaFileDeltaProvider;

	@Override
	public String createMessage() throws IOException {
		if (onlyChangedFilesChecker.checkFailed()) {
			return null;
		}
		for (ChangedFile changedFile : fileSetDelta.getChangedFiles()) {
			JavaFileDelta javaFileDelta = javaFileDeltaProvider
					.getDeltaFor(changedFile);
			if (!formatationChecker
					.foundJavaFormatationChangesOnly(javaFileDelta)) {
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

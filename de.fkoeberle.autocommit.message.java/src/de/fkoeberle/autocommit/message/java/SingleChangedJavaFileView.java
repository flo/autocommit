package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.SingleChangedFileView;

public class SingleChangedJavaFileView {
	private boolean changedFileDetermined;
	private ChangedFile changedFile;
	private SoftReference<CompilationUnit> oldCompilationUnitRef;
	private SoftReference<CompilationUnit> newCompilationUnitRef;
	private SoftReference<DeclarationListDelta> declarationListDeltaRef;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	@InjectedBySession
	private SingleChangedFileView singleChangedFileView;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	/**
	 * 
	 * @return the compilation unit of the new version of the file or null if
	 *         {@link #isValid(FileSetDelta)} returns false.
	 * @throws IOException
	 *             if the compilation unit got parsed and an IOException
	 *             occured.
	 */
	public CompilationUnit getNewCompilationUnit() throws IOException {
		CompilationUnit compilationUnit = null;
		if (newCompilationUnitRef != null) {
			compilationUnit = newCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			ChangedFile file = getChangedFile();
			if (file == null) {
				return null;
			}
			compilationUnit = parser.getInstanceFor(file.getNewContent());
			newCompilationUnitRef = new SoftReference<CompilationUnit>(
					compilationUnit);
		}
		return compilationUnit;
	}

	/**
	 * 
	 * @return the compilation unit of the new version of the file or null if
	 *         {@link #isValid(FileSetDelta)} returns false.
	 * @throws IOException
	 *             if the compilation unit got parsed and an IOException
	 *             occurred.
	 */
	public CompilationUnit getOldCompilationUnit() throws IOException {
		CompilationUnit compilationUnit = null;
		if (oldCompilationUnitRef != null) {
			compilationUnit = oldCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			ChangedFile file = getChangedFile();
			if (file == null) {
				return null;
			}
			compilationUnit = parser.getInstanceFor(file.getOldContent());
			oldCompilationUnitRef = new SoftReference<CompilationUnit>(
					compilationUnit);
		}
		return compilationUnit;
	}

	/**
	 * 
	 * @return an instance of {@link DeclarationListDelta} or null if
	 *         {@link #isValid()} returns false.
	 * @throws IOException
	 */
	public DeclarationListDelta getDeclarationListDelta() throws IOException {
		DeclarationListDelta declarationListDelta = null;
		if (declarationListDeltaRef != null) {
			declarationListDelta = declarationListDeltaRef.get();
		}
		if (declarationListDelta == null) {
			if (!isValid()) {
				return null;
			}
			CompilationUnit oldCompilationUnit = getOldCompilationUnit();
			CompilationUnit newCompilationUnit = getNewCompilationUnit();
			declarationListDelta = new DeclarationListDelta(oldCompilationUnit,
					newCompilationUnit);
			declarationListDeltaRef = new SoftReference<DeclarationListDelta>(
					declarationListDelta);

		}
		return declarationListDelta;
	}

	public ChangedFile getChangedFile() {
		if (!changedFileDetermined) {
			changedFileDetermined = true;
			if (!extensions.containsOnly("java")) {
				return null;
			}
			changedFile = singleChangedFileView.getChangedFile();
		}
		return changedFile;
	}

	public boolean isValid() {
		// TODO remove this method and update documentation
		return getChangedFile() != null;
	}
}

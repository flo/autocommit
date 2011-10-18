package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.ModifiedFile;

public class SingleChangedJavaFileView {
	private boolean changedFileDetermined;
	private ModifiedFile changedFile;
	private SoftReference<CompilationUnit> oldCompilationUnitRef;
	private SoftReference<CompilationUnit> newCompilationUnitRef;
	private static final Set<String> DOT_JAVA = Collections.singleton("java"); //$NON-NLS-1$
	private SoftReference<DeclarationListDelta> declarationListDeltaRef;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	@InjectedBySession
	private FileSetDelta delta;

	/**
	 * 
	 * @return the compilation unit of the new version of the file or null if
	 *         {@link #isValid(FileSetDelta)} returns false.
	 * @throws IOException
	 *             if the compilation unit got parsed and an IOException
	 *             occured.
	 */
	public CompilationUnit getNewCompilationUnit()
			throws IOException {
		CompilationUnit compilationUnit = null;
		if (newCompilationUnitRef != null) {
			compilationUnit = newCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			ModifiedFile file = getChangedFile();
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
	public CompilationUnit getOldCompilationUnit()
			throws IOException {
		CompilationUnit compilationUnit = null;
		if (oldCompilationUnitRef != null) {
			compilationUnit = oldCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			ModifiedFile file = getChangedFile();
			if (file == null) {
				return null;
			}
			compilationUnit = parser.getInstanceFor(file.getOldContent());
			oldCompilationUnitRef = new SoftReference<CompilationUnit>(
					compilationUnit);
		}
		return compilationUnit;
	}

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
			declarationListDelta = new DeclarationListDelta(oldCompilationUnit, newCompilationUnit);
			declarationListDeltaRef = new SoftReference<DeclarationListDelta>(
					declarationListDelta);

		}
		return declarationListDelta;
	}

	public ModifiedFile getChangedFile() {
		if (!changedFileDetermined) {
			changedFileDetermined = true;
			if (!delta.getFileExtensions().equals(DOT_JAVA)) {
				return null;
			}
			if (delta.getChangedFiles().size() != 1) {
				return null;
			}
			if (delta.getRemovedFiles().size() != 0) {
				return null;
			}
			if (delta.getAddedFiles().size() != 0) {
				return null;
			}

			changedFile = delta.getChangedFiles().get(0);
		}
		return changedFile;
	}

	public boolean isValid() {
		return getChangedFile() != null;
	}
}

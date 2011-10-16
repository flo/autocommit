package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ModifiedFile;
import de.fkoeberle.autocommit.message.Session;

public class SingleChangedJavaFileView {
	private boolean changedFileDetermined;
	private ModifiedFile changedFile;
	private SoftReference<CompilationUnit> oldCompilationUnitRef;
	private SoftReference<CompilationUnit> newCompilationUnitRef;
	private static final Set<String> DOT_JAVA = Collections.singleton("java"); //$NON-NLS-1$
	private SoftReference<DeclarationListDelta> declarationListDeltaRef;

	/**
	 * 
	 * @return the compilation unit of the new version of the file or null if
	 *         {@link #isValid(FileSetDelta)} returns false.
	 * @throws IOException
	 *             if the compilation unit got parsed and an IOException
	 *             occured.
	 */
	public CompilationUnit getNewCompilationUnit(Session session,
			FileSetDelta delta)
			throws IOException {
		CompilationUnit compilationUnit = null;
		if (newCompilationUnitRef != null) {
			compilationUnit = newCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			ModifiedFile file = getChangedFile(delta);
			if (file == null) {
				return null;
			}
			CachingJavaFileContentParser parser = session
					.getInstanceOf(CachingJavaFileContentParser.class);
			compilationUnit = parser.getInstanceFor(file.getNewContent(),
					session);
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
	 *             occured.
	 */
	public CompilationUnit getOldCompilationUnit(Session session,
			FileSetDelta delta)
			throws IOException {
		CompilationUnit compilationUnit = null;
		if (oldCompilationUnitRef != null) {
			compilationUnit = oldCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			ModifiedFile file = getChangedFile(delta);
			if (file == null) {
				return null;
			}
			CachingJavaFileContentParser parser = session
					.getInstanceOf(CachingJavaFileContentParser.class);
			compilationUnit = parser.getInstanceFor(file.getOldContent(),
					session);
			oldCompilationUnitRef = new SoftReference<CompilationUnit>(
					compilationUnit);
		}
		return compilationUnit;
	}

	public DeclarationListDelta getDeclarationListDelta(Session session,
			FileSetDelta fileSetDelta) throws IOException {
		DeclarationListDelta declarationListDelta = null;
		if (declarationListDeltaRef != null) {
			declarationListDelta = declarationListDeltaRef.get();
		}
		if (declarationListDelta == null) {
			if (!isValid(fileSetDelta)) {
				return null;
			}
			CompilationUnit oldCompilationUnit = getOldCompilationUnit(
					session, fileSetDelta);
			CompilationUnit newCompilationUnit = getNewCompilationUnit(session,
					fileSetDelta);
			declarationListDelta = new DeclarationListDelta(oldCompilationUnit, newCompilationUnit);
			declarationListDeltaRef = new SoftReference<DeclarationListDelta>(
					declarationListDelta);

		}
		return declarationListDelta;
	}

	public ModifiedFile getChangedFile(FileSetDelta delta) {
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

	public boolean isValid(FileSetDelta delta) {
		return getChangedFile(delta) != null;
	}
}

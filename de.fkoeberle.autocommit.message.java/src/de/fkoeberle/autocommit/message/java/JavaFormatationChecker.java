package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class JavaFormatationChecker {
	private SoftReference<Map<ChangedFile, Boolean>> cache;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	/**
	 * 
	 * @param changedFile
	 * @return true if it can be guaranteed that there were only formation
	 *         changes and false otherwise.
	 * @throws IOException
	 */
	public boolean foundJavaFormatationChangesOnly(ChangedFile changedFile)
			throws IOException {

		Map<ChangedFile, Boolean> map = null;
		if (cache != null) {
			map = cache.get();
		}
		if (map == null) {
			map = new HashMap<ChangedFile, Boolean>();
			cache = new SoftReference<Map<ChangedFile, Boolean>>(map);
		}
		Boolean result = map.get(changedFile);
		if (result == null) {
			CompilationUnit oldContent = parser.getInstanceFor(changedFile
					.getOldContent());
			CompilationUnit newContent = parser.getInstanceFor(changedFile
					.getNewContent());

			if (containsProblems(oldContent)) {
				return false;
			}
			if (containsProblems(newContent)) {
				return false;
			}
			boolean match = oldContent.subtreeMatch(new ASTMatcher(true),
					newContent);
			result = Boolean.valueOf(match);
			map.put(changedFile, result);
		}

		return result.booleanValue();
	}

	private static boolean containsProblems(CompilationUnit compUnit) {
		return compUnit.getProblems().length != 0;
	}
}

package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.core.IteratorService;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.SkipWorkTreeFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import de.fkoeberle.autocommit.IRepository;
import de.fkoeberle.autocommit.message.CommitMessageBuilderPluginActivator;
import de.fkoeberle.autocommit.message.ICommitMessageBuilder;

/**
 * Enhances and existing git repository to match the interface
 * {@link IRepository}. It keeps only a weak reference to the repository, which
 * makes it possible to manage instances of this class in a WeakHashMap from
 * {@link Repository} to {@link GitRepositoryAdapter}.
 */
public class GitRepositoryAdapter implements IRepository {
	private final WeakReference<Repository> repositoryRef;
	private final List<Object> sessionData;
	private byte[] sessionDataDeltaHash;

	public GitRepositoryAdapter(Repository repository) {
		this.repositoryRef = new WeakReference<Repository>(repository);
		this.sessionData = new ArrayList<Object>();
	}

	@Override
	public void commit() {
		Repository repository = repositoryRef.get();
		if (repository == null) {
			return;
		}
		WorkingTreeIterator workingTreeIterator = IteratorService
				.createInitialIterator(repository);
		try {
			IndexDiff indexDiff = new IndexDiff(repository, Constants.HEAD,
					workingTreeIterator);
			boolean differencesFound = indexDiff.diff(); // TODO use version
															// with progress
															// monitor
			if (differencesFound) {
				Git git = new Git(repository);
				Collection<String> filesToCommit = new HashSet<String>(
						indexDiff.getChanged());
				filesToCommit.addAll(indexDiff.getUntracked());
				filesToCommit.addAll(indexDiff.getModified());
				filesToCommit.addAll(indexDiff.getAdded());

				if (filesToCommit.size() > 0) {
					AddCommand addCommand = git.add();
					for (String path : filesToCommit) {
						addCommand.addFilepattern(path);
					}
					try {
						addCommand.call();
					} catch (NoFilepatternException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
				Set<String> filesToRemove = indexDiff.getMissing();
				if (filesToRemove.size() > 0) {
					RmCommand rmCommand = git.rm();
					for (String path : indexDiff.getMissing()) {
						rmCommand.addFilepattern(path);
					}
					try {
						rmCommand.call();
					} catch (NoFilepatternException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}

				String message;
				try {
					message = buildCommitMessage(repository);
				} catch (NoHeadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				CommitCommand commitCommand = git.commit();
				commitCommand.setMessage(message);

				try {
					commitCommand.call();
				} catch (NoHeadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (NoMessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (ConcurrentRefUpdateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (JGitInternalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (WrongRepositoryStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	@Override
	public boolean noUncommittedChangesExist() {
		Repository repository = repositoryRef.get();
		if (repository == null) {
			return true;
		}
		WorkingTreeIterator workingTreeIterator = IteratorService
				.createInitialIterator(repository);
		try {
			IndexDiff indexDiff = new IndexDiff(repository, Constants.HEAD,
					workingTreeIterator);
			if (indexDiff.diff()) {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String buildCommitMessage(Repository repository)
			throws IOException, NoHeadException {
		final ICommitMessageBuilder messageBuilder = CommitMessageBuilderPluginActivator
				.getDefault().createBuilder();
		final ObjectReader reader = repository.newObjectReader();
		FileSetDeltaVisitor FileDeltaToMessageBuilderAdder = new FileDeltaToMessageBuilderAdder(
				reader, messageBuilder);
		if (sessionDataDeltaHash != null) {
			HashCacluatingDeltaVisitor hashCalculator = new HashCacluatingDeltaVisitor();
			visitHeadIndexDelta(repository, FileDeltaToMessageBuilderAdder,
					hashCalculator);
			byte[] currentHash = hashCalculator.buildHash();
			if (Arrays.equals(sessionDataDeltaHash, currentHash)) {
				for (Object data : sessionData) {
					messageBuilder.addSessionData(data);
				}
			}
			sessionDataDeltaHash = null;
		} else {
			visitHeadIndexDelta(repository, FileDeltaToMessageBuilderAdder);
		}
		return messageBuilder.buildMessage();
	}

	private void visitHeadIndexDelta(Repository repository,
			FileSetDeltaVisitor... visitors) throws IOException,
			NoHeadException {
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.setRecursive(true);
		ObjectId headTreeId = repository.resolve(Constants.HEAD);
		if (headTreeId == null) {
			throw new NoHeadException("Failed to resolve HEAD");
		}
		RevWalk revWalk = new RevWalk(repository);
		RevTree revTree = revWalk.parseTree(headTreeId);
		DirCache dirCache = repository.readDirCache();
		DirCacheIterator dirCacheTree = new DirCacheIterator(dirCache);
		int revTreeIndex = treeWalk.addTree(revTree);
		int dirCacheTreeIndex = treeWalk.addTree(dirCacheTree);
		TreeFilter filter = AndTreeFilter.create(TreeFilter.ANY_DIFF,
				new SkipWorkTreeFilter(dirCacheTreeIndex));
		treeWalk.setFilter(filter);

		while (treeWalk.next()) {
			AbstractTreeIterator headMatch = treeWalk.getTree(revTreeIndex,
					AbstractTreeIterator.class);
			DirCacheIterator dirCacheMatch = treeWalk.getTree(
					dirCacheTreeIndex, DirCacheIterator.class);
			final String path = treeWalk.getPathString();
			ObjectId oldObjectId = null;
			if (headMatch != null) {
				oldObjectId = headMatch.getEntryObjectId();
			}
			ObjectId newObjectId = null;
			if (dirCacheMatch != null) {
				newObjectId = dirCacheMatch.getEntryObjectId();
			}
			for (FileSetDeltaVisitor visitor : visitors) {
				if (newObjectId == null) {
					visitor.visitRemovedFile(path, oldObjectId);
				} else if (oldObjectId == null) {
					visitor.visitAddedFile(path, newObjectId);
				} else {
					visitor.visitChangedFile(path, oldObjectId, newObjectId);
				}
			}
		}
	}

	private void visitHeadFileSystemDelta(Repository repository,
			FileSetDeltaVisitor visitor) throws IOException, NoHeadException {
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.setRecursive(true);
		ObjectId headTreeId = repository.resolve(Constants.HEAD);
		if (headTreeId == null) {
			throw new NoHeadException("Failed to resolve HEAD");
		}
		RevWalk revWalk = new RevWalk(repository);
		RevTree revTree = revWalk.parseTree(headTreeId);

		// Using the IteratorService is important
		// to for example automatically ignore class files in bin/
		WorkingTreeIterator fileTreeIterator = IteratorService
				.createInitialIterator(repository);
		int revTreeIndex = treeWalk.addTree(revTree);
		int workTreeIndex = treeWalk.addTree(fileTreeIterator);
		treeWalk.setFilter(TreeFilter.ANY_DIFF);

		while (treeWalk.next()) {
			AbstractTreeIterator headMatch = treeWalk.getTree(revTreeIndex,
					AbstractTreeIterator.class);
			WorkingTreeIterator fileTreeMatch = treeWalk.getTree(workTreeIndex,
					WorkingTreeIterator.class);
			// TODO is this check necessary:
			if (fileTreeMatch != null) {
				if (fileTreeMatch.isEntryIgnored()) {
					continue;
				}
			}
			final String path = treeWalk.getPathString();
			ObjectId oldObjectId = null;
			if (headMatch != null) {
				oldObjectId = headMatch.getEntryObjectId();
			}
			ObjectId newObjectId = null;
			if (fileTreeMatch != null) {
				newObjectId = fileTreeMatch.getEntryObjectId();
			}
			if (newObjectId == null) {
				visitor.visitRemovedFile(path, oldObjectId);
			} else if (oldObjectId == null) {
				visitor.visitAddedFile(path, newObjectId);
			} else {
				visitor.visitChangedFile(path, oldObjectId, newObjectId);
			}
		}
	}

	@Override
	public void addSessionDataForUncommittedChanges(Object data) {
		Repository repository = repositoryRef.get();
		if (repository == null) {
			return;
		}
		HashCacluatingDeltaVisitor hashCalculator = new HashCacluatingDeltaVisitor();
		try {
			visitHeadFileSystemDelta(repository, hashCalculator);
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] currentHash = hashCalculator.buildHash();
		if (!Arrays.equals(currentHash, sessionDataDeltaHash)) {
			sessionData.clear();
		}
		sessionDataDeltaHash = currentHash;
		sessionData.add(data);
	}
}

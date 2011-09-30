package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.egit.core.IteratorService;
import org.eclipse.egit.core.project.RepositoryMapping;
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

import de.fkoeberle.autocommit.IVersionControlSystem;
import de.fkoeberle.autocommit.message.CompleteContentCommitMessageBuilder;
import de.fkoeberle.autocommit.message.ICommitMessageBuilder;

public class GitVersionControlSystemAdapter implements IVersionControlSystem {

	public GitVersionControlSystemAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void commit(String message) {
		Map<Repository,Set<IProject>> repositoryToProjectsMap = getRepositoryToProjectSetMap();
		for (Repository repository: repositoryToProjectsMap.keySet()) {
			WorkingTreeIterator workingTreeIterator = IteratorService.createInitialIterator(repository);
			try {
				IndexDiff indexDiff = new IndexDiff(repository, Constants.HEAD, workingTreeIterator);
				boolean differencesFound = indexDiff.diff(); // TODO use version with progress monitor
				if (differencesFound) {
					Git git = new Git(repository);
					Collection<String> filesToCommit = new HashSet<String>(indexDiff.getChanged());
					filesToCommit.addAll(indexDiff.getUntracked());
					filesToCommit.addAll(indexDiff.getModified());
					filesToCommit.addAll(indexDiff.getAdded());
					
					
					
					if (filesToCommit.size() > 0) {
						AddCommand addCommand = git.add();
						for (String path: filesToCommit) {
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
						for (String path: indexDiff.getMissing()) {
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
					if (message == null) {
						try {
							message = buildCommitMessage(repository);
						} catch (NoHeadException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
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
	}
	
	private String buildCommitMessage(Repository repository) throws IOException, NoHeadException {
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
		TreeFilter filter = AndTreeFilter.create(TreeFilter.ANY_DIFF, new SkipWorkTreeFilter(dirCacheTreeIndex));
		treeWalk.setFilter(filter);
		//TODO pass project array
		ICommitMessageBuilder messageBuilder = new CompleteContentCommitMessageBuilder();
		ObjectReader reader = repository.newObjectReader();
		while (treeWalk.next()) {
			AbstractTreeIterator headMatch = treeWalk.getTree(revTreeIndex,
					AbstractTreeIterator.class);
			DirCacheIterator dirCacheMatch = treeWalk.getTree(dirCacheTreeIndex,
					DirCacheIterator.class);
			IProject project = null; //TODO
			final String path = treeWalk.getPathString();
			FileContent oldContent = null;
			if (headMatch != null) {
				ObjectId objectId = headMatch.getEntryObjectId();;
				oldContent = new FileContent(objectId,reader);
			} 
			FileContent newContent = null;
			if (dirCacheMatch != null) {
				ObjectId objectId = dirCacheMatch.getEntryObjectId();;
				newContent = new FileContent(objectId,reader);
			}
			
			if (newContent == null) {
				messageBuilder.addDeletedFile(project, path, newContent);
			} else if (oldContent == null) {
				messageBuilder.addAddedFile(project, path, oldContent);
			} else {
				messageBuilder.addChangedFile(project, path, oldContent, newContent);
			}
		}
		return messageBuilder.buildMessage();
	}
	
	@Override
	public boolean noUncommittedChangesExist() {
		Map<Repository,Set<IProject>> repositoryToProjectsMap = getRepositoryToProjectSetMap();
		for (Repository repository: repositoryToProjectsMap.keySet()) {
			WorkingTreeIterator workingTreeIterator = IteratorService.createInitialIterator(repository);
			try {
				IndexDiff indexDiff = new IndexDiff(repository, Constants.HEAD, workingTreeIterator);
				if (indexDiff.diff()) {
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private Map<Repository, Set<IProject>> getRepositoryToProjectSetMap() {
		Map<Repository, Set<IProject>> map = new HashMap<Repository, Set<IProject>>();
		
		final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (IProject project : allProjects) {
			RepositoryMapping mapping = RepositoryMapping.getMapping(project);
			if (mapping != null) {
				Repository repository = mapping.getRepository();
				Set<IProject> projects = map.get(repository);
				if (projects == null) {
					projects = new HashSet<IProject>(allProjects.length);
					map.put(repository, projects);
				}
				projects.add(project);
			}
		}
		return map;
	}
}

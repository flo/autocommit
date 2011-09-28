package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.IteratorService;
import org.eclipse.egit.core.op.CommitOperation;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;

import de.fkoeberle.autocommit.IVersionControlSystem;

public class GitVersionControlSystemAdapter implements IVersionControlSystem {

	public GitVersionControlSystemAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void commit(String message) {
		final String author = "autocommit <test@example.org>";
		final String committer = "autocommit <test@example.org>";
		Map<Repository,Set<IProject>> repositoryToProjectsMap = getRepositoryToProjectSetMap();
		for (Repository repository: repositoryToProjectsMap.keySet()) {
			WorkingTreeIterator workingTreeIterator = IteratorService.createInitialIterator(repository);
			try {
				IndexDiff indexDiff = new IndexDiff(repository, Constants.HEAD, workingTreeIterator);
				boolean differencesFound = indexDiff.diff(); // TODO use version with progress monitor
				if (differencesFound) {
					Collection<String> filesToCommit = new HashSet<String>(indexDiff.getChanged());
					filesToCommit.addAll(indexDiff.getUntracked());
					filesToCommit.addAll(indexDiff.getModified());
					filesToCommit.addAll(indexDiff.getAdded());
					Collection<String> notIndexed = null;
					Collection<String> notTracked = indexDiff.getUntracked();
					CommitOperation commitOperation = new CommitOperation(
							repository, filesToCommit, notIndexed, notTracked, author, committer, message);
					IProgressMonitor commitExecuteMonitior = null;
					commitOperation.execute(commitExecuteMonitior);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private void printIndexDiff(PrintStream out, IndexDiff indexDiff) {
		out.println("Added " + indexDiff.getAdded());
		out.println("Assume Unchanged " + indexDiff.getAssumeUnchanged());
		out.println("Changed " + indexDiff.getChanged());
		out.println("Conflicting " + indexDiff.getConflicting());
		out.println("Missing " + indexDiff.getMissing());
		out.println("Modified " + indexDiff.getModified());
		out.println("Removed " + indexDiff.getRemoved());
		out.println("Untracked " + indexDiff.getUntracked());
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

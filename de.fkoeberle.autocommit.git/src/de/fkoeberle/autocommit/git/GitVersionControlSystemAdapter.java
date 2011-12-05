package de.fkoeberle.autocommit.git;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Repository;

import de.fkoeberle.autocommit.IRepository;
import de.fkoeberle.autocommit.IVersionControlSystem;
import de.fkoeberle.autocommit.Nature;

public class GitVersionControlSystemAdapter implements IVersionControlSystem {
	private final WeakHashMap<Repository, GitRepositoryAdapter> repositoryAdapterMap;

	public GitVersionControlSystemAdapter() {
		repositoryAdapterMap = new WeakHashMap<Repository, GitRepositoryAdapter>();
		// TODO Auto-generated constructor stub
	}

	private Map<IRepository, Set<IProject>> getRepositoryToProjectSetMap(
			boolean enabledOnly) {
		Map<IRepository, Set<IProject>> map = new HashMap<IRepository, Set<IProject>>();

		final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (IProject project : allProjects) {
			try {
				if (!enabledOnly || project.hasNature(Nature.ID)) {
					RepositoryMapping mapping = RepositoryMapping
							.getMapping(project);
					if (mapping != null) {
						Repository repository = mapping.getRepository();
						IRepository repositoryAdapter = getAdapterFor(repository);
						Set<IProject> projects = map.get(repositoryAdapter);
						if (projects == null) {
							projects = new HashSet<IProject>(allProjects.length);
							map.put(repositoryAdapter, projects);
						}
						projects.add(project);

					}
				}
			} catch (CoreException e) {
				Activator.logError(
						"Failed to determine repositories for autocommit", e);
			}
		}
		return map;
	}

	@Override
	public Iterator<IRepository> iterator() {
		return getRepositoryToProjectSetMap(true).keySet().iterator();
	}

	private GitRepositoryAdapter getAdapterFor(Repository repository) {
		GitRepositoryAdapter adapter = repositoryAdapterMap.get(repository);
		if (adapter == null) {
			adapter = new GitRepositoryAdapter(repository);
			repositoryAdapterMap.put(repository, adapter);
		}
		return adapter;
	}

}

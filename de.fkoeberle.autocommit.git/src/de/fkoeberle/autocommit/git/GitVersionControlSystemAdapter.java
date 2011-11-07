package de.fkoeberle.autocommit.git;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Repository;

import de.fkoeberle.autocommit.IRepository;
import de.fkoeberle.autocommit.IVersionControlSystem;

public class GitVersionControlSystemAdapter implements IVersionControlSystem {
	private final WeakHashMap<Repository, WeakReference<GitRepositoryAdapter>> repositoryAdapterMap;

	public GitVersionControlSystemAdapter() {
		repositoryAdapterMap = new WeakHashMap<Repository, WeakReference<GitRepositoryAdapter>>();
		// TODO Auto-generated constructor stub
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

	@Override
	public Iterator<IRepository> iterator() {
		List<IRepository> adapterList = new ArrayList<IRepository>();
		for (Repository repository : getRepositoryToProjectSetMap().keySet()) {
			WeakReference<GitRepositoryAdapter> ref = repositoryAdapterMap
					.get(repository);
			GitRepositoryAdapter adapter = null;
			if (ref != null) {
				adapter = ref.get();
			}
			if (adapter == null) {
				adapter = new GitRepositoryAdapter(repository);
				repositoryAdapterMap.put(repository,
						new WeakReference<GitRepositoryAdapter>(adapter));
			}
			adapterList.add(adapter);
		}
		return adapterList.iterator();
	}
}

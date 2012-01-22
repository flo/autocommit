package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Repository;

import de.fkoeberle.autocommit.IVersionControlSystem;

public class GitVersionControlSystemAdapter implements IVersionControlSystem {
	private final WeakHashMap<Repository, WeakReference<GitRepositoryAdapter>> repositoryAdapterMap;

	public GitVersionControlSystemAdapter() {
		repositoryAdapterMap = new WeakHashMap<Repository, WeakReference<GitRepositoryAdapter>>();
	}

	private GitRepositoryAdapter getAdapterFor(Repository repository) {
		WeakReference<GitRepositoryAdapter> adapterRef = repositoryAdapterMap
				.get(repository);
		GitRepositoryAdapter adapter = null;
		if (adapterRef != null) {
			adapter = adapterRef.get();
		}
		if (adapter == null) {
			adapter = new GitRepositoryAdapter(repository);
			repositoryAdapterMap.put(repository,
					new WeakReference<GitRepositoryAdapter>(adapter));
		}
		return adapter;
	}

	@Override
	public GitRepositoryAdapter getRepositoryFor(IProject project) {
		RepositoryMapping mapping = RepositoryMapping.getMapping(project);
		if (mapping == null) {
			return null;
		}
		return getAdapterFor(mapping.getRepository());
	}

	@Override
	public void prepareProjectForAutocommits(IProject project)
			throws IOException {
		GitRepositoryAdapter repositoryAdapter = getRepositoryFor(project);
		if (repositoryAdapter == null) {
			return;
		}
		repositoryAdapter.prepareForAutocommits(project);
	}
}

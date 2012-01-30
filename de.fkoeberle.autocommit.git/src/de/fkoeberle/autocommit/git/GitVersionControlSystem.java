/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.git;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Repository;

import de.fkoeberle.autocommit.IRepository;
import de.fkoeberle.autocommit.IVersionControlSystem;

public class GitVersionControlSystem implements IVersionControlSystem {
	private final WeakHashMap<Repository, WeakReference<GitRepositoryAdapter>> repositoryAdapterMap;

	public GitVersionControlSystem() {
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
	public IRepository getRepositoryFor(IProject project) {
		RepositoryMapping mapping = RepositoryMapping.getMapping(project);
		if (mapping == null) {
			return null;
		}
		return getAdapterFor(mapping.getRepository());
	}

}

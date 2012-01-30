/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.egit.core.IteratorService;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.UnmergedPathException;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.fkoeberle.autocommit.IRepository;
import de.fkoeberle.autocommit.message.CommitMessageBuilder;
import de.fkoeberle.autocommit.message.CommitMessageBuilderPluginActivator;
import de.fkoeberle.autocommit.message.ICommitMessageBuilder;
import de.fkoeberle.autocommit.message.Profile;
import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;
import de.fkoeberle.autocommit.message.ProfileReferenceXml;
import de.fkoeberle.autocommit.message.ProfileXml;

/**
 * Enhances an existing Git repository to match the interface
 * {@link IRepository}. It keeps only a weak reference to the repository, which
 * makes it possible to manage instances of this class in a WeakHashMap from
 * {@link Repository} to {@link GitRepositoryAdapter}.
 */
public class GitRepositoryAdapter implements IRepository {
	private final Repository repository;
	private final List<Object> sessionData;
	private byte[] sessionDataDeltaHash;

	public GitRepositoryAdapter(Repository repository) {
		this.repository = repository;
		this.sessionData = new ArrayList<Object>();
	}

	@Override
	public void commit() throws IOException {
		Git git = new Git(repository);
		stageForCommit(git, FilesToAdd.ADDED_OR_MODIFIED);
		stageForCommit(git, FilesToAdd.REMOVED_OR_MODIFIED);

		String message = buildCommitMessage();
		if (message != null) {
			commitStagedFiles(git, message);
		}
	}

	private void commitStagedFiles(Git git, String message)
			throws UnmergedPathException, IOException {
		CommitCommand commitCommand = git.commit();
		commitCommand.setMessage(message);

		try {
			commitCommand.call();
		} catch (GitAPIException e) {
			throw new IOException(e);
		}
	}

	private enum FilesToAdd {
		ADDED_OR_MODIFIED, REMOVED_OR_MODIFIED;
	}

	private void stageForCommit(Git git, FilesToAdd filesToAdd) {
		WorkingTreeIterator workingTreeIterator = IteratorService
				.createInitialIterator(repository);
		try {
			AddCommand addCommand = git.add();
			addCommand.addFilepattern(".");
			addCommand.setWorkingTreeIterator(workingTreeIterator);
			addCommand.setUpdate(filesToAdd == FilesToAdd.REMOVED_OR_MODIFIED);
			addCommand.call();
		} catch (NoFilepatternException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean noUncommittedChangesExist() throws IOException {
		WorkingTreeIterator workingTreeIterator = IteratorService
				.createInitialIterator(repository);

		IndexDiff indexDiff = new IndexDiff(repository, Constants.HEAD,
				workingTreeIterator);
		if (indexDiff.diff()) {
			return false;
		}

		return true;
	}

	private String buildCommitMessage() throws IOException {
		final File commitMessagesFile = getProfileFile();
		final Profile profile = CommitMessageBuilderPluginActivator
				.getProfile(commitMessagesFile);

		final ICommitMessageBuilder messageBuilder = new CommitMessageBuilder(
				profile);
		final ObjectReader reader = repository.newObjectReader();
		FileSetDeltaVisitor fileDeltaToMessageBuilderAdder = new FileDeltaToMessageBuilderAdder(
				reader, messageBuilder);
		AnyChangeDetectingDeltaVisitor anyChangeDetectingDeltaVisitor = new AnyChangeDetectingDeltaVisitor();
		if (sessionDataDeltaHash != null) {
			HashCacluatingDeltaVisitor hashCalculator = new HashCacluatingDeltaVisitor();
			visitHeadIndexDelta(fileDeltaToMessageBuilderAdder,
					anyChangeDetectingDeltaVisitor, hashCalculator);
			byte[] currentHash = hashCalculator.buildHash();
			if (Arrays.equals(sessionDataDeltaHash, currentHash)) {
				for (Object data : sessionData) {
					messageBuilder.addSessionData(data);
				}
			}
			sessionDataDeltaHash = null;
		} else {
			visitHeadIndexDelta(fileDeltaToMessageBuilderAdder,
					anyChangeDetectingDeltaVisitor);
		}
		if (anyChangeDetectingDeltaVisitor.hasDetectedChange()) {
			return messageBuilder.buildMessage();
		} else {
			return null;
		}
	}

	private File getProfileFile() {
		File repositoryDirectory = repository.getWorkTree();
		final File commitMessagesFile = new File(repositoryDirectory,
				".commitmessages");
		return commitMessagesFile;
	}

	private ObjectId objectIdOrNullOfMatch(AbstractTreeIterator match) {
		ObjectId objectId = null;
		if (match != null) {
			objectId = match.getEntryObjectId();
		}
		return objectId;
	}

	void visitDeltaWithAll(String path, AbstractTreeIterator oldTreeMatch,
			AbstractTreeIterator newTreeMatch, FileSetDeltaVisitor... visitors)
			throws IOException {
		ObjectId oldObjectId = objectIdOrNullOfMatch(oldTreeMatch);
		ObjectId newObjectId = objectIdOrNullOfMatch(newTreeMatch);
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

	private void visitHeadIndexDelta(FileSetDeltaVisitor... visitors)
			throws IOException {
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.setRecursive(true);
		ObjectId headTreeId = repository.resolve(Constants.HEAD);
		DirCache dirCache = repository.readDirCache();
		DirCacheIterator dirCacheTree = new DirCacheIterator(dirCache);
		int dirCacheTreeIndex = treeWalk.addTree(dirCacheTree);

		if (headTreeId == null) {
			while (treeWalk.next()) {
				DirCacheIterator dirCacheMatch = treeWalk.getTree(
						dirCacheTreeIndex, DirCacheIterator.class);
				final String path = treeWalk.getPathString();
				ObjectId newObjectId = dirCacheMatch.getEntryObjectId();
				for (FileSetDeltaVisitor visitor : visitors) {
					visitor.visitAddedFile(path, newObjectId);
				}
			}
		} else {
			RevWalk revWalk = new RevWalk(repository);
			RevTree revTree = revWalk.parseTree(headTreeId);
			int revTreeIndex = treeWalk.addTree(revTree);
			TreeFilter filter = AndTreeFilter.create(TreeFilter.ANY_DIFF,
					new SkipWorkTreeFilter(dirCacheTreeIndex));
			treeWalk.setFilter(filter);

			while (treeWalk.next()) {
				AbstractTreeIterator headMatch = treeWalk.getTree(revTreeIndex,
						AbstractTreeIterator.class);
				DirCacheIterator dirCacheMatch = treeWalk.getTree(
						dirCacheTreeIndex, DirCacheIterator.class);
				final String path = treeWalk.getPathString();
				visitDeltaWithAll(path, headMatch, dirCacheMatch, visitors);
			}
		}
	}

	private void visitHeadFileSystemDelta(Repository repository,
			FileSetDeltaVisitor... visitors) throws IOException {
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.setRecursive(true);
		// Using the IteratorService is important
		// to for example automatically ignore class files in bin/
		WorkingTreeIterator fileTreeIterator = IteratorService
				.createInitialIterator(repository);
		int workTreeIndex = treeWalk.addTree(fileTreeIterator);

		ObjectId headTreeId = repository.resolve(Constants.HEAD);
		if (headTreeId == null) {
			while (treeWalk.next()) {
				WorkingTreeIterator fileTreeMatch = treeWalk.getTree(
						workTreeIndex, WorkingTreeIterator.class);
				final String path = treeWalk.getPathString();
				ObjectId newObjectId = fileTreeMatch.getEntryObjectId();
				for (FileSetDeltaVisitor visitor : visitors) {
					visitor.visitAddedFile(path, newObjectId);
				}
			}
		} else {
			RevWalk revWalk = new RevWalk(repository);
			RevTree revTree = revWalk.parseTree(headTreeId);
			int revTreeIndex = treeWalk.addTree(revTree);
			treeWalk.setFilter(TreeFilter.ANY_DIFF);

			while (treeWalk.next()) {
				AbstractTreeIterator headMatch = treeWalk.getTree(revTreeIndex,
						AbstractTreeIterator.class);
				WorkingTreeIterator fileTreeMatch = treeWalk.getTree(
						workTreeIndex, WorkingTreeIterator.class);
				// TODO is this check necessary:
				if (fileTreeMatch != null) {
					if (fileTreeMatch.isEntryIgnored()) {
						continue;
					}
				}
				final String path = treeWalk.getPathString();
				visitDeltaWithAll(path, headMatch, fileTreeMatch, visitors);
			}
		}
	}

	@Override
	public void addSessionDataForUncommittedChanges(Object data)
			throws IOException {
		HashCacluatingDeltaVisitor hashCalculator = new HashCacluatingDeltaVisitor();

		visitHeadFileSystemDelta(repository, hashCalculator);

		byte[] currentHash = hashCalculator.buildHash();
		if (!Arrays.equals(currentHash, sessionDataDeltaHash)) {
			sessionData.clear();
		}
		sessionDataDeltaHash = currentHash;
		sessionData.add(data);
	}

	public void editCommitMessagesFor(IProject project) throws IOException {
		File profileFile = getProfileFile();
		if (!profileFile.exists()) {
			createInitialProfileFile(profileFile);
		}
		openEditorForProfileFile(profileFile);
	}

	private void openEditorForProfileFile(File profileFile) throws IOException {
		URI profileFileURI = profileFile.toURI();
		IFileStore fileStore = EFS.getLocalFileSystem()
				.getStore(profileFileURI);
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void prepareProjectForAutomaticCommits(IProject project)
			throws IOException {
		File profileFile = getProfileFile();
		if (!profileFile.exists()) {
			createInitialProfileFile(profileFile);
			openEditorForProfileFile(profileFile);
		}
	}

	private void createInitialProfileFile(File profileFile) throws IOException {
		ProfileReferenceXml profileReferenceXml = determineInitialProfileReferenceXml();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileXml.class,
					ProfileReferenceXml.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(profileReferenceXml, profileFile);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	private ProfileReferenceXml determineInitialProfileReferenceXml()
			throws IOException {
		Collection<ProfileIdResourceAndName> defaultProfiles = CommitMessageBuilderPluginActivator
				.getDefaultProfiles();
		ProfileIdResourceAndName first = defaultProfiles.iterator().next();
		ProfileReferenceXml profileReferenceXml = new ProfileReferenceXml();
		profileReferenceXml.setId(first.getId());
		return profileReferenceXml;
	}

}

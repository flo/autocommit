package de.fkoeberle.autocommit.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * Enhances and existing git repository to match the interface
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
	public void commit() {
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
					message = buildCommitMessage();
				} catch (NoHeadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				if (message != null) {
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
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	@Override
	public boolean noUncommittedChangesExist() {
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

	private String buildCommitMessage() throws IOException, NoHeadException {
		final File commitMessagesFile = getProfileFile();
		final Profile profile = CommitMessageBuilderPluginActivator
				.getProfile(commitMessagesFile);

		final ICommitMessageBuilder messageBuilder = new CommitMessageBuilder(
				profile);
		final ObjectReader reader = repository.newObjectReader();
		FileSetDeltaVisitor FileDeltaToMessageBuilderAdder = new FileDeltaToMessageBuilderAdder(
				reader, messageBuilder);
		if (sessionDataDeltaHash != null) {
			HashCacluatingDeltaVisitor hashCalculator = new HashCacluatingDeltaVisitor();
			visitHeadIndexDelta(FileDeltaToMessageBuilderAdder, hashCalculator);
			byte[] currentHash = hashCalculator.buildHash();
			if (Arrays.equals(sessionDataDeltaHash, currentHash)) {
				for (Object data : sessionData) {
					messageBuilder.addSessionData(data);
				}
			}
			sessionDataDeltaHash = null;
		} else {
			visitHeadIndexDelta(FileDeltaToMessageBuilderAdder);
		}
		return messageBuilder.buildMessage();
	}

	private File getProfileFile() {
		File repositoryDirectory = repository.getWorkTree();
		// TODO handle case no working tree
		final File commitMessagesFile = new File(repositoryDirectory,
				".commitmessages");
		return commitMessagesFile;
	}

	private void visitHeadIndexDelta(FileSetDeltaVisitor... visitors)
			throws IOException, NoHeadException {
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

	public void prepareForAutocommits(IProject project) throws IOException {
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
			// TODO better exception handling: e.g. message dialog
			// or other exception type
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

/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;
import de.fkoeberle.autocommit.message.ui.Model.CMFList;

public class ModelTest {

	private static final String FIVE_DUMMIES_ID = "fiveDummiesId";
	private IProject project;
	private Model model;

	@Before
	public void before() throws CoreException {
		String projectName = "test-project";
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		project = workspaceRoot.getProject(projectName);
		project.create(null);
		project.open(null);
		model = new Model();
	}

	@After
	public void after() throws CoreException {
		project.delete(false, null);
	}

	private IEditorInput createInputFromResource(String resource)
			throws CoreException {
		IFile file = project.getFile(resource);

		InputStream inputStream = getClass().getResourceAsStream(resource);
		if (inputStream == null) {
			throw new RuntimeException("Resource is missing");
		}
		boolean force = false;
		IProgressMonitor monitor = null;
		file.create(inputStream, force, monitor);
		return new FileEditorInput(file);
	}

	@Test
	public void testGetList() throws IOException, CoreException {
		IEditorInput editorInput = createInputFromResource("singleRealCMF.commitmessages");
		Model model = new Model();
		model.load(editorInput);
		WritableList usedFactories = model.getFactoryDescriptions();
		WritableList unusedFactories = model.getUnusedFactoryDescriptions();
		assertSame(usedFactories, model.getList(CMFList.USED));
		assertSame(unusedFactories, model.getList(CMFList.UNUSED));
		assertEquals(usedFactories.size(), 1);
		assertEquals(true, unusedFactories.size() > 1);
	}

	@Test
	public void testMoveFactoryByMovingAFactoryFromUnusedToTopOfUsed()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("singleRealCMF.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.UNUSED, CMFList.USED, new int[] { 0 }, 0);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(newUsedFactories.get(0), oldUnusedFactories.get(0));
			assertSame(newUsedFactories.get(1), oldUsedFactories.get(0));
			assertSame(newUnusedFactories.get(0), oldUnusedFactories.get(1));
			assertSame(newUnusedFactories.get(1), oldUnusedFactories.get(2));
			assertSame(oldUsedFactories.size() + 1, newUsedFactories.size());
			assertSame(oldUnusedFactories.size() - 1, newUnusedFactories.size());
			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingAFactoryFromUnusedToBottomOfUsed()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("singleRealCMF.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.UNUSED, CMFList.USED, new int[] { 0 }, 1);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(newUsedFactories.get(0), oldUsedFactories.get(0));
			assertSame(newUsedFactories.get(1), oldUnusedFactories.get(0));
			assertSame(newUnusedFactories.get(0), oldUnusedFactories.get(1));
			assertSame(newUnusedFactories.get(1), oldUnusedFactories.get(2));
			assertSame(oldUsedFactories.size() + 1, newUsedFactories.size());
			assertSame(oldUnusedFactories.size() - 1, newUnusedFactories.size());
			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMarkingTheOnlyFactoryAsUnused()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("singleRealCMF.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.UNUSED, new int[] { 0 }, 0);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.get(0), newUnusedFactories.get(0));
			assertSame(oldUnusedFactories.get(0), newUnusedFactories.get(1));
			assertSame(oldUsedFactories.size() - 1, newUsedFactories.size());
			assertSame(oldUnusedFactories.size() + 1, newUnusedFactories.size());
			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingFactoryAtIndex2ToIndex1()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 2 }, 1);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());

			assertSame(oldUsedFactories.get(0), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(3));
			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingFactoryAtIndex1ToIndex3()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 1 }, 3);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(4));

			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testResetCommitMessage() throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);

		Object firstObject = model.getFactoryDescriptions().get(0);
		CommitMessageFactoryDescription firstFactory = (CommitMessageFactoryDescription) firstObject;

		CommitMessageDescription commitMessageDescription = firstFactory
				.getCommitMessageDescriptions().get(0);
		String initialCurrentValue = "current value";
		String initialDefaultValue = commitMessageDescription.getDefaultValue();
		commitMessageDescription.setCurrentValue(initialCurrentValue);
		assertFalse(model.isDirty());
		ProfileIdResourceAndName initialProfileId = model.getCurrentProfile();
		assertEquals(FIVE_DUMMIES_ID, initialProfileId.getId());

		model.resetMessage(commitMessageDescription);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			assertEquals(initialDefaultValue,
					commitMessageDescription.getCurrentValue());
			assertTrue(model.isDirty());
			assertSame(Model.CUSTOM_PROFILE, model.getCurrentProfile());

			undoLastOperation();

			assertEquals(initialCurrentValue,
					commitMessageDescription.getCurrentValue());
			assertFalse(model.isDirty());
			assertSame(initialProfileId, model.getCurrentProfile());

			redoLastOperation();
		}
	}

	@Test
	public void testSetCommitMessage() throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);

		Object firstObject = model.getFactoryDescriptions().get(0);
		CommitMessageFactoryDescription firstFactory = (CommitMessageFactoryDescription) firstObject;

		CommitMessageDescription commitMessageDescription = firstFactory
				.getCommitMessageDescriptions().get(0);
		String initialCurrentValue = "current value";
		String assignedValue = "new value";
		commitMessageDescription.setCurrentValue(initialCurrentValue);
		assertFalse(model.isDirty());
		ProfileIdResourceAndName initialProfileId = model.getCurrentProfile();
		assertEquals(FIVE_DUMMIES_ID, initialProfileId.getId());

		model.setMessage(commitMessageDescription, assignedValue);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			assertEquals(assignedValue,
					commitMessageDescription.getCurrentValue());
			assertTrue(model.isDirty());
			assertSame(Model.CUSTOM_PROFILE, model.getCurrentProfile());

			undoLastOperation();

			assertEquals(initialCurrentValue,
					commitMessageDescription.getCurrentValue());
			assertFalse(model.isDirty());
			assertSame(initialProfileId, model.getCurrentProfile());

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingFactoriesAtIndex0and1ToIndex3AndSwitchToCustomProfile()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());
		assertFalse(model.isDirty());
		ProfileIdResourceAndName initialProfileId = model.getCurrentProfile();
		assertEquals(FIVE_DUMMIES_ID, initialProfileId.getId());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 0, 1 }, 3);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(4));
			assertEquals(oldUnusedFactories, newUnusedFactories);
			assertTrue(model.isDirty());
			assertSame(Model.CUSTOM_PROFILE, model.getCurrentProfile());
			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);
			assertFalse(model.isDirty());
			assertSame(initialProfileId, model.getCurrentProfile());

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingFactoriesAtIndex0and2ToIndex3()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 0, 2 }, 3);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(4));

			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingFactoriesAtIndex0and4ToIndex2()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 0, 4 }, 2);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(4));

			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingAllFactoriesAfterItself()
			throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 0, 1, 2, 3,
				4 }, 5);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(4));

			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingAllFactoriesToIndex0() throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 0, 1, 2, 3,
				4 }, 0);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(4));

			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	@Test
	public void testMoveFactoryByMovingFirstAfterLast() throws Exception {
		IEditorInput editorInput = createInputFromResource("fiveDummies.commitmessages");
		model.load(editorInput);
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUsedFactories = new ArrayList<Object>(
				model.getFactoryDescriptions());
		@SuppressWarnings("unchecked")
		ArrayList<?> oldUnusedFactories = new ArrayList<Object>(
				model.getUnusedFactoryDescriptions());

		model.moveFactories(CMFList.USED, CMFList.USED, new int[] { 0 }, 5);

		for (int undoRedoIteration = 0; undoRedoIteration < 2; undoRedoIteration++) {
			@SuppressWarnings("unchecked")
			ArrayList<?> newUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> newUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());

			assertSame(oldUsedFactories.size(), newUsedFactories.size());
			assertSame(oldUsedFactories.get(1), newUsedFactories.get(0));
			assertSame(oldUsedFactories.get(2), newUsedFactories.get(1));
			assertSame(oldUsedFactories.get(3), newUsedFactories.get(2));
			assertSame(oldUsedFactories.get(4), newUsedFactories.get(3));
			assertSame(oldUsedFactories.get(0), newUsedFactories.get(4));

			assertEquals(oldUnusedFactories, newUnusedFactories);

			assertTrue(model.isDirty());

			undoLastOperation();

			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUsedFactories = new ArrayList<Object>(
					model.getFactoryDescriptions());
			@SuppressWarnings("unchecked")
			ArrayList<?> restoredUnusedFactories = new ArrayList<Object>(
					model.getUnusedFactoryDescriptions());
			assertEquals(oldUsedFactories, restoredUsedFactories);
			assertEquals(oldUnusedFactories, restoredUnusedFactories);

			redoLastOperation();
		}
	}

	private void undoLastOperation() throws ExecutionException {
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		IUndoContext undoContext = model.getUndoContext();
		IStatus status = operationHistory.undo(undoContext, null, null);
		assertSame(status, Status.OK_STATUS);
	}

	private void redoLastOperation() throws ExecutionException {
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		IUndoContext undoContext = model.getUndoContext();
		IStatus status = operationHistory.redo(undoContext, null, null);
		assertSame(status, Status.OK_STATUS);
	}

}

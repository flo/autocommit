package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;

import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.WorkedOnPathCMF;
import de.fkoeberle.autocommit.message.ui.Model.CMFList;
import de.fkoeberle.autocommit.message.ui.Model.IDirtyPropertyListener;

public class CommitMessagesEditorPart extends EditorPart {
	private final class FactoryLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			CommitMessageFactoryDescription factoryDescription = (CommitMessageFactoryDescription) element;
			return factoryDescription.getTitle();
		}
	}

	public static final String ID = "de.fkoeberle.autocommit.message.ui.CommitMessagesEditorPart"; //$NON-NLS-1$
	private final Model model;
	private Table usedFactoriesTable;
	private Composite factoriesComposite;
	private ScrolledComposite rightComposite;
	private Table unusedFactoriesTable;

	public CommitMessagesEditorPart() {
		ArrayList<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		factories.add(new WorkedOnPathCMF());
		model = new Model();
	}

	/**
	 * Create contents of the editor part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(container, SWT.NONE);

		Composite leftComposite = new Composite(sashForm, SWT.BORDER);
		leftComposite.setLayout(new GridLayout(1, false));

		Composite leftHeader = new Composite(leftComposite, SWT.NONE);
		leftHeader.setLayout(new GridLayout(1, false));

		Label lblCommitMessageFactories = new Label(leftHeader, SWT.NONE);
		lblCommitMessageFactories.setText("Used:");

		final TableViewer usedFactoriesTableViewer = new TableViewer(
				leftComposite, SWT.BORDER | SWT.MULTI);
		usedFactoriesTable = usedFactoriesTableViewer.getTable();
		usedFactoriesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		usedFactoriesTableViewer
				.setContentProvider(new ObservableListContentProvider());
		usedFactoriesTableViewer.setLabelProvider(new FactoryLabelProvider());
		usedFactoriesTableViewer.setInput(model.getFactoryDescriptions());

		Composite middleComposite = new Composite(sashForm, SWT.BORDER);
		middleComposite.setLayout(new GridLayout(1, false));

		Composite middleHeader = new Composite(middleComposite, SWT.NONE);
		middleHeader.setLayout(new GridLayout(1, false));

		Label lblUnused = new Label(middleHeader, SWT.NONE);
		lblUnused.setText("Unused:");

		TableViewer unusedFactoriesTableViewer = new TableViewer(
				middleComposite, SWT.BORDER | SWT.MULTI);
		unusedFactoriesTable = unusedFactoriesTableViewer.getTable();
		unusedFactoriesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1));
		unusedFactoriesTableViewer
				.setContentProvider(new ObservableListContentProvider());
		unusedFactoriesTableViewer.setLabelProvider(new FactoryLabelProvider());
		unusedFactoriesTableViewer.setInput(model
				.getUnusedFactoryDescriptions());

		addDragAndDropSupport(usedFactoriesTableViewer,
				unusedFactoriesTableViewer);

		usedFactoriesTableViewer
				.addSelectionChangedListener(new FactoriesSelectionListener(
						CMFList.USED, usedFactoriesTableViewer,
						unusedFactoriesTableViewer));
		unusedFactoriesTableViewer
				.addSelectionChangedListener(new FactoriesSelectionListener(
						CMFList.UNUSED, unusedFactoriesTableViewer,
						usedFactoriesTableViewer));

		rightComposite = new ScrolledComposite(sashForm, SWT.V_SCROLL
				| SWT.BORDER);
		factoriesComposite = new Composite(rightComposite, SWT.NONE);
		rightComposite.setContent(factoriesComposite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		factoriesComposite.setLayout(layout);
		rightComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle r = rightComposite.getClientArea();
				factoriesComposite.setSize(factoriesComposite.computeSize(
						r.width, SWT.DEFAULT));
				factoriesComposite.layout();
			}
		});
		rightComposite.setAlwaysShowScrollBars(true);
		sashForm.setWeights(new int[] { 200, 200, 350 });

		model.addDirtyPropertyListener(new IDirtyPropertyListener() {

			@Override
			public void handleDirtyPropertyChange() {
				firePropertyChange(PROP_DIRTY);
			}
		});
		container.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				model.dispose();
			}
		});
	}

	private void addDragAndDropSupport(
			final TableViewer usedFactoriesTableViewer,
			TableViewer unusedFactoriesTableViewer) {
		Transfer[] transfers = new Transfer[] { UniqueIdTransfer.INSTANCE };

		Map<Long, Model.CMFList> listIdToTypeMap = new HashMap<Long, Model.CMFList>();
		Long usedListId = Long.valueOf(new Random().nextLong());
		Long unusedListId = Long.valueOf(usedListId.longValue() + 1);
		listIdToTypeMap.put(usedListId, CMFList.USED);
		listIdToTypeMap.put(unusedListId, CMFList.UNUSED);

		Map<CMFList, TableViewer> listIdToTableViewerMap = new HashMap<CMFList, TableViewer>();
		listIdToTableViewerMap.put(CMFList.USED, usedFactoriesTableViewer);
		listIdToTableViewerMap.put(CMFList.UNUSED, unusedFactoriesTableViewer);

		unusedFactoriesTableViewer.addDragSupport(DND.DROP_MOVE, transfers,
				new CMFDragSource(unusedFactoriesTableViewer, unusedListId));
		unusedFactoriesTableViewer.addDropSupport(DND.DROP_MOVE, transfers,
				new CMFDropAdapter(Model.CMFList.UNUSED, model,
						listIdToTypeMap, listIdToTableViewerMap));

		usedFactoriesTableViewer.addDragSupport(DND.DROP_MOVE, transfers,
				new CMFDragSource(usedFactoriesTableViewer, usedListId));
		usedFactoriesTableViewer.addDropSupport(DND.DROP_MOVE, transfers,
				new CMFDropAdapter(Model.CMFList.USED, model, listIdToTypeMap,
						listIdToTableViewerMap));
	}

	private final class FactoriesSelectionListener implements
			ISelectionChangedListener {
		private final CMFList listType;
		private final TableViewer tableViewer;
		private final TableViewer otherTableViewer;

		private FactoriesSelectionListener(CMFList listType,
				TableViewer tableViewer, TableViewer otherTableViewer) {
			this.listType = listType;
			this.tableViewer = tableViewer;
			this.otherTableViewer = otherTableViewer;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			int[] indices = tableViewer.getTable().getSelectionIndices();
			if (indices.length != 0) {
				setRightFactorySelection(listType, indices);
				otherTableViewer.getTable().setSelection(new int[] {});
			}
			int otherSelectionCount = otherTableViewer.getTable()
					.getSelectionCount();
			if (indices.length == 0 && otherSelectionCount == 0) {
				setRightFactorySelection(listType, new int[] {});
			}
		}
	}

	public void setRightFactorySelection(CMFList list, int[] indices) {
		for (Control child : factoriesComposite.getChildren()) {
			child.dispose();
		}
		for (int factoryIndex : indices) {
			CommitMessageFactoryDescription factory = (CommitMessageFactoryDescription) model
					.getList(list).get(factoryIndex);
			CommitMessageFactoryComposite factoryComposite = new CommitMessageFactoryComposite(
					factoriesComposite, SWT.NONE, model, factory);
			factoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
					true, false, 1, 1));
		}

		factoriesComposite.layout(true, true);
		factoriesComposite.setSize(factoriesComposite.computeSize(
				factoriesComposite.getSize().x, SWT.DEFAULT));
	}

	@Override
	public void setFocus() {
		// Set the focus

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			model.save(monitor);
		} catch (IOException e) {
			reportError(getEditorSite().getShell(), "Saving failed", e);
		}
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		try {
			model.load(input);
		} catch (IOException e) {
			reportError(this.getSite().getShell(),
					"Loading failed. See error log for details", e);
		}
		createUndoAndRedoActionHandlers(site);
	}

	@Override
	public boolean isDirty() {
		return model.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void createUndoAndRedoActionHandlers(IEditorSite site) {
		IActionBars actionBars = site.getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				new UndoActionHandler(site, model.getUndoContext()));
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				new RedoActionHandler(site, model.getUndoContext()));
	}

	public static void reportError(Shell shell, String message, Exception e) {
		MessageDialog.openError(shell, message,
				NLS.bind("{0}: See error log for details", message));
		Activator
				.getDefault()
				.getLog()
				.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK,
						message, e));
	}
}

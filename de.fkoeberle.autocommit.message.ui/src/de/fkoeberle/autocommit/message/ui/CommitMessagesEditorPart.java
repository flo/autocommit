package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;

import de.fkoeberle.autocommit.message.CommitMessageBuilderPluginActivator;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.Profile;
import de.fkoeberle.autocommit.message.ProfileDescription;
import de.fkoeberle.autocommit.message.WorkedOnPathCMF;

public class CommitMessagesEditorPart extends EditorPart {

	public static final String ID = "de.fkoeberle.autocommit.message.ui.CommitMessagesEditorPart"; //$NON-NLS-1$
	private ProfileDescription model;
	private Controller controller;
	private Table table;
	private TableViewer tableViewer;
	private Composite factoriesComposite;
	private final IUndoContext undoContext = new ObjectUndoContext(this);

	public CommitMessagesEditorPart() {
		ArrayList<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		factories.add(new WorkedOnPathCMF());
		model = null;
		controller = null;
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
		leftHeader.setLayout(new GridLayout(2, false));

		Label lblCommitMessageFactories = new Label(leftHeader, SWT.NONE);
		lblCommitMessageFactories.setText("Used Commit Message Factories:");

		Button btnAdd = new Button(leftHeader, SWT.NONE);
		btnAdd.setText("Add..");

		tableViewer = new TableViewer(leftComposite, SWT.BORDER | SWT.MULTI);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						int[] indices = tableViewer.getTable()
								.getSelectionIndices();
						controller.handleLeftFactorySelection(indices);
					}
				});
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				CommitMessageFactoryDescription factoryDescription = (CommitMessageFactoryDescription) element;
				return factoryDescription.getTitle();
			}

		});
		// init gets called first thus model is not null:
		tableViewer.setInput(model.getFactoryDescriptions());

		// TODO ScrolledComposite rightComposite = new
		// ScrolledComposite(sashForm,
		// TODO SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		// TODO rightComposite.setExpandHorizontal(true);
		// TODO rightComposite.setExpandVertical(true);
		// TODO rightComposite.setToolTipText("right");
		factoriesComposite = new Composite(sashForm, SWT.BORDER);
		// TODO rightComposite.setContent(factoriesComposite);
		factoriesComposite.setLayout(new GridLayout(1, false));
		sashForm.setWeights(new int[] { 318, 502 });
	}

	public void setRightFactorySelection(int[] indices) {
		for (Control child : factoriesComposite.getChildren()) {
			child.dispose();
		}
		for (int factoryIndex : indices) {
			CommitMessageFactoryDescription factory = (CommitMessageFactoryDescription) model
					.getFactoryDescriptions().get(factoryIndex);
			CommitMessageFactoryComposite factoryComposite = new CommitMessageFactoryComposite(
					factoriesComposite, SWT.NONE, controller, factory);
			factoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
					true, false, 1, 1));
		}
		factoriesComposite.layout(true, true);
	}

	private void createUndoAndRedoActionHandlers() {
		IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				new UndoActionHandler(this.getSite(), undoContext));
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				new RedoActionHandler(this.getSite(), undoContext));
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
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
		if (!(input instanceof IURIEditorInput)) {
			throw new PartInitException("Input type not supported");

		}
		URL url;
		try {
			url = ((IURIEditorInput) input).getURI().toURL();
		} catch (MalformedURLException e) {
			throw new PartInitException("Unble to handle input as an url", e);
		}
		try {
			Profile p = CommitMessageBuilderPluginActivator.getProfile(url);
			this.model = new ProfileDescription(p);
			this.controller = new Controller(this);
		} catch (IOException e) {
			throw new PartInitException("An IOException occured while loading",
					e);
		}
		createUndoAndRedoActionHandlers();
		// Initialize the editor part
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public ProfileDescription getProfile() {
		return model;
	}

	public IUndoContext getUndoContext() {
		return undoContext;
	}
}

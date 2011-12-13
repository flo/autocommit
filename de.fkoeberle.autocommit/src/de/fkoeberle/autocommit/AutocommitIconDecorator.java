package de.fkoeberle.autocommit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

public class AutocommitIconDecorator extends LabelProvider implements
		ILabelDecorator {

	private final IAutoCommitEnabledStateListener listener;

	public AutocommitIconDecorator() {
		listener = new IAutoCommitEnabledStateListener() {

			@Override
			public void handleEnabledStateChanged(IProject project) {
				fireLabelProviderChanged(new LabelProviderChangedEvent(
						AutocommitIconDecorator.this, project));
			}
		};
		AutoCommitPluginActivator.getDefault()
				.addAutoCommitEnabledStateListener(listener);
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decorateText(String text, Object element) {
		// TODO Auto-generated method stub
		IProject project = (IProject) Platform.getAdapterManager().getAdapter(
				element, IProject.class);
		if (project == null) {
			return null;
		}
		try {
			if (!project.hasNature(Nature.ID)) {
				return null;
			}
		} catch (CoreException e) {
			AutoCommitPluginActivator.logUnexpectedException(e);
		}
		return "[a]" + text;
	}

	@Override
	public void dispose() {
		try {
			AutoCommitPluginActivator.getDefault()
					.removeAutoCommitEnabledStateListener(listener);
		} finally {
			super.dispose();
		}
	}

}

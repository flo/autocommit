/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

public class AutocommitIconDecorator extends LabelProvider implements
		ILabelDecorator {

	private final IAutoCommitEnabledStateListener listener;
	private final ImageDescriptor icon;

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
		URL iconURL = getClass().getResource("/icon8x8.png");
		this.icon = ImageDescriptor.createFromURL(iconURL);
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		IProject project = (IProject) Platform.getAdapterManager().getAdapter(
				element, IProject.class);
		if (project == null || !project.isOpen()) {
			return null;
		}
		try {
			if (!project.hasNature(Nature.ID)) {
				return null;
			}
		} catch (CoreException e) {
			AutoCommitPluginActivator.logUnexpectedException(e);
			return null;
		}
		DecorationOverlayIcon newImageDescriptor = new DecorationOverlayIcon(
				image, new ImageDescriptor[] { icon, null, null, null, null });

		return newImageDescriptor.createImage();
	}

	@Override
	public String decorateText(String text, Object element) {
		return null;
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

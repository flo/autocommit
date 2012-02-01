/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class MessagePluginActivator extends Plugin {
	public static final String PLUGIN_ID = "de.fkoeberle.autocommit.message"; //$NON-NLS-1$
	private static MessagePluginActivator plugin;
	private ProfileManager profileManager;

	public MessagePluginActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		profileManager = new ProfileManager();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			profileManager.dispose();
		} finally {
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static MessagePluginActivator getDefault() {
		return plugin;
	}

	public static Profile getProfile(File commitMessagesFile)
			throws IOException {
		return plugin.profileManager.getProfileFor(commitMessagesFile);
	}

	public static Profile getProfile(URL url) throws IOException {
		return plugin.profileManager.getProfileFor(url);
	}

	public static ProfileDescription createProfileDescription(URL url)
			throws IOException {
		return plugin.profileManager.createProfileDescriptionFor(url);
	}

	public static Collection<CommitMessageFactoryDescription> findMissingFactories(
			ProfileDescription profileDescription) {

		return plugin.profileManager.findMissingFactories(profileDescription);
	}

	public static Collection<ProfileIdResourceAndName> getDefaultProfiles()
			throws IOException {
		return plugin.profileManager.getDefaultProfiles();
	}
}

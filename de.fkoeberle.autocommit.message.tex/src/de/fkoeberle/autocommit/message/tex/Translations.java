/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import org.eclipse.osgi.util.NLS;

public class Translations extends NLS {
	private static final String BASE_NAME = "de.fkoeberle.autocommit.message.tex.translations";

	public static String WorkedOnHeadlineCMF_workedOnChapter;
	public static String WorkedOnHeadlineCMF_workedOnSection;
	public static String WorkedOnHeadlineCMF_workedOnSubsection;
	public static String WorkedOnHeadlineCMF_workedOnSubsubsection;

	static {
		initializeMessages(BASE_NAME, Translations.class);
	}
}

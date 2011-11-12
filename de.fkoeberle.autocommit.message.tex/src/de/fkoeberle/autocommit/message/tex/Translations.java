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

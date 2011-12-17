package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class SingleChangedSectionViewTest {
	private SingleAddedSectionView create(String oldText, String newText)
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/hello.tex", oldText, newText);
		FileSetDelta fileSetDelta = builder.build();
		Session session = new Session();
		session.add(fileSetDelta);
		return session.getInstanceOf(SingleAddedSectionView.class);
	}

	@Test
	public void testAdded1InMiddle() throws IOException {
		SingleAddedSectionView view = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}");

		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertTrue(view.getAddedSectionInfo().getCharactersAddedBefore() >= 0);
		assertTrue(view.getAddedSectionInfo().getCharactersAddedBefore() <= "}"
				.length());
		assertTrue(view.getAddedSectionInfo().getCharactersAddedAfter() >= 0);
		assertTrue(view.getAddedSectionInfo().getCharactersAddedAfter() <= "\\section{"
				.length());

	}

}

package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class ProfileXmlTest {

	@Test
	public void testCreateFrom() throws IOException {
		URL resource = getClass().getResource("testcase1.commitmessages");
		ProfileXml profileXml = ProfileXml.createFrom(resource);
		assertEquals(1, profileXml.getFactories().size());
		CommitMessageFactoryXml factoryXml = profileXml.getFactories().get(0);
		assertEquals("de.fkoeberle.autocommit.message.WorkedOnPathCMF",
				factoryXml.getId());
		assertEquals(0, factoryXml.getTemplates().size());
	}
}

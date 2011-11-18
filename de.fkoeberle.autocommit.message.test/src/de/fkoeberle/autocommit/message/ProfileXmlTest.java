package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class ProfileXmlTest {

	private static final String FACTORY_1_ID = "dummy1";
	private static final String FACTORY_2_ID = "dummy2";

	@Test
	public void testCreateFrom() throws IOException {
		URL resource = getClass().getResource("testcase1.commitmessages");
		ProfileXml profileXml = ProfileXml.createFrom(resource);
		assertEquals(2, profileXml.getFactories().size());
		CommitMessageFactoryXml factoryXml1 = profileXml.getFactories().get(0);
		assertEquals(FACTORY_1_ID, factoryXml1.getId());
		assertEquals(0, factoryXml1.getTemplates().size());

		CommitMessageFactoryXml factoryXml2 = profileXml.getFactories().get(1);
		assertEquals(FACTORY_2_ID, factoryXml2.getId());
		assertEquals(2, factoryXml2.getTemplates().size());
	}

	@Test
	public void testCreateProfile() throws IOException {
		URL resource = getClass().getResource("testcase1.commitmessages");
		ProfileXml profileXml = ProfileXml.createFrom(resource);
		Profile profile = profileXml.createProfile(new DummyCMFFactory());
		assertEquals(2, profile.getFactories().size());
		ICommitMessageFactory factory1 = profile.getFactories().get(0);
		assertEquals(Dummy1CMF.class, factory1.getClass());
		ICommitMessageFactory factory2 = profile.getFactories().get(1);
		assertEquals(Dummy2CMF.class, factory2.getClass());
		Dummy2CMF dummy2 = (Dummy2CMF) factory2;
		assertEquals("value for A", dummy2.messageA.getValue());
		assertEquals("value for B", dummy2.messageB.getValue());

	}

	private final class DummyCMFFactory implements ICommitMessageFactoryFactory {
		@Override
		public ICommitMessageFactory createFactory(String id) {
			if (FACTORY_1_ID.equals(id)) {
				return new Dummy1CMF();
			}
			if (FACTORY_2_ID.equals(id)) {
				return new Dummy2CMF();
			}
			return null;
		}
	}

	private static class Dummy1CMF implements ICommitMessageFactory {

		@Override
		public String createMessage() throws IOException {
			return null;
		}

	}

	private static class Dummy2CMF implements ICommitMessageFactory {
		public final CommitMessageTemplate messageA = new CommitMessageTemplate(
				"old default value");

		public final CommitMessageTemplate messageB = new CommitMessageTemplate(
				"old default value");

		@Override
		public String createMessage() throws IOException {
			return null;
		}

	}
}

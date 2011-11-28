package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ProfileXmlTest {

	private static final String FACTORY_1_ID = "dummy1";
	private static final String FACTORY_2_ID = "dummy2";

	@Test
	public void testCreateFrom() throws IOException {
		URL resource = getClass().getResource("testcase1.commitmessages");
		ProfileXml profileXml = (ProfileXml) ProfileXml
				.loadProfileFile(resource);
		assertEquals(2, profileXml.getFactories().size());
		CommitMessageFactoryXml factoryXml1 = profileXml.getFactories().get(0);
		assertEquals(FACTORY_1_ID, factoryXml1.getId());
		assertEquals(0, factoryXml1.getTemplates().size());

		CommitMessageFactoryXml factoryXml2 = profileXml.getFactories().get(1);
		assertEquals(FACTORY_2_ID, factoryXml2.getId());
		assertEquals(2, factoryXml2.getTemplates().size());
	}

	@Test
	public void testCreateProfileDescription() throws IOException {
		URL resource = getClass().getResource("testcase1.commitmessages");
		ProfileXml profileXml = (ProfileXml) ProfileXml
				.loadProfileFile(resource);
		ProfileDescription profile = profileXml.createProfileDescription(
				new DummyCMFFactory(), "");
		assertEquals(2, profile.getFactoryDescriptions().size());
		CommitMessageFactoryDescription factory1 = profile
				.getFactoryDescriptions().get(0);
		assertEquals(Dummy1CMF.class, factory1.getFactoryClass());
		CommitMessageFactoryDescription factory2 = profile
				.getFactoryDescriptions().get(1);
		assertEquals(Dummy2CMF.class, factory2.getFactoryClass());
		List<CommitMessageDescription> factory2MessageDescriptions = factory2
				.getCommitMessageDescriptions();
		assertEquals(2, factory2MessageDescriptions.size());
		CommitMessageDescription messageA = factory2MessageDescriptions.get(0);
		assertEquals("value for A", messageA.getCurrentValue());
		CommitMessageDescription messageB = factory2MessageDescriptions.get(1);
		assertEquals("value for B", messageB.getCurrentValue());

	}

	private final class DummyCMFFactory implements ICMFDescriptionFactory {
		@Override
		public CommitMessageFactoryDescription createFactoryDescription(
				String id) {
			if (FACTORY_1_ID.equals(id)) {
				return new CommitMessageFactoryDescription(Dummy1CMF.class, "",
						Arrays.asList(""),
						new ArrayList<CommitMessageDescription>());
			}
			if (FACTORY_2_ID.equals(id)) {
				ArrayList<CommitMessageDescription> messageDescriptions = new ArrayList<CommitMessageDescription>();
				try {
					messageDescriptions.add(new CommitMessageDescription(
							Dummy2CMF.class.getField("messageA"),
							"message a default", "value for A"));
					messageDescriptions.add(new CommitMessageDescription(
							Dummy2CMF.class.getField("messageB"),
							"message a default", "value for B"));
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				} catch (NoSuchFieldException e) {
					throw new RuntimeException(e);
				}

				return new CommitMessageFactoryDescription(Dummy2CMF.class, "",
						Arrays.asList(""), messageDescriptions);
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
		// fields are obtained via reflection:
		@SuppressWarnings("unused")
		public final CommitMessageTemplate messageA = new CommitMessageTemplate(
				"old default value");
		@SuppressWarnings("unused")
		public final CommitMessageTemplate messageB = new CommitMessageTemplate(
				"old default value");

		@Override
		public String createMessage() throws IOException {
			return null;
		}

	}
}

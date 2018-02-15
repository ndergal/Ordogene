package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Const;

/**
 * Unit test for simple App.
 */
public class FileServiceTest {

	@Before
	public void init() throws URISyntaxException {
 		String configFileLocation = ModelTest.class.getClassLoader().getResource("ordogene.conf.json").toURI().toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
	}

	@Test
	public void noUserTest() {
		FileService fs = new FileService();
		assertFalse(fs.userExist("edsvinubb"));
	}

	@Test
	public void createUserTest() {
		FileService fs = new FileService();
		if (fs.userExist("bwana"))
			fs.removeUser("bwana");
		assertTrue(fs.addUser("bwana"));
		fs.removeUser("bwana");
	}

	@Test
	public void removesUserTest() {
		FileService fs = new FileService();
		if (!fs.userExist("edsvinubb"))
			fs.addUser("bwana");

		assertTrue(fs.removeUser("bwana"));
	}
	

	@Test
	public void getRandomUid() {
		FileService fs = new FileService();
		int size=89;
		String ruid = fs.generateRandomUserId(size);
		assertFalse(ruid==null);
		assertEquals(size,ruid.length());
	}
}

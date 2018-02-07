package org.ordogene.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
public class FileServiceTest {

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
}

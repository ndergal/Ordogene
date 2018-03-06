package org.ordogene.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Const;

/**
 * Unit test for simple App.
 */
public class UserManagerTest {

	@Before
	public void init() {
		Const.loadConfig("./src/test/resources/ordogene.conf.json");
	}
	
	@Test
	public void noUserTest() {
		UserManager uh = new UserManager();
		assertFalse(uh.checkUserExists("edsvinubb"));
	}

	@Test
	public void createUserTest() {
		UserManager uh = new UserManager();
		if (uh.checkUserExists("bwana"))
			uh.removeUser("bwana");
		assertTrue(uh.createUser("bwana"));
		uh.removeUser("bwana");
	}

	@Test
	public void removesUserTest() {
		UserManager uh = new UserManager();
		if (!uh.checkUserExists("edsvinubb"))
			uh.createUser("bwana");

		assertTrue(uh.removeUser("bwana"));
	}
}

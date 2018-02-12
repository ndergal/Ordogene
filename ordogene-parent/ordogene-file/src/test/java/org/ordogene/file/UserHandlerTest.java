package org.ordogene.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Const;

/**
 * Unit test for simple App.
 */
public class UserHandlerTest {

	@Before
	public void init() {
		Const.loadConfig("./src/test/resources/ordogene.conf.json");
	}
	
	@Test
	public void noUserTest() {
		UserHandler uh = new UserHandler();
		assertFalse(uh.checkUserExists("edsvinubb"));
	}

	@Test
	public void createUserTest() {
		UserHandler uh = new UserHandler();
		if (uh.checkUserExists("bwana"))
			uh.removeUser("bwana");
		assertTrue(uh.createAnUser("bwana"));
		uh.removeUser("bwana");
	}

	@Test
	public void removesUserTest() {
		UserHandler uh = new UserHandler();
		if (!uh.checkUserExists("edsvinubb"))
			uh.createAnUser("bwana");

		assertTrue(uh.removeUser("bwana"));
	}
}

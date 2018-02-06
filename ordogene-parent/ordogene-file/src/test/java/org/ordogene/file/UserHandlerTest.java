package org.ordogene.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
public class UserHandlerTest {

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

package org.ordogene.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
public class ConstTest {

	@Test
	public void loadConstTest() {
		assertTrue(Const.getConst().size()>0);
	}
}

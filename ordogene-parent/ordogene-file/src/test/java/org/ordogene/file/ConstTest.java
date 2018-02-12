package org.ordogene.file;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Const;

/**
 * Unit test for simple App.
 */
public class ConstTest {

	@Before
	public void init() {
		Const.loadConfig("./src/test/resources/ordogene.conf.json");
	}
	
	@Test
	public void loadConstTest() {
		assertTrue(Const.getConst().size()>0);
	}
}

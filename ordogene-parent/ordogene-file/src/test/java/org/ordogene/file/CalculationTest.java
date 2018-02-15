package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

public class CalculationTest {

	@Before
	public void init() {
		Const.loadConfig("./src/test/resources/ordogene.conf.json");
	}

	@Test
	public void gettersSettersTest() {
		Calculation c = new Calculation();
		int id = 000354;
		long ts = System.currentTimeMillis();
		String uid = "seigneur yvain";
		Date now = new Date(ts);
		int fitness = 35;
		int iter = 1324325;
		int lastIter = 124444;
		boolean run = true;
		c.setId(id);
		c.setDate(now);
		c.setFitnessSaved(fitness);
		c.setLastIterationSaved(lastIter);
		c.setMaxIteration(iter);
		assertTrue((c.getDate() != null));
		assertTrue(ts == (c.getStartTimestamp()));
		c.setStartTimestamp(ts);
		assertTrue(ts == (c.getStartTimestamp()));
		assertEquals(id, c.getId());
		assertEquals(lastIter, c.getLastIterationSaved());
		assertEquals(iter, c.getMaxIteration());
		assertEquals(fitness, c.getFitnessSaved());

	}
}

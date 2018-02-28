package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		int fitness = 35;
		int iter = 1324325;
		int iterMax = 1324326;
		int lastIter = 124444;
		String name = "calculation test";
		c.setId(id);
 		c.setFitnessSaved(fitness);
		c.setLastIterationSaved(lastIter);
		c.setMaxIteration(iterMax);
		c.setStartTimestamp(ts);
		c.setIterationNumber(iter);
		c.setName(name);
		c.setRunning(true);
		assertTrue(ts == (c.getStartTimestamp()));
		assertEquals(id, c.getId());
		assertEquals(lastIter, c.getLastIterationSaved());
		assertEquals(iterMax, c.getMaxIteration());
		assertEquals(fitness, c.getFitnessSaved());
		assertEquals(name, c.getName());
		assertEquals(iter, c.getIterationNumber());
		assertTrue(c.isRunning());

	}
}

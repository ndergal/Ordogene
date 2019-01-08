package org.ordogene.file.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

public class CalculationTest {

	@Test
	public void testGet() {
		 Method[] methods = Calculation.class.getDeclaredMethods();
		 Arrays.stream(methods)
		 	.filter(m -> m.getName().contains("get"))
		 	.forEach(m -> {
				try {
					m.invoke(Calculation.class.newInstance());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			});
	}
	
	@Test
	public void testHashCodeTrue() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		Calculation cprime = new Calculation();
		cprime.setCalculation(0, 0, 0, 0, 0, "c", 0);
		assertEquals(c.hashCode(), cprime.hashCode());
	}
	
	@Test
	public void testHashCodeFalse() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		Calculation cprime = new Calculation();
		cprime.setCalculation(1, 1, 1, 1, 1, "cprime", 1);
		assertNotEquals(c.hashCode(), cprime.hashCode());
	}
	
	@Test
	public void testEqualsThis() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		assertTrue(c.equals(c));
	}
	
	@Test
	public void testEqualsNull() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		assertFalse(c.equals(null));
	}
	
	@Test
	public void testEqualsInstanceOf() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		assertFalse(c.equals(new Object()));
	}
	
	@Test
	public void testEqualsFalse() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		Calculation cprime = new Calculation();
		cprime.setCalculation(1, 1, 1, 1, 1, "cprime", 1);
		assertFalse(c.equals(cprime));
	}
	
	@Test
	public void testEqualsTrue() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "c", 0);
		Calculation cprime = new Calculation();
		cprime.setCalculation(0, 0, 0, 0, 0, "c", 0);
		assertTrue(c.equals(cprime));
	}
	
	@Test
	public void testIsRunning() {
		Calculation c = new Calculation();
		c.setRunning(true);
		assertTrue(c.isRunning());
	}
	
	@Test
	public void testIsValidFalse() {
		Calculation c = new Calculation();
		assertFalse(c.isValid());
	}
	
	@Test
	public void testIsValidTrue() {
		Calculation c = new Calculation();
		c.setName("canard");
		assertTrue(c.isValid());
	}
	
	@Test
	public void testToString() {
		Calculation c = new Calculation();
		c.setCalculation(0, 0, 0, 0, 0, "test", 0);
		c.setRunning(true);
		String expected = "Calculation [id=0, name=test, running=true, iterationNumber=0, maxIteration=0, fitnessSaved=0, lastIterationSaved=0, startTimestamp=0]";
		assertEquals(expected, c.toString());
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetNameNull() {
		Calculation c = new Calculation();
		c.setName(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStartTimestamp() {
		Calculation c = new Calculation();
		c.setStartTimestamp(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetIterationNumberNegative() {
		Calculation c = new Calculation();
		c.setIterationNumber(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetMaxIterationNegative() {
		Calculation c = new Calculation();
		c.setMaxIteration(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetLastIterationSavedNegative() {
		Calculation c = new Calculation();
		c.setLastIterationSaved(-1);
	}
}

package org.ordogene.algorithme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.ordogene.file.models.JSONEntity;

public class EntityTest {

	@Test
	public void test_creation() {
		new Entity("name", 0);
	}
	
	@Test(expected=NullPointerException.class)
	public void test_creation_null_name() {
		new Entity(null, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_creation_negative_quantity() {
		new Entity("name", -1);
	}
	
	@Test
	public void test_creation_from_JSONEntity() {
		JSONEntity je = new JSONEntity();
		je.setName("name");
		je.setQuantity(0);
		Entity.createEntity(je);
	}
	
	@Test
	public void test_getQuantity() {
		Entity e = new Entity("name", 0);
		assertEquals(0, e.getQuantity());
	}
	
	@Test
	public void test_setQuantity() {
		Entity e = new Entity("name", 0);
		e.setQuantity(3);
		assertEquals(3, e.getQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_setQuantity_negative_value() {
		Entity e = new Entity("name", 0);
		e.setQuantity(-3);
	}
	
	@Test
	public void test_getName() {
		Entity e = new Entity("name", 0);
		assertEquals("name", e.getName());
	}
	
	@Test
	public void test_addQuantity1() {
		Entity e = new Entity("name", 50);
		e.addQuantity(12);
		assertEquals(62, e.getQuantity());
	}
	
	@Test
	public void test_addQuantity2() {
		Entity e = new Entity("name", 50);
		e.addQuantity(-12);
		assertEquals(38, e.getQuantity());
	}
	
	@Test
	public void test_addQuantity3() {
		Entity e = new Entity("name", 50);
		e.addQuantity(-50);
		assertEquals(0, e.getQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_addQuantity_negative_result() {
		Entity e = new Entity("name", 50);
		e.addQuantity(-51);
	}
}

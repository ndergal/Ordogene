package org.ordogene.algorithme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		JSONEntity je = mock(JSONEntity.class);
		
		when(je.getName()).thenReturn("name");
		when(je.getQuantity()).thenReturn(0);
		
		Entity.createEntity(je);
	}
	
	@Test(expected=NullPointerException.class)
	public void test_creation_from_null_JSONEntity() {
		Entity.createEntity(null);
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
	
	@Test
	public void test_equals1() {
		Entity e1 = new Entity("name", 50);
		Entity e2 = new Entity("name", 50);
		assertTrue(e1.equals(e2));
	}
	
	@Test
	public void test_equals2() {
		Entity e1 = new Entity("name", 50);
		assertFalse(e1.equals(null));
	}
	
	@Test
	public void test_equals3() {
		Entity e = new Entity("name", 50);
		Object o = new Object();
		assertFalse(e.equals(o));
	}
	
	@Test
	public void test_equals4() {
		Entity e1 = new Entity("name", 50);
		Entity e2 = new Entity("name", 30);
		assertFalse(e1.equals(e2));
	}
	
	@Test
	public void test_equals5() {
		Entity e1 = new Entity("name1", 50);
		Entity e2 = new Entity("name2", 50);
		assertFalse(e1.equals(e2));
	}
	
	@Test
	public void test_equals6() {
		Entity e1 = new Entity("name1", 50);
		assertTrue(e1.equals(e1));
	}
	
	@Test
	public void test_hashcode1() {
		Entity e1 = new Entity("name", 50);
		Entity e2 = new Entity("name", 50);
		assertTrue(e1.hashCode() == e2.hashCode());
	}
	
	@Test
	public void test_hashcode2() {
		Entity e1 = new Entity("name", 50);
		Entity e2 = new Entity("name", 30);
		assertFalse(e1.hashCode() == e2.hashCode());
	}
	
	@Test
	public void test_hashcode3() {
		Entity e1 = new Entity("name1", 50);
		Entity e2 = new Entity("name2", 50);
		assertFalse(e1.hashCode() == e2.hashCode());
	}
}

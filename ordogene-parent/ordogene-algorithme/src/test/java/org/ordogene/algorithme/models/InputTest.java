package org.ordogene.algorithme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.ordogene.file.models.JSONInput;
import org.ordogene.file.models.Relation;

public class InputTest {
	
	@Test
	public void test_creation() {
		new Input("name", 0, Relation.c);
	}
	
	@Test(expected=NullPointerException.class)
	public void test_creation_with_null_name() {
		new Input(null, 0, Relation.c);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_creation_with_negative_quantity() {
		new Input("name", -1, Relation.c);
	}

	@Test
	public void test_creation_from_JSONInput() {
		JSONInput ji = mock(JSONInput.class);
		
		when(ji.getName()).thenReturn("name");
		when(ji.getQuantity()).thenReturn(0);
		when(ji.getRelation()).thenReturn(Relation.c);
		
		Input.createInput(ji);
	}
	
	@Test(expected=NullPointerException.class)
	public void test_creation_from_null_JSONInput() {
		Input.createInput(null);
	}

	@Test
	public void test_getRelation() {
		Input i = new Input("name", 0, Relation.c);
		assertEquals(Relation.c, i.getRelation());
	}

	@Test
	public void test_equals1() {
		Input i1 = new Input("name", 0, Relation.c);
		Input i2 = new Input("name", 0, Relation.c);
		assertTrue(i1.equals(i2));
	}
	
	@Test
	public void test_equals2() {
		Input i = new Input("name", 0, Relation.c);
		assertFalse(i.equals(null));
	}
	
	@Test
	public void test_equals3() {
		Input i = new Input("name", 0, Relation.c);
		Object o = new Object();
		assertFalse(i.equals(o));
	}
	
	@Test
	public void test_equals4() {
		Input i1 = new Input("name", 0, Relation.c);
		Input i2 = new Input("name", 50, Relation.c);
		assertFalse(i1.equals(i2));
	}
	
	@Test
	public void test_equals5() {
		Input i1 = new Input("name1", 0, Relation.c);
		Input i2 = new Input("name2", 0, Relation.c);
		assertFalse(i1.equals(i2));
	}

	@Test
	public void test_equals6() {
		Input i1 = new Input("name1", 0, Relation.c);
		Input i2 = new Input("name2", 0, Relation.p);
		assertFalse(i1.equals(i2));
	}
	
	@Test
	public void test_hashcode1() {
		Input i1 = new Input("name", 0, Relation.c);
		Input i2 = new Input("name", 0, Relation.c);
		assertTrue(i1.hashCode() == i2.hashCode());
	}
	
	@Test
	public void test_hashcode2() {
		Input i1 = new Input("name1", 0, Relation.c);
		Input i2 = new Input("name2", 0, Relation.c);
		assertFalse(i1.hashCode() == i2.hashCode());
	}
	
	@Test
	public void test_hashcode3() {
		Input i1 = new Input("name", 0, Relation.c);
		Input i2 = new Input("name", 50, Relation.c);
		assertFalse(i1.hashCode() == i2.hashCode());
	}
	
	@Test
	public void test_hashcode4() {
		Input i1 = new Input("name", 0, Relation.c);
		Input i2 = new Input("name", 0, Relation.p);
		assertFalse(i1.hashCode() == i2.hashCode());
	}

}

package org.ordogene.algorithme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.ordogene.file.models.JSONAction;
import org.ordogene.file.models.Relation;

import edu.emory.mathcs.backport.java.util.Collections;

public class ActionTest {

	@Test
	public void testEMPTY() {
		Action.EMPTY(1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEMPTY_zero_time() {
		Action.EMPTY(0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEMPTY_negative_time() {
		Action.EMPTY(-1);
	}

	@Test
	public void testAction() {
		new Action("name", 1, Collections.emptySet(), Collections.emptySet());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAction_zero_time() {
		new Action("name", 0, Collections.emptySet(), Collections.emptySet());
	}

	@Test(expected=NullPointerException.class)
	public void testAction_null_name() {
		new Action(null, 1, Collections.emptySet(), Collections.emptySet());
	}

	@Test(expected=NullPointerException.class)
	public void testAction_null_input() {
		new Action("name", 1, null, Collections.emptySet());
	}

	@Test(expected=NullPointerException.class)
	public void testAction_null_output() {
		new Action("name", 1, Collections.emptySet(), null);
	}

	@Test(expected=NullPointerException.class)
	public void testAction_null_element_output() {
		HashSet<Entity> outputs = new HashSet<>();
		outputs.add(null);
		
		new Action("name", 1, Collections.emptySet(), outputs);
	}

	@Test(expected=NullPointerException.class)
	public void testAction_null_element_input() {
		HashSet<Input> inputs = new HashSet<>();
		inputs.add(null);
		
		new Action("name", 1, inputs, Collections.emptySet());
	}

	@Test
	public void testCreateAction() {
		JSONAction ja = mock(JSONAction.class);
		
		when(ja.getName()).thenReturn("name");
		when(ja.getTime()).thenReturn(1);
		when(ja.getInput()).thenReturn(Collections.emptyList());
		when(ja.getOutput()).thenReturn(Collections.emptyList());
		
		Action.createAction(ja);
	}

	@Test
	public void testGetName() {
		Action a = new Action("name", 1, Collections.emptySet(), Collections.emptySet());
		assertEquals("name", a.getName());
	}

	@Test
	public void testGetTime() {
		Action a = new Action("name", 1, Collections.emptySet(), Collections.emptySet());
		assertEquals(1, a.getTime());
	}

	@Test
	public void testGetInputs() {
		HashSet<Input> inputs = new HashSet<>();
		
		Input i = mock(Input.class);
		
		when(i.getName()).thenReturn("input");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		inputs.add(i);
		
		Action a = new Action("name", 1, inputs, Collections.emptySet());
		assertEquals(inputs, a.getInputs());
	}

	@Test
	public void testGetOutputs() {
		HashSet<Entity> outputs = new HashSet<>();
		Entity e = mock(Entity.class);
		
		when(e.getName()).thenReturn("output");
		when(e.getQuantity()).thenReturn(1);
		
		outputs.add(e);
		
		Action a = new Action("name", 1, Collections.emptySet(), outputs);
		assertEquals(outputs, a.getOutputs());
	}

	@Test
	public void testHashCode1() {
		HashSet<Entity> outputs = new HashSet<>();
		HashSet<Input> inputs = new HashSet<>();
		Entity e = mock(Entity.class);
		Input i = mock(Input.class);
		
		when(e.getName()).thenReturn("output");
		when(e.getQuantity()).thenReturn(1);
		
		when(i.getName()).thenReturn("input");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		outputs.add(e);
		inputs.add(i);
		
		Action a1 = new Action("name", 1, inputs, outputs);
		Action a2 = new Action("name", 1, inputs, outputs);
		
		assertTrue(a1.hashCode() == a2.hashCode());
	}

	@Test
	public void testHashCode2() {
		HashSet<Entity> outputs1 = new HashSet<>();
		HashSet<Entity> outputs2 = new HashSet<>();
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("output");
		when(e1.getQuantity()).thenReturn(1);

		when(e2.getName()).thenReturn("output");
		when(e2.getQuantity()).thenReturn(1);
		
		outputs1.add(e1);
		outputs2.add(e2);
		
		Action a1 = new Action("name", 1, Collections.emptySet(), outputs1);
		Action a2 = new Action("name", 1, Collections.emptySet(), outputs2);
		
		assertFalse(a1.hashCode() == a2.hashCode());
	}

	@Test
	public void testHashCode3() {
		HashSet<Input> inputs1 = new HashSet<>();
		HashSet<Input> inputs2 = new HashSet<>();
		Input i1 = mock(Input.class);
		Input i2 = mock(Input.class);

		when(i1.getName()).thenReturn("input1");
		when(i1.getQuantity()).thenReturn(1);
		when(i1.getRelation()).thenReturn(Relation.c);
		
		when(i2.getName()).thenReturn("input2");
		when(i2.getQuantity()).thenReturn(1);
		when(i2.getRelation()).thenReturn(Relation.p);
		
		inputs1.add(i1);
		inputs2.add(i2);
		
		Action a1 = new Action("name", 1, inputs1, Collections.emptySet());
		Action a2 = new Action("name", 1, inputs2, Collections.emptySet());
		
		assertFalse(a1.hashCode() == a2.hashCode());
	}

	@Test
	public void testHashCode4() {
		Action a1 = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		Action a2 = new Action("name", 1, Collections.emptySet(), Collections.emptySet());
		
		assertFalse(a1.hashCode() == a2.hashCode());
	}

	@Test
	public void testHashCode5() {
		Action a1 = new Action("name1", 2, Collections.emptySet(), Collections.emptySet());
		Action a2 = new Action("name2", 2, Collections.emptySet(), Collections.emptySet());
		
		assertFalse(a1.hashCode() == a2.hashCode());
	}

	@Test
	public void testEqualsObject1() {
		Action a = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		
		assertTrue(a.equals(a));
	}

	@Test
	public void testEqualsObject2() {
		Action a = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		
		assertFalse(a.equals(null));
	}

	@Test
	public void testEqualsObject3() {
		Action a = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		
		assertFalse(a.equals(new Object()));
	}

	@Test
	public void testEqualsObject4() {
		Action a1 = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		Action a2 = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		
		assertTrue(a1.equals(a2));
	}

	@Test
	public void testEqualsObject5() {
		Action a1 = new Action("name1", 2, Collections.emptySet(), Collections.emptySet());
		Action a2 = new Action("name2", 2, Collections.emptySet(), Collections.emptySet());
		
		assertFalse(a1.equals(a2));
	}

	@Test
	public void testEqualsObject6() {
		Action a1 = new Action("name", 1, Collections.emptySet(), Collections.emptySet());
		Action a2 = new Action("name", 2, Collections.emptySet(), Collections.emptySet());
		
		assertFalse(a1.equals(a2));
	}

	@Test
	public void testEqualsObject7() {
		HashSet<Entity> outputs1 = new HashSet<>();
		HashSet<Entity> outputs2 = new HashSet<>();
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("output1");
		when(e1.getQuantity()).thenReturn(1);

		when(e2.getName()).thenReturn("output2");
		when(e2.getQuantity()).thenReturn(1);
		
		outputs1.add(e1);
		outputs2.add(e2);
		
		Action a1 = new Action("name", 1, Collections.emptySet(), outputs1);
		Action a2 = new Action("name", 1, Collections.emptySet(), outputs2);
		
		assertFalse(a1.equals(a2));
	}

	@Test
	public void testEqualsObject8() {
		HashSet<Input> inputs1 = new HashSet<>();
		HashSet<Input> inputs2 = new HashSet<>();
		Input i1 = mock(Input.class);
		Input i2 = mock(Input.class);
		
		when(i1.getName()).thenReturn("output1");
		when(i1.getQuantity()).thenReturn(1);
		when(i1.getRelation()).thenReturn(Relation.c);

		when(i2.getName()).thenReturn("output2");
		when(i2.getQuantity()).thenReturn(1);
		when(i2.getRelation()).thenReturn(Relation.c);
		
		inputs1.add(i1);
		inputs2.add(i2);
		
		Action a1 = new Action("name", 1, inputs1, Collections.emptySet());
		Action a2 = new Action("name", 1, inputs2, Collections.emptySet());
		
		assertFalse(a1.equals(a2));
	}

}

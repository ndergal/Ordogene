package org.ordogene.algorithme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.models.JSONOperand;
import org.ordogene.file.models.Type;

public class FitnessTest {

	@Test
	public void test_fitness_creation() {
		HashMap<String, Long> op = new HashMap<>();
		op.put("name", Long.valueOf(1));
		
		new Fitness(Type.max, op);
	}

	@Test(expected=NullPointerException.class)
	public void test_fitness_creation_null_type() {
		HashMap<String, Long> op = new HashMap<>();
		op.put("name", Long.valueOf(1));
		
		new Fitness(null, op);
	}

	@Test(expected=NullPointerException.class)
	public void test_fitness_creation_null_operands() {
		new Fitness(Type.max, null);
	}

	@Test(expected=NullPointerException.class)
	public void test_fitness_creation_null_name_in_operands() {
		HashMap<String, Long> op = new HashMap<>();
		op.put(null, Long.valueOf(1));
		
		new Fitness(Type.max, op);
	}

	@Test(expected=NullPointerException.class)
	public void test_fitness_creation_null_coef_in_operands() {
		HashMap<String, Long> op = new HashMap<>();
		op.put("name", null);
		
		new Fitness(Type.max, op);
	}

	@Test
	public void test_createFitness_from_JSONFitness() {
		JSONFitness jf = mock(JSONFitness.class);
		
		JSONOperand jo1 = mock(JSONOperand.class);
		when(jo1.getName()).thenReturn("1");
		when(jo1.getCoef()).thenReturn(Long.valueOf(1));
		
		List<JSONOperand> operands = new ArrayList<>();
		operands.add(jo1);
		
		when(jf.getOperands()).thenReturn(operands);
		when(jf.getType()).thenReturn(Type.min);
		
		Fitness.createFitness(jf);
	}

	@Test(expected=NullPointerException.class)
	public void test_createFitness_from_null_JSONFitness() {
		Fitness.createFitness(null);
	}

	@Test(expected=NullPointerException.class)
	public void test_createFitness_from_JSONFitness_null_type() {
		JSONFitness jf = mock(JSONFitness.class);
		
		JSONOperand jo1 = mock(JSONOperand.class);
		when(jo1.getName()).thenReturn("1");
		when(jo1.getCoef()).thenReturn(Long.valueOf(1));
		
		List<JSONOperand> operands = new ArrayList<>();
		operands.add(jo1);
		
		when(jf.getOperands()).thenReturn(operands);
		when(jf.getType()).thenReturn(null);
		
		Fitness.createFitness(jf);
	}

	@Test(expected=NullPointerException.class)
	public void test_createFitness_from_JSONFitness_null_op() {
		JSONFitness jf = mock(JSONFitness.class);
		
		List<JSONOperand> operands = new ArrayList<>();
		operands.add(null);
		
		when(jf.getOperands()).thenReturn(operands);
		when(jf.getType()).thenReturn(Type.max);
		
		Fitness.createFitness(jf);
	}

	@Test
	public void testEval() {
		Action a = mock(Action.class);
		Input i1 = mock(Input.class);
		Entity o1 = mock(Entity.class);
		
		HashSet<Input> inputs = new HashSet<>();
		inputs.add(i1);
		HashSet<Entity> outputs = new HashSet<>();
		outputs.add(o1);
		
		when(i1.getName()).thenReturn("i1");
		when(i1.getQuantity()).thenReturn(1);
		when(o1.getName()).thenReturn("o1");
		when(o1.getQuantity()).thenReturn(1);
		
		when(a.getInputs()).thenReturn(inputs);
		when(a.getOutputs()).thenReturn(outputs);
		
		HashMap<String, Long> op = new HashMap<>();
		op.put("i1", Long.valueOf(1));
		op.put("o1", Long.valueOf(10));
		
		Fitness f = new Fitness(Type.max, op);
		
		assertEquals(9, f.eval(a));
	}

	@Test(expected=NullPointerException.class)
	public void testEval_null_action() {		
		HashMap<String, Long> op = new HashMap<>();
		op.put("i1", Long.valueOf(1));
		op.put("o1", Long.valueOf(10));
		
		Fitness f = new Fitness(Type.max, op);
		
		f.eval(null);
	}

	@Test(expected=NullPointerException.class)
	public void testEval_null_input_element() {
		Action a = mock(Action.class);
		Entity o1 = mock(Entity.class);
		
		HashSet<Input> inputs = new HashSet<>();
		inputs.add(null);
		HashSet<Entity> outputs = new HashSet<>();
		outputs.add(o1);
		
		when(o1.getName()).thenReturn("o1");
		when(o1.getQuantity()).thenReturn(1);
		
		when(a.getInputs()).thenReturn(inputs);
		when(a.getOutputs()).thenReturn(outputs);
		
		HashMap<String, Long> op = new HashMap<>();
		op.put("i1", Long.valueOf(1));
		op.put("o1", Long.valueOf(10));
		
		Fitness f = new Fitness(Type.max, op);
		
		f.eval(a);
	}

	@Test(expected=NullPointerException.class)
	public void testEval_null_output_element() {
		Action a = mock(Action.class);
		Input i1 = mock(Input.class);
		
		HashSet<Input> inputs = new HashSet<>();
		inputs.add(i1);
		HashSet<Entity> outputs = new HashSet<>();
		outputs.add(null);
		
		when(i1.getName()).thenReturn("i1");
		when(i1.getQuantity()).thenReturn(1);
		
		when(a.getInputs()).thenReturn(inputs);
		when(a.getOutputs()).thenReturn(outputs);
		
		HashMap<String, Long> op = new HashMap<>();
		op.put("i1", Long.valueOf(1));
		op.put("o1", Long.valueOf(10));
		
		Fitness f = new Fitness(Type.max, op);
		
		f.eval(a);
	}

	@Test
	public void testHashCode1() {
		HashMap<String, Long> op = new HashMap<>();
		op.put("i1", Long.valueOf(1));
		op.put("o1", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op);
		Fitness f2 = new Fitness(Type.max, op);
		
		assertTrue(f1.hashCode() == f2.hashCode());
	}

	@Test
	public void testHashCode2() {
		HashMap<String, Long> op = new HashMap<>();
		op.put("i1", Long.valueOf(1));
		op.put("o1", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op);
		Fitness f2 = new Fitness(Type.min, op);
		
		assertFalse(f1.hashCode() == f2.hashCode());
	}

	@Test
	public void testHashCode3() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		HashMap<String, Long> op2 = new HashMap<>();
		op2.put("i", Long.valueOf(1));
		op2.put("o1", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		Fitness f2 = new Fitness(Type.max, op2);
		
		assertFalse(f1.hashCode() == f2.hashCode());
	}

	@Test
	public void testHashCode4() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		HashMap<String, Long> op2 = new HashMap<>();
		op2.put("i", Long.valueOf(1));
		op2.put("o", Long.valueOf(1));
		
		Fitness f1 = new Fitness(Type.max, op1);
		Fitness f2 = new Fitness(Type.max, op2);
		
		assertFalse(f1.hashCode() == f2.hashCode());
	}

	@Test
	public void testEqualsObject1() {
		HashMap<String, Long> op = new HashMap<>();
		op.put("i", Long.valueOf(1));
		op.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op);
		Fitness f2 = new Fitness(Type.max, op);
		
		assertTrue(f1.equals(f2));
	}

	@Test
	public void testEqualsObject2() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		HashMap<String, Long> op2 = new HashMap<>();
		op2.put("i", Long.valueOf(1));
		op2.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		Fitness f2 = new Fitness(Type.min, op2);
		
		assertFalse(f1.equals(f2));
	}

	@Test
	public void testEqualsObject3() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		HashMap<String, Long> op2 = new HashMap<>();
		op2.put("i1", Long.valueOf(1));
		op2.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		Fitness f2 = new Fitness(Type.max, op2);
		
		assertFalse(f1.equals(f2));
	}

	@Test
	public void testEqualsObject4() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		HashMap<String, Long> op2 = new HashMap<>();
		op2.put("i", Long.valueOf(10));
		op2.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		Fitness f2 = new Fitness(Type.max, op2);
		
		assertFalse(f1.equals(f2));
	}

	@Test
	public void testEqualsObject5() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		
		assertTrue(f1.equals(f1));
	}

	@Test
	public void testEqualsObject6() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		
		assertFalse(f1.equals(null));
	}

	@Test
	public void testEqualsObject7() {
		HashMap<String, Long> op1 = new HashMap<>();
		op1.put("i", Long.valueOf(1));
		op1.put("o", Long.valueOf(10));
		
		Fitness f1 = new Fitness(Type.max, op1);
		
		assertFalse(f1.equals(new Object()));
	}

}

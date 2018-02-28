package org.ordogene.algorithme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;
import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.models.JSONModel;
import org.ordogene.file.models.Relation;
import org.ordogene.file.models.Type;

import edu.emory.mathcs.backport.java.util.Collections;
import io.jenetics.util.RandomRegistry;

public class ModelTest {

	@Test
	public void testModel() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 100, 20, env, Collections.emptySet(), f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testModel_zero_slots() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 0, 20, env, Collections.emptySet(), f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testModel_negative_slots() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", -1, 20, env, Collections.emptySet(), f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testModel_zero_exectime() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 100, 0, env, Collections.emptySet(), f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testModel_negative_exectime() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 100, -1, env, Collections.emptySet(), f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_env() {
		Fitness f = mock(Fitness.class);
		
		new Model("model", 100, 20, null, Collections.emptySet(), f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_actions() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 100, 20, env, null, f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_element_actions() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(null);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 100, 20, env, actions, f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_fitness() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model("model", 100, 20, env, Collections.emptySet(), null);
	}

	@Test
	public void testCreateModel() {
		JSONModel jm = mock(JSONModel.class);
		JSONFitness jf = mock(JSONFitness.class);
		
		when(jm.getActions()).thenReturn(Collections.emptyList());
		when(jm.getEnvironment()).thenReturn(Collections.emptyList());
		when(jm.getExecTime()).thenReturn(20);
		when(jm.getFitness()).thenReturn(jf);
		when(jm.getSlots()).thenReturn(100);
		when(jm.getName()).thenReturn("testModelCreation");
		
		when(jf.getType()).thenReturn(Type.max);
		when(jf.getOperands()).thenReturn(Collections.emptyList());
		
		Model.createModel(jm);
	}

	@Test
	public void testWorkable_True() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		assertTrue(m.workable(a, env, 0));
	}
	
	@Test
	public void testWorkable_False() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(5);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(10);
		when(i.getRelation()).thenReturn(Relation.c);
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		assertFalse(m.workable(a, env, 0));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWorkable_actionNotInModel() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Action otherAction = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(5);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(otherAction.getOutputs()).thenReturn(outputs);
		when(otherAction.getInputs()).thenReturn(inputs);
		when(otherAction.getName()).thenReturn("otherAction");
		when(otherAction.getTime()).thenReturn(2);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(10);
		when(i.getRelation()).thenReturn(Relation.c);
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		assertFalse(m.workable(otherAction, env, 0));
	}

	@Test
	public void testGetWorkableAction() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		assertEquals(a, m.getWorkableAction(env, 0));
	}

	// FIXME
	@Ignore
	@Test
	public void testGetWorkableAction_multiple_time() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Action a1 = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		actions.add(a1);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(a1.getOutputs()).thenReturn(outputs);
		when(a1.getInputs()).thenReturn(inputs);
		when(a1.getName()).thenReturn("action1");
		when(a1.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(1));
		when(f.eval(a1)).thenReturn(Long.valueOf(1));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		assertEquals(a1, m.getWorkableAction(env, 0));
		assertEquals(a, m.getWorkableAction(env, 0));
		assertEquals(a, m.getWorkableAction(env, 0));
		assertEquals(a1, m.getWorkableAction(env, 0));
	}

	@Test
	public void testStartAnAction() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i1 = mock(Input.class);
		Input i2 = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		entityEnv.add(e2);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i1);
		inputs.add(i2);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		outputs.add(e2);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntity("e2")).thenReturn(e2);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(e2.getName()).thenReturn("e2");
		when(e2.getQuantity()).thenReturn(2);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i1.getName()).thenReturn("e1");
		when(i1.getQuantity()).thenReturn(1);
		when(i1.getRelation()).thenReturn(Relation.c);
		
		when(i2.getName()).thenReturn("e2");
		when(i2.getQuantity()).thenReturn(1);
		when(i2.getRelation()).thenReturn(Relation.p);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.startAction(a, env, 0);
	}

	@Test(expected=NullPointerException.class)
	public void testStartAnAction_null_action() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.startAction(null, env, 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testStartAnAction_action_not_in_model() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Action otherAction = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(otherAction.getOutputs()).thenReturn(outputs);
		when(otherAction.getInputs()).thenReturn(inputs);
		when(otherAction.getName()).thenReturn("action");
		when(otherAction.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(1);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.startAction(otherAction, env, 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testStartAnAction_action_not_workable() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(15);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.startAction(a, env, 0);
	}

	@Test
	public void testEndAnAction() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i1 = mock(Input.class);
		Input i2 = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		entityEnv.add(e2);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i1);
		inputs.add(i2);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		outputs.add(e2);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntity("e2")).thenReturn(e2);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(e2.getName()).thenReturn("e2");
		when(e2.getQuantity()).thenReturn(2);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i1.getName()).thenReturn("e1");
		when(i1.getQuantity()).thenReturn(1);
		when(i1.getRelation()).thenReturn(Relation.c);
		
		when(i2.getName()).thenReturn("e2");
		when(i2.getQuantity()).thenReturn(1);
		when(i2.getRelation()).thenReturn(Relation.p);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.startAction(a, env, 0);
		m.endAction(env, a);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEndAnAction_action_not_in_model() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Action otherAction = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(otherAction.getOutputs()).thenReturn(outputs);
		when(otherAction.getInputs()).thenReturn(inputs);
		when(otherAction.getName()).thenReturn("action");
		when(otherAction.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(5);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.startAction(a, env, 0);
		m.endAction(env, otherAction);
	}

	@Test(expected=NullPointerException.class)
	public void testEndAnAction_null_action() {
		RandomRegistry.setRandom(new Random(0));
		
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		Action a = mock(Action.class);
		Input i = mock(Input.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		Set<Action> actions = new HashSet<>();
		actions.add(a);
		
		Set<Input> inputs = new HashSet<>();
		inputs.add(i);
		Set<Entity> outputs = new HashSet<>();
		outputs.add(e1);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(10);
		
		when(a.getOutputs()).thenReturn(outputs);
		when(a.getInputs()).thenReturn(inputs);
		when(a.getName()).thenReturn("action");
		when(a.getTime()).thenReturn(5);
		
		when(i.getName()).thenReturn("e1");
		when(i.getQuantity()).thenReturn(5);
		when(i.getRelation()).thenReturn(Relation.c);
		
		when(f.eval(a)).thenReturn(Long.valueOf(3));
		
		Model m = new Model("model", 100, 20, env, actions, f);
		
		m.endAction(env, null);
	}

}

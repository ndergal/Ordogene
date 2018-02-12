package org.ordogene.algorithme;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;
import org.ordogene.file.JSONModel;
import org.ordogene.file.models.JSONAction;
import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.models.JSONOperand;
import org.ordogene.file.models.Relation;
import org.ordogene.file.models.Type;

import edu.emory.mathcs.backport.java.util.Collections;

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
		
		new Model(Collections.emptyList(), 100, 20, env, Collections.emptyList(), f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_snap() {
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
		
		new Model(null, 100, 20, env, Collections.emptyList(), f);
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
		
		new Model(Collections.emptyList(), 0, 20, env, Collections.emptyList(), f);
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
		
		new Model(Collections.emptyList(), -1, 20, env, Collections.emptyList(), f);
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
		
		new Model(Collections.emptyList(), 100, 0, env, Collections.emptyList(), f);
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
		
		new Model(Collections.emptyList(), 100, -1, env, Collections.emptyList(), f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_env() {
		Fitness f = mock(Fitness.class);
		
		new Model(null, 100, 20, null, Collections.emptyList(), f);
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
		
		new Model(Collections.emptyList(), 100, 20, env, null, f);
	}

	@Test(expected=NullPointerException.class)
	public void testModel_null_element_actions() {
		Environment env = mock(Environment.class);
		Entity e1 = mock(Entity.class);
		Fitness f = mock(Fitness.class);
		
		Set<Entity> entityEnv = new HashSet<>();
		entityEnv.add(e1);
		
		List<Action> actions = new ArrayList<>();
		actions.add(null);
		
		when(env.containsEntity(anyString())).thenReturn(true);
		when(env.getEntity("e1")).thenReturn(e1);
		when(env.getEntities()).thenReturn(entityEnv);
		
		when(e1.getName()).thenReturn("e1");
		when(e1.getQuantity()).thenReturn(1);
		
		new Model(Collections.emptyList(), 100, 20, env, actions, f);
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
		
		new Model(Collections.emptyList(), 100, 20, env, Collections.emptyList(), null);
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
		when(jm.getSnaps()).thenReturn(Collections.emptyList());
		
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
		
		List<Action> actions = new ArrayList<>();
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
		
		Model m = new Model(Collections.emptyList(), 100, 20, env, actions, f);
		
		assertTrue(m.workable(a));
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
		
		List<Action> actions = new ArrayList<>();
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
		
		Model m = new Model(Collections.emptyList(), 100, 20, env, actions, f);
		
		assertFalse(m.workable(a));
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
		
		List<Action> actions = new ArrayList<>();
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
		
		Model m = new Model(Collections.emptyList(), 100, 20, env, actions, f);
		
		assertFalse(m.workable(otherAction));
	}

	@Test
	public void testGetWorkableAction() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartAnAction() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndAnAction() {
		fail("Not yet implemented");
	}

}

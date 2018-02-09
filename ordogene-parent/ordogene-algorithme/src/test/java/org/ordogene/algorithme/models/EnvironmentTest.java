package org.ordogene.algorithme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void test_environment() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e2.getName()).thenReturn("name2");
		
		List<Entity> entities = Arrays.asList(e1, e2);
		
		new Environment(entities);		
	}
	
	@Test(expected=NullPointerException.class)
	public void test_environment_with_null_list() {
		new Environment(null);		
	}
	
	@Test(expected=NullPointerException.class)
	public void test_environment_with_null_element_in_list() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e2.getName()).thenReturn("name2");
		
		List<Entity> entities = Arrays.asList(e1, e2, null);
		
		new Environment(entities);		
	}

	@Test
	public void test_getEntity() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		
		List<Entity> entities = Arrays.asList(e1, e2);
		
		Environment env = new Environment(entities);
		
		assertEquals(e1, env.getEntity("name1"));
	}

	@Test(expected=NullPointerException.class)
	public void test_getEntity_with_null() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		
		List<Entity> entities = Arrays.asList(e1, e2);
		
		Environment env = new Environment(entities);
		
		env.getEntity(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_getEntity_with_non_present_element() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		
		List<Entity> entities = Arrays.asList(e1, e2);
		
		Environment env = new Environment(entities);
		
		env.getEntity("name3");
	}

	@Test
	public void test_getEntities() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		
		List<Entity> entities = Arrays.asList(e1, e2);
		
		Environment env = new Environment(entities);
		
		assertEquals(entities, env.getEntities());
	}

	@Test
	public void test_equals1() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		
		List<Entity> entities = Arrays.asList(e1, e2);
		
		Environment env1 = new Environment(entities);
		Environment env2 = new Environment(entities);
		
		assertTrue(env1.equals(env2));
	}

	@Test
	public void test_equals2() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		Entity e3 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		when(e3.getName()).thenReturn("name3");
		when(e3.getQuantity()).thenReturn(2);
		
		List<Entity> entities1 = Arrays.asList(e1, e2);
		List<Entity> entities2 = Arrays.asList(e1, e3);
		
		Environment env1 = new Environment(entities1);
		Environment env2 = new Environment(entities2);
		
		assertFalse(env1.equals(env2));
	}

	@Test
	public void test_equals3() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		Entity e3 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		when(e3.getName()).thenReturn("name2");
		when(e3.getQuantity()).thenReturn(3);
		
		List<Entity> entities1 = Arrays.asList(e1, e2);
		List<Entity> entities2 = Arrays.asList(e1, e3);
		
		Environment env1 = new Environment(entities1);
		Environment env2 = new Environment(entities2);
		
		assertFalse(env1.equals(env2));
	}

	@Test
	public void test_equals4() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		Entity e3 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		when(e3.getName()).thenReturn("name2");
		when(e3.getQuantity()).thenReturn(3);
		
		List<Entity> entities1 = Arrays.asList(e1, e2);
		List<Entity> entities2 = Arrays.asList(e1, e2, e3);
		
		Environment env1 = new Environment(entities1);
		Environment env2 = new Environment(entities2);
		
		assertFalse(env1.equals(env2));
	}
	
	@Test
	public void test_equals5() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		Entity e3 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		when(e3.getName()).thenReturn("name3");
		when(e3.getQuantity()).thenReturn(3);
		
		List<Entity> entities1 = Arrays.asList(e1, e3, e2);
		List<Entity> entities2 = Arrays.asList(e1, e2, e3);
		
		Environment env1 = new Environment(entities1);
		Environment env2 = new Environment(entities2);
		
		assertTrue(env1.equals(env2));
	}

	@Test
	public void test_hashCode1() {
		Entity e1 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		
		List<Entity> entities = Arrays.asList(e1);
		
		Environment env1 = new Environment(entities);
		Environment env2 = new Environment(entities);
		
		assertTrue(env1.hashCode() == env2.hashCode());
	}

	@Test
	public void test_hashCode2() {
		Entity e1 = new Entity("name1", 1);
		Entity e2 = new Entity("name1", 1);
		
		List<Entity> entities1 = Arrays.asList(e1);
		List<Entity> entities2 = Arrays.asList(e2);
		
		Environment env1 = new Environment(entities1);
		Environment env2 = new Environment(entities2);
		assertTrue(env1.hashCode() == env2.hashCode());
	}

	@Test
	public void test_hashCode3() {
		Entity e1 = mock(Entity.class);
		Entity e2 = mock(Entity.class);
		
		when(e1.getName()).thenReturn("name1");
		when(e1.getQuantity()).thenReturn(1);
		when(e2.getName()).thenReturn("name2");
		when(e2.getQuantity()).thenReturn(2);
		
		List<Entity> entities1 = Arrays.asList(e1);
		List<Entity> entities2 = Arrays.asList(e2);
		
		Environment env1 = new Environment(entities1);
		Environment env2 = new Environment(entities2);
		
		assertFalse(env1.hashCode() == env2.hashCode());
	}

}

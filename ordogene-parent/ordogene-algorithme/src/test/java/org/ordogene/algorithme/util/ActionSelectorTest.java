package org.ordogene.algorithme.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.algorithme.models.Action;

import io.jenetics.util.RandomRegistry;

@RunWith(MockitoJUnitRunner.class)
public class ActionSelectorTest {
	@InjectMocks
	private ActionSelector mockActionSelector;
	
	@Test
	public void should_assert_true_for_empty_action_list() {
		assertThat(mockActionSelector.isReset()).isTrue();
	}
	
	@Test
	public void do_test_isReset_for_not_empty_action_list() {
		// what - Création des mocks pour ce test particulier
		Action action = mock(Action.class);
		
		mockActionSelector.add(action, 0);
		assertFalse(mockActionSelector.isReset());
	}
	
	@Test
	public void should_assert_true_after_emptying_actions() {
		// what - Création des mocks pour ce test particulier
		Action action = mock(Action.class);
		
		// when - pas vraiment utile pour ce test, c'est un exemple
		when(action.getName()).thenReturn("a1");
		
		// then - assertions via assertj
		mockActionSelector.add(action, 0);
		assertFalse(mockActionSelector.isReset());
		
		mockActionSelector.reset();
		assertTrue(mockActionSelector.isReset());
	}
	
	@Test(expected=IllegalStateException.class)
	public void do_select_without_action() {
		mockActionSelector.select();
	}
	
	@Test
	public void do_select_with_one_action() {
		// Initialize Random with a particular seed
		RandomRegistry.setRandom(new Random(0));
		
		// what
		Action a = Action.EMPTY(1);
		
		// when
		
		// then
		mockActionSelector.add(a, 1);
		assertEquals(a, mockActionSelector.select());
	}
	
	@Test
	public void do_select_with_multiple_actions() {
		// Initialize Random with a particular seed
		RandomRegistry.setRandom(new Random(0));
		
		// what
		Action a1 = Action.EMPTY(1);
		Action a2 = Action.EMPTY(2);
		Action a3 = Action.EMPTY(3);
		Action a4 = Action.EMPTY(4);
		
		// when
		
		// then
		mockActionSelector.add(a1, 1);
		mockActionSelector.add(a2, 1);
		mockActionSelector.add(a3, 1);
		mockActionSelector.add(a4, 1);
		assertEquals(a1, mockActionSelector.select());
		assertEquals(a4, mockActionSelector.select());
		assertEquals(a4, mockActionSelector.select());
	}
}

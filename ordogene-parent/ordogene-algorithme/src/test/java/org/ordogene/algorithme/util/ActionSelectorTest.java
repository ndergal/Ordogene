package org.ordogene.algorithme.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Random;

import org.assertj.core.api.Assertions;
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
		assertThat(mockActionSelector.isEmpty()).isTrue();
	}
	
	@Test
	public void do_test_isReset_for_not_empty_action_list() {
		// what - Création des mocks pour ce test particulier
		Action action = mock(Action.class);
		
		mockActionSelector.add(action, 0);
		assertThat(mockActionSelector.isEmpty()).isFalse();
	}
	
	@Test
	public void should_assert_true_after_emptying_actions() {
		// what - Création des mocks pour ce test particulier
		Action action = mock(Action.class);
		
		// when - pas vraiment utile pour ce test, c'est un exemple
		when(action.getName()).thenReturn("a1");
		
		// then - assertions via assertj
		mockActionSelector.add(action, 0);
		assertThat(mockActionSelector.isEmpty()).isFalse();
		
		mockActionSelector.reset();
		assertThat(mockActionSelector.isEmpty()).isTrue();
	}
	
	@Test
	public void do_select_without_action() {
		Assertions.assertThatThrownBy(() -> mockActionSelector.select()).isInstanceOf(IllegalStateException.class);
	}
	
	@Test
	public void do_select_with_one_action() {
		// Initialize Random with a particular seed
		RandomRegistry.setRandom(new Random(0));
		
		// what
		Action a = mock(Action.class);
		
		// when
		when(a.getInputs()).thenReturn(Collections.emptySet());
		when(a.getOutputs()).thenReturn(Collections.emptySet());
		when(a.getName()).thenReturn("EMPTY");
		when(a.getTime()).thenReturn(0);
		
		// then
		mockActionSelector.add(a, 1);
		assertThat(mockActionSelector.select()).isEqualTo(a);
	}
	
	@Test
	public void do_select_with_multiple_actions() {
		// Initialize Random with a particular seed
		RandomRegistry.setRandom(new Random(0));
		
		// what
		
		Action a1 = mock(Action.class);
		Action a2 = mock(Action.class);
		Action a3 = mock(Action.class);
		Action a4 = mock(Action.class);
		
		// when
		when(a1.getName()).thenReturn("EMPTY");
		when(a2.getName()).thenReturn("EMPTY");
		when(a3.getName()).thenReturn("EMPTY");
		when(a4.getName()).thenReturn("EMPTY");

		when(a1.getTime()).thenReturn(1);
		when(a1.getTime()).thenReturn(2);
		when(a1.getTime()).thenReturn(3);
		when(a1.getTime()).thenReturn(4);

		when(a1.getInputs()).thenReturn(Collections.emptySet());
		when(a2.getInputs()).thenReturn(Collections.emptySet());
		when(a3.getInputs()).thenReturn(Collections.emptySet());
		when(a4.getInputs()).thenReturn(Collections.emptySet());

		when(a1.getOutputs()).thenReturn(Collections.emptySet());
		when(a2.getOutputs()).thenReturn(Collections.emptySet());
		when(a3.getOutputs()).thenReturn(Collections.emptySet());
		when(a4.getOutputs()).thenReturn(Collections.emptySet());
		
		// then
		mockActionSelector.add(a1, 1);
		mockActionSelector.add(a2, 1);
		mockActionSelector.add(a3, 1);
		mockActionSelector.add(a4, 1);
		assertThat(mockActionSelector.select()).isEqualTo(a1);
		assertThat(mockActionSelector.select()).isEqualTo(a4);
		assertThat(mockActionSelector.select()).isEqualTo(a4);
	}
}

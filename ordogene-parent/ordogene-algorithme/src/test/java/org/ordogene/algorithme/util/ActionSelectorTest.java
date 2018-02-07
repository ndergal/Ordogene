package org.ordogene.algorithme.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.algorithme.models.Action;

@RunWith(MockitoJUnitRunner.class)
public class ActionSelectorTest {
	@InjectMocks
	private ActionSelector mockActionSelector;
	
	@Test
	public void should_assert_true_for_empty_action_list() {
		assertThat(mockActionSelector.isReset()).isTrue();
	}
	
	@Test
	public void should_assert_true_after_emptying_actions() {
		// what - Création des mocks pour ce test particulier
		Action action = mock(Action.class);
		
		// when - pas vraiment utile pour ce test, c'est un exemple
		when(action.getName()).thenReturn("a1");
		
		// then - assertions via assertj
		mockActionSelector.add(action, 1);
		assertThat(mockActionSelector.isReset()).isFalse();
		assertThat(mockActionSelector.select()).isEqualTo(action);
		
		mockActionSelector.reset();
		assertThat(mockActionSelector.isReset()).isTrue();
	}
}

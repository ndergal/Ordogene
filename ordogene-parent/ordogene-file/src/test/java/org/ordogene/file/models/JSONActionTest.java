package org.ordogene.file.models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JSONActionTest {

	@Test
	public void sould_assert_true_after_getname() {
		JSONAction jaction = new JSONAction();
		jaction.setName("action_name");
		assertEquals(jaction.getName(), "action_name");
	}

	@Test
	public void sould_assert_true_after_gettime() {
		JSONAction jaction = new JSONAction();
		jaction.setTime(113);
		assertEquals(jaction.getTime(), 113);
	}
	
	@Test
	public void sould_assert_true_after_getinputs() {
		JSONAction jaction = new JSONAction();
		jaction.setInput(new ArrayList<JSONInput>());
		assertEquals(jaction.getInput(), new ArrayList<>());
		
	}
}

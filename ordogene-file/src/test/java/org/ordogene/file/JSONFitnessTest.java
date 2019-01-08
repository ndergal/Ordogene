package org.ordogene.file;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.models.JSONOperand;
import org.ordogene.file.models.Type;

public class JSONFitnessTest {
	@Test
	public void sould_assert_true_after_gettype() {
		JSONFitness fitness = new JSONFitness();
		fitness.setType(Type.max);
		assertEquals(fitness.getType(), Type.max);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_IAE_if_a_operand_null() {
		JSONFitness fitness = new JSONFitness();
		List<JSONOperand> list = new ArrayList<>();
		list.add(null);
		list.add(new JSONOperand());
		list.add(null);
		fitness.setOperands(list);
	}
}

package org.ordogene.algorithme.models;

import java.util.HashMap;
import java.util.Objects;

public class Fitness {
	private final Type type;
	private final HashMap<String, Long> operands;

	public Fitness(Type type, HashMap<String, Long> operands) {
		this.type = Objects.requireNonNull(type);
		this.operands = Objects.requireNonNull(operands);
	}
	
	public long eval(Action a) {
		int score = 0;
		for(Input i : a.getInputs()) {
			String entityName = i.getEntity().getName();
			long coef = operands.getOrDefault(entityName, Long.valueOf(0));
			long quantity = i.getEntity().getQuantity();
			score += coef * quantity;
		}
		return score;
	}

	@Override
	public String toString() {
		return "Fitness [type=" + type + ", operands=" + operands + "]";
	}
}

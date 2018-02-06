package org.ordogene.algorithme.models;

import java.util.List;
import java.util.Objects;

public class Fitness {
	private final Type type;
	private final List<Operand> operands;

	public Fitness(Type type, List<Operand> operands) {
		this.type = Objects.requireNonNull(type);
		this.operands = Objects.requireNonNull(operands);
	}
}

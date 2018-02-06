package org.ordogene.algorithme.models;

import java.util.ArrayList;
import java.util.Objects;

public class Fitness {
	private final Type type;
	private final ArrayList<Operand> operands;

	public Fitness(Type type, ArrayList<Operand> operands) {
		this.type = Objects.requireNonNull(type);
		this.operands = Objects.requireNonNull(operands);
	}
}

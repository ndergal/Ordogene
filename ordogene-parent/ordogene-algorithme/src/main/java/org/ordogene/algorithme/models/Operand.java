package org.ordogene.algorithme.models;

import java.util.Objects;

public class Operand {
	private final String name;
	private final int coef;

	public Operand(String name, int coef) {
		if(coef <= 0) {
			throw new IllegalArgumentException("the coef of an operand had to be positive");
		}
		this.name = Objects.requireNonNull(name);
		this.coef = coef;
	}
}

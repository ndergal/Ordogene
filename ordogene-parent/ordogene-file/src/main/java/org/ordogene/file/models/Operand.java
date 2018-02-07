package org.ordogene.file.models;

import java.util.Objects;

public class Operand {
	private String name;
	private int coef;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public int getCoef() {
		return coef;
	}

	public void setCoef(int coef) {
		this.coef = coef;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Operand [name=");
		builder.append(name);
		builder.append(", coef=");
		builder.append(coef);
		builder.append("]");
		return builder.toString();
	}

}

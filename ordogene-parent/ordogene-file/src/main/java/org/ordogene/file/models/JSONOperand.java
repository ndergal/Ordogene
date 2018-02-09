package org.ordogene.file.models;

import java.util.Objects;

import org.ordogene.file.parser.Validable;

public class JSONOperand implements Validable {
	private String name;
	private long coef;

	@Override
	public boolean isValid() {
		return name != null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public long getCoef() {
		return coef;
	}

	public void setCoef(long coef) {
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

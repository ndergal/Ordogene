package org.ordogene.file.models;

import java.util.List;
import java.util.Objects;

public class JSONFitness {
	private Type type;
	private List<JSONOperand> operands;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = Objects.requireNonNull(type);
	}

	public List<JSONOperand> getOperands() {
		return operands;
	}

	public void setOperands(List<JSONOperand> operands) {
		this.operands = Objects.requireNonNull(operands);
		if (operands.stream().anyMatch(x -> x == null)) {
			throw new IllegalArgumentException("the fitness cannot contains a null operand");
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Fitness [type=");
		builder.append(type);
		builder.append(", operands=");
		builder.append(operands);
		builder.append("]");
		return builder.toString();
	}

}

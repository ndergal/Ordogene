package org.ordogene.file.models;

import java.util.List;
import java.util.Objects;

import org.ordogene.file.parser.Validable;

public class JSONFitness implements Validable {
	
	private Type type;
	private Integer value;
	private List<JSONOperand> operands;

	@Override
	public boolean isValid() {
		if(type != null && operands != null && operands.stream().allMatch(Validable::isValid)) {
			if(type.equals(Type.value)) {
				return value != null;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

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
		builder.append(", value=");
		builder.append(value);
		builder.append(", operands=");
		builder.append(operands);
		builder.append("]");
		return builder.toString();
	}

}

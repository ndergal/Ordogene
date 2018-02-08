package org.ordogene.algorithme.models;

import java.util.Objects;

import org.ordogene.file.models.JSONInput;
import org.ordogene.file.models.Relation;

public class Input extends Entity{
	private final Relation relation;
	
	public Input(String name, int quantity, Relation relation) {
		super(name, quantity);
		this.relation = Objects.requireNonNull(relation);
	}

	public static Input createInput(JSONInput ji) {
		Objects.requireNonNull(ji);
		return new Input(ji.getName(), ji.getQuantity(), ji.getRelation());
	}

	public Relation getRelation() {
		return relation;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * super.hashCode();
		result = prime * result + relation.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Input))
			return false;
		Input input = (Input) obj;
		if (relation != input.relation)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Input [entity=" + super.toString() + ", relation=" + relation + "]";
	}
}

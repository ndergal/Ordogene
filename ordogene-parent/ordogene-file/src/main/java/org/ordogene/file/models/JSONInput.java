package org.ordogene.file.models;

import java.util.Objects;

public class JSONInput {
	private String name;
	private int quantity;
	private Relation relation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("The quantity has to be strictly positive");
		}
		this.quantity = quantity;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = Objects.requireNonNull(relation);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Input [name=");
		builder.append(name);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", relation=");
		builder.append(relation);
		builder.append("]");
		return builder.toString();
	}

}

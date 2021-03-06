package org.ordogene.file.models;

import java.util.Objects;

import org.ordogene.file.parser.Validable;
/**
 * POJO
 * @author darwinners team
 *
 */
public class JSONEntity implements Validable {
	private String name;
	private int quantity;

	public int getQuantity() {
		return quantity;
	}

	@Override
	public boolean isValid() {
		return name != null;
	}

	public void setQuantity(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("the quantity of an entity has to be positive or equal to zero");
		}
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Entity [name=");
		builder.append(name);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append("]");
		return builder.toString();
	}

}
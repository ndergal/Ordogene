package org.ordogene.algorithme.models;

import java.util.Objects;

import org.ordogene.file.models.JSONEntity;

public class Entity {
	private final String name;
	private int quantity;
	
	public Entity(String name, int quantity) {
		if(quantity <0){
			throw new IllegalArgumentException("the quantity of an entity cannot be negative");
		}
		this.name = Objects.requireNonNull(name);
		this.quantity = quantity;
	}
	
	public static Entity createEntity(JSONEntity je) {
		Objects.requireNonNull(je);
		return new Entity(je.getName(),je.getQuantity());
	}

	public int getQuantity() {
		return quantity;
	}
	
	public void addQuantity(int q) {
		if(quantity + q < 0) {
			throw new IllegalArgumentException("The end quantity can't be negative.");
		}
		quantity += q;
	}

	public void setQuantity(int quantity) {
		if(quantity < 0) {
			throw new IllegalArgumentException("The end quantity can't be negative.");
		}
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * name.hashCode();
		result = prime * result + quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Entity))
			return false;
		Entity entity = (Entity) obj;
		if (!name.equals(entity.name))
			return false;
		if (quantity != entity.quantity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Entity [name=" + name + ", quantity=" + quantity + "]";
	}
}

package org.ordogene.algorithme.models;

import java.util.Objects;

import org.ordogene.file.models.JSONEntity;

public class Entity {
	
	private final String name;
	private int quantity;
	private int maxQuantity;
	
	public Entity(String name, int quantity) {
		this(name, quantity, quantity);
	}
	
	private Entity(String name, int quantity, int maxQuantity) {
		if(quantity < 0){
			throw new IllegalArgumentException("the quantity of an entity cannot be negative");
		}
		this.name = Objects.requireNonNull(name);
		this.quantity = quantity;
		this.maxQuantity = maxQuantity;
	}
	
	public static Entity createEntity(JSONEntity je) {
		Objects.requireNonNull(je);
		return new Entity(je.getName(),je.getQuantity());
	}

	public int getQuantity() {
		return quantity;
	}
	
	public int getMaxQuantity() {
		return maxQuantity;
	}

	public void addQuantity(int q) {
		if(quantity + q < 0) {
			throw new IllegalArgumentException("The end quantity can't be negative.");
		}
		quantity += q;
		maxQuantity += q;
	}
	
	public void putInPending(int q) {
		if(quantity - q < 0) {
			throw new IllegalArgumentException("The end quantity can't be negative.");
		}
		if(q < 0) {
			throw new IllegalArgumentException("The quantity to put in pending can't be negative.");
		}
		quantity -= q;
	}
	
	public void freePending(int q) {
		if(quantity + q > maxQuantity) {
			throw new IllegalArgumentException("The end quantity can't exceed the maximum quantity");
		}
		if(q < 0) {
			throw new IllegalArgumentException("The quantity to free can't be negative.");
		}
		quantity += q;
	}

	public String getName() {
		return name;
	}
	
	public Entity copy() {
		return new Entity(name, quantity, maxQuantity);
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

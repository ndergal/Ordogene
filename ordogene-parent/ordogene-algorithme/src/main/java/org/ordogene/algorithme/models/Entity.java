package org.ordogene.algorithme.models;

import java.io.Serializable;
import java.util.Objects;

import org.ordogene.file.models.JSONEntity;

public class Entity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private int quantity;
	private boolean available = false;
	
	public Entity(String name, int quantity) {
		if(quantity < 0){
			throw new IllegalArgumentException("the quantity of an entity cannot be negative");
		}
		this.name = Objects.requireNonNull(name);
		this.quantity = quantity;
		if(quantity > 0) {
			this.available = true;
		}
	}
	
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
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
		if(quantity == 0) {
			available = false;
		}
		quantity += q;
	}

	public void setQuantity(int quantity) {
		if(quantity < 0) {
			throw new IllegalArgumentException("The end quantity can't be negative.");
		}
		this.quantity = quantity;
	}
	
	public void putInPending(int q) {
		if(q < 0) {
			throw new IllegalArgumentException("The quantity to put in pending can't be negative.");
		}
		quantity -= q;
	}
	
	public void free(int q) {
		if(q < 0) {
			throw new IllegalArgumentException("The quantity to free can't be negative.");
		}
		quantity += q;
	}

	public String getName() {
		return name;
	}
	
	public Entity copy() {
		return new Entity(name, quantity);
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

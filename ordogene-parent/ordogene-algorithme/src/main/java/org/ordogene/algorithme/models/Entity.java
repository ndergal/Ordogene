package org.ordogene.algorithme.models;

import java.util.Objects;

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
}

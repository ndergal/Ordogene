package org.ordogene.algorithme.models;

import java.util.ArrayList;
import java.util.Objects;

public class Environment {
	private final ArrayList<Entity> entities;
	
	public Environment(ArrayList<Entity> entities) {
		this.entities = Objects.requireNonNull(entities);
	}
}

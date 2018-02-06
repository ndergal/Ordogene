package org.ordogene.algorithme.models;

import java.util.List;
import java.util.Objects;

public class Environment {
	private final List<Entity> entities;

	public Environment(List<Entity> entities) {
		this.entities = Objects.requireNonNull(entities);
	}
}

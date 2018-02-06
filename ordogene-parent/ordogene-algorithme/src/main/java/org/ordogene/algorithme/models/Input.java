package org.ordogene.algorithme.models;

import java.util.Objects;

public class Input {
	private final Entity entity;
	private final Relation relation;
	
	public Input(Entity entity, Relation relation) {
		this.entity = Objects.requireNonNull(entity);
		this.relation = Objects.requireNonNull(relation);
	}
}

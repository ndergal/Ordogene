package org.ordogene.algorithme.models;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Environment {
	private final Map<String, Entity> entities = new HashMap<>();
	
	public Environment(List<Entity> entities) {
		Objects.requireNonNull(entities);
		entities.forEach(e -> this.entities.put(e.getName(), e));
	}
	
	public Entity getEntity(String name) {
		Entity e = entities.get(name);
		if(e == null) {
			throw new IllegalArgumentException("The entity asked don't exist");
		}
		return e;
	}

	public List<Entity> getEntities() {
		return new ArrayList<>(entities.values());
	}
}

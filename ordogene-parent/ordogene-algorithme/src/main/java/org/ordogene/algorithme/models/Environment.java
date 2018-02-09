package org.ordogene.algorithme.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Environment {
	private final Map<String, Entity> entities = new HashMap<>();
	
	public Environment(List<Entity> entities) {
		Objects.requireNonNull(entities);
		for(Entity e : entities) {
			Objects.requireNonNull(e);
			this.entities.put(e.getName(), e);
		}
	}
	
	public Entity getEntity(String name) {
		Objects.requireNonNull(name);
		Entity e = entities.get(name);
		if(e == null) {
			throw new IllegalArgumentException("The entity asked don't exist");
		}
		return e;
	}
	
	public boolean containsEntity(String name) {
		return entities.containsKey(name);
	}

	public List<Entity> getEntities() {
		return new ArrayList<>(entities.values());
	}

	@Override
	public int hashCode() {
		return 31 * entities.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Environment))
			return false;
		Environment env = (Environment) obj;
		if (!entities.equals(env.entities))
			return false;
		return true;
	}
}

package org.ordogene.algorithme.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Environment{
	
	private final Map<String, Entity> entities = new HashMap<>();
	
	public Environment(Set<Entity> entities) {
		Objects.requireNonNull(entities);
		for(Entity e : entities) {
			Objects.requireNonNull(e);
			if(this.entities.put(e.getName(), e) != null) {
				throw new IllegalArgumentException("The Environment can't have Entityt with the same name.");
			}
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

	public Set<Entity> getEntities() {
		return new HashSet<>(entities.values());
	}

	@Override
	public String toString() {
		return "Environment [entities=" + entities + "]";
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
	
	public Environment copy() {
		Set<Entity> entitiesSet = new HashSet<>();
		entities.forEach((k, v) -> entitiesSet.add(v.copy()));
		return new Environment(entitiesSet);
	}
}

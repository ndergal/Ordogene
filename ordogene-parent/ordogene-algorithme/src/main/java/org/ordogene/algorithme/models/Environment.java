package org.ordogene.algorithme.models;

<<<<<<< HEAD
import java.util.List;
=======
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> branch 'dev' of https://ndergal@bitbucket.org/darwinners/ordogene.git
import java.util.Objects;

public class Environment {
<<<<<<< HEAD
	private final List<Entity> entities;

	public Environment(List<Entity> entities) {
		this.entities = Objects.requireNonNull(entities);
=======
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
>>>>>>> branch 'dev' of https://ndergal@bitbucket.org/darwinners/ordogene.git
	}
}

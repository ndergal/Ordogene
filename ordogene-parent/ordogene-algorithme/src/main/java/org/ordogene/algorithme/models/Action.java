package org.ordogene.algorithme.models;

import java.util.List;
import java.util.Objects;

public class Action {
	private final String name;
	private final int time;
	private final List<Input> inputs;
	private final List<Entity> outputs;

	public Action(String name, int time, List<Input> inputs, List<Entity> outputs) {
		if (time <= 0) {
			throw new IllegalArgumentException("the time an action need has to be positive");
		}
		this.name = Objects.requireNonNull(name);
		this.time = time;
		this.inputs = Objects.requireNonNull(inputs);
		this.outputs = Objects.requireNonNull(outputs);
	}
}

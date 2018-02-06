package org.ordogene.algorithme.models;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Action {
	public static Action EMPTY(int time) {
		return new Action("EMPTY", time, Collections.emptyList(), Collections.emptyList());
	}
	
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

	public String getName() {
		return name;
	}

	public int getTime() {
		return time;
	}

	public List<Input> getInputs() {
		return inputs;
	}

	public List<Entity> getOutputs() {
		return outputs;
	}
}

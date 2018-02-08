package org.ordogene.algorithme.models;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ordogene.file.models.JSONAction;

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
	
	public static Action createAction(JSONAction jAction) {
		List<Input> inputs = jAction.getInputs().stream().map(Input::createInput).collect(Collectors.toList());
		List<Entity> outputs = jAction.getOutputs().stream().map(Entity::createEntity).collect(Collectors.toList());
		return new Action(jAction.getName(), jAction.getTime(), inputs, outputs);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * inputs.hashCode();
		result = prime * result + name.hashCode();
		result = prime * result + outputs.hashCode();
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Action))
			return false;
		Action action = (Action) obj;
		if (!inputs.equals(action.inputs))
			return false;
		if (!name.equals(action.name))
			return false;
		if (!outputs.equals(action.outputs))
			return false;
		if (time != action.time)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Action [name=" + name + ", time=" + time + ", inputs=" + inputs + ", outputs=" + outputs + "]";
	}
}

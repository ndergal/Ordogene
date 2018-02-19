package org.ordogene.algorithme.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.ordogene.file.models.JSONAction;

public class Action {
	public static Action EMPTY() {
		return new Action("EMPTY", 1, Collections.emptySet(), Collections.emptySet());
	}
	
	private final String name;
	private final int time;
	private final Set<Input> inputs = new HashSet<>();
	private final Set<Entity> outputs = new HashSet<>();

	public Action(String name, int time, Set<Input> inputs, Set<Entity> outputs) {
		if (time <= 0) {
			throw new IllegalArgumentException("the time an action need has to be positive");
		}
		this.name = Objects.requireNonNull(name);
		this.time = time;
		Objects.requireNonNull(inputs).stream().forEach(i -> this.inputs.add(Objects.requireNonNull(i)));
		Objects.requireNonNull(outputs).stream().forEach(e -> this.outputs.add(Objects.requireNonNull(e)));
	}
	
	public static Action createAction(JSONAction jAction) {
		Set<Input> inputs = jAction.getInput().stream().map(Input::createInput).collect(Collectors.toSet());
		Set<Entity> outputs = jAction.getOutput().stream().map(Entity::createEntity).collect(Collectors.toSet());
		return new Action(jAction.getName(), jAction.getTime(), inputs, outputs);
	}

	public String getName() {
		return name;
	}

	public int getTime() {
		return time;
	}

	public Set<Input> getInputs() {
		return inputs;
	}

	public Set<Entity> getOutputs() {
		return outputs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * inputs.hashCode();
		result = prime * result + name.hashCode();
		result = prime * result + outputs.hashCode();
//		result = prime * result + startTime;
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
//		if (startTime != action.startTime)
//			return false;
		if (time != action.time)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Action [name=" + name + ", time=" + time + ", inputs=" + inputs + ", outputs=" + outputs + "]";
	}
}

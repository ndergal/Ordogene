package org.ordogene.file.models;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Action {
	public static Action EMPTY(int time) {
		return new Action("EMPTY", time, Collections.emptyList(), Collections.emptyList());
	}

	private String name;
	private int time;
	private List<Input> input;
	private List<Entity> output;

	private Action(String name, int time, List<Input> input, List<Entity> output) {

		this.name = Objects.requireNonNull(name);
		this.time = time;
		this.input = Objects.requireNonNull(input);

	}

	public Action() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public int getTime() {
		return time;
	}

	public List<Input> getInputs() {
		return input;
	}

	public List<Entity> getOutputs() {
		return output;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public void setTime(int time) {
		if (time <= 0) {
			throw new IllegalArgumentException("the time an action need has to be positive");
		}
		this.time = time;
	}

	public void setInput(List<Input> input) {
		this.input = Objects.requireNonNull(input);
	}

	public void setOutput(List<Entity> output) {
		this.output = Objects.requireNonNull(output);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Action [name=");
		builder.append(name);
		builder.append(", time=");
		builder.append(time);
		builder.append(", input=");
		builder.append(input);
		builder.append(", output=");
		builder.append(output);
		builder.append("]");
		return builder.toString();
	}

}

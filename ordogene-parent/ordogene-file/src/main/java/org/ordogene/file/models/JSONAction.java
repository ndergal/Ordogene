package org.ordogene.file.models;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.ordogene.file.parser.Validable;

public class JSONAction implements Validable {

	private String name;
	private int time;
	private List<JSONInput> input;
	private List<JSONEntity> output;

	private JSONAction(String name, int time, List<JSONInput> input, List<JSONEntity> output) {

		this.name = Objects.requireNonNull(name);
		this.time = time;
		this.input = Objects.requireNonNull(input);

	}

	public JSONAction() {

	}

	@Override
	public boolean isValid() {
		return name != null && time != 0 && input != null && output != null
				&& input.stream().allMatch(Validable::isValid) && output.stream().allMatch(Validable::isValid);
	}

	public static JSONAction EMPTY(int time) {
		return new JSONAction("EMPTY", time, Collections.emptyList(), Collections.emptyList());
	}

	public String getName() {
		return name;
	}

	public int getTime() {
		return time;
	}

	public List<JSONInput> getInput() {
		return input;
	}

	public List<JSONEntity> getOutput() {
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

	public void setInput(List<JSONInput> input) {
		this.input = Objects.requireNonNull(input);
	}

	public void setOutput(List<JSONEntity> output) {
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

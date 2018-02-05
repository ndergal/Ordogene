package org.ordogene.algorithme.models;

import java.util.ArrayList;
import java.util.Objects;

public class Action {
	private final String name;
	private final int time;
	private final ArrayList<Input> inputs;
	private final ArrayList<Entity> outputs;
	
	public Action(String name, int time, ArrayList<Input> inputs, ArrayList<Entity> outputs) {
		if(time <=0) {
			throw new IllegalArgumentException("the time an action need has to be positive");
		}
		this.name = Objects.requireNonNull(name);
		this.time = time;
		this.inputs = Objects.requireNonNull(inputs);
		this.outputs = Objects.requireNonNull(outputs);
	}
}

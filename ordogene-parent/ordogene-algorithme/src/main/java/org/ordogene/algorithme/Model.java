package org.ordogene.algorithme;

import java.util.ArrayList;
import java.util.Objects;

import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;

public class Model {
	private final int slots;
	private final int execTime;
	private final Environment environment;
	private final ArrayList<Action> actions;
	private final ArrayList<Fitness> fitnesses;
	
	private Model(int slots, int execTime,Environment environment,ArrayList<Action> actions,ArrayList<Fitness> fitnesses) {
		if(slots <= 0) {
			throw new IllegalArgumentException("slots has to be a positive integer");
		}
		if(execTime <= 0) {
			throw new IllegalArgumentException("execTime has to be a positive integer");
		}
		this.slots = slots;
		this.execTime = execTime;
		this.environment = Objects.requireNonNull(environment);
		this.actions = Objects.requireNonNull(actions);
		this.fitnesses = Objects.requireNonNull(fitnesses);
	}
	
	public static Model createModel(String jsonModel) {
		return null;
	}
}

package org.ordogene.file;

import java.util.List;
import java.util.Objects;

import org.ordogene.file.models.Action;
import org.ordogene.file.models.Entity;
import org.ordogene.file.models.Fitness;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Model {
	private List<Integer> snaps;
	private int slots;
	@JsonProperty("exec_time")
	private int execTime;
	private List<Entity> environment;
	private List<Action> actions;
	private Fitness fitness;
	@JsonIgnore
	private List<Entity> currentEnvironment;

	public List<Integer> getSnaps() {
		return snaps;
	}

	public void setSnaps(List<Integer> snaps) {
		this.snaps = Objects.requireNonNull(snaps);
		if (this.snaps.stream().anyMatch(x -> x <= 0)) {
			throw new IllegalArgumentException("a snap time cannot be negative or equal to zero");
		}
	}

	public int getSlots() {
		return slots;
	}

	public void setSlots(int slots) {

		this.slots = slots;
		if (slots <= 0) {
			throw new IllegalArgumentException("slots number cannot be negative or equal to zero");
		}
	}

	public int getExecTime() {
		return execTime;
	}

	public void setExecTime(int execTime) {
		if (execTime <= 0) {
			throw new IllegalArgumentException("exec_time number cannot be negative or equal to zero");
		}
		this.execTime = execTime;
	}

	public List<Entity> getEnvironment() {
		return environment;
	}

	public void setEnvironment(List<Entity> environment) {

		this.environment = Objects.requireNonNull(environment);
		if (environment.stream().anyMatch(x -> x == null)) {
			throw new IllegalArgumentException("the environment should not contains null entities");
		}
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {

		this.actions = Objects.requireNonNull(actions);
		if (actions.stream().anyMatch(x -> x == null)) {
			throw new IllegalArgumentException("the actions list should not contains null action");
		}
	}

	public Fitness getFitness() {
		return fitness;
	}

	public void setFitness(Fitness fitness) {
		this.fitness = Objects.requireNonNull(fitness);
	}

	public List<Entity> getCurrentEnvironment() {
		return currentEnvironment;
	}

	public void setCurrentEnvironment(List<Entity> currentEnvironment) {
		this.currentEnvironment = Objects.requireNonNull(currentEnvironment);
		if (currentEnvironment.stream().anyMatch(x -> x == null)) {
			throw new IllegalArgumentException("the current environment should not contains null entities");
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Model\n[snaps=");
		builder.append(snaps);
		builder.append(",\nslots=");
		builder.append(slots);
		builder.append(",\nexecTime=");
		builder.append(execTime);
		builder.append(",\nenvironment=");
		builder.append(environment);
		builder.append(",\nactions=");
		builder.append(actions);
		builder.append(",\nfitness=");
		builder.append(fitness);
		builder.append(",\ncurrentEnvironment=");
		builder.append(currentEnvironment);
		builder.append("]");
		return builder.toString();
	}

}

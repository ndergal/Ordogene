package org.ordogene.file;

import java.util.List;
import java.util.Objects;

import org.ordogene.file.models.JSONAction;
import org.ordogene.file.models.JSONEntity;
import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.parser.Validable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.emory.mathcs.backport.java.util.Collections;

public class JSONModel implements Validable {
	private List<Integer> snaps;
	private int slots;
	@JsonProperty("exec_time")
	private int execTime;
	private List<JSONEntity> environment;
	private List<JSONAction> actions;
	private JSONFitness fitness;
	@JsonIgnore
	private List<JSONEntity> currentEnvironment;

	@Override
	public boolean isValid() {
		return snaps != null && slots != 0 && execTime != 0 && environment != null && actions != null && fitness != null
				&& environment.stream().allMatch(Validable::isValid) && actions.stream().allMatch(Validable::isValid)
				&& fitness.isValid();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getSnaps() {
		return Collections.unmodifiableList(snaps);
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

	public List<JSONEntity> getEnvironment() {
		return environment;
	}

	public void setEnvironment(List<JSONEntity> environment) {

		this.environment = Objects.requireNonNull(environment);
		if (environment.stream().anyMatch(x -> x == null)) {
			throw new IllegalArgumentException("the environment should not contains null entities");
		}
	}

	public List<JSONAction> getActions() {
		return actions;
	}

	public void setActions(List<JSONAction> actions) {

		this.actions = Objects.requireNonNull(actions);
		if (actions.stream().anyMatch(x -> x == null)) {
			throw new IllegalArgumentException("the actions list should not contains null action");
		}
		if (actions.stream().anyMatch(x -> !x.isValid())) {
			throw new IllegalArgumentException("the actions list should not contains not valid action");
		}

	}

	public JSONFitness getFitness() {
		return fitness;
	}

	public void setFitness(JSONFitness fitness) {
		this.fitness = Objects.requireNonNull(fitness);
	}

	public List<JSONEntity> getCurrentEnvironment() {
		return currentEnvironment;
	}

	public void setCurrentEnvironment(List<JSONEntity> currentEnvironment) {
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

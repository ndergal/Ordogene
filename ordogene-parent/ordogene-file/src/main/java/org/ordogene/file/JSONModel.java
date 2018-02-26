package org.ordogene.file;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.ordogene.file.models.JSONAction;
import org.ordogene.file.models.JSONEntity;
import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.models.JSONInput;
import org.ordogene.file.models.JSONOperand;
import org.ordogene.file.parser.Validable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONModel implements Validable {
	private int slots;
	@JsonProperty("exec_time")
	private int execTime;
	private String name;
	private List<JSONEntity> environment;
	private List<JSONAction> actions;
	private JSONFitness fitness;

	@Override
	public boolean isValid() {
		return slots != 0 && execTime != 0 && environment != null && actions != null && fitness != null
				&& environment.stream().allMatch(Validable::isValid) && actions.stream().allMatch(Validable::isValid)
				&& fitness.isValid() && this.Consistency();
	}

	private boolean Consistency() {
		Set<String> all = new HashSet<String>();
		Set<String> contained = new HashSet<String>();
		all.addAll(environment.stream().map(JSONEntity::getName).collect(Collectors.toList()));
		for (JSONAction a : actions) {
			for (JSONInput i : a.getInput()) {
				contained.add(i.getName());
			}
		}
		for (JSONAction a : actions) {
			for (JSONEntity e : a.getOutput()) {
				contained.add(e.getName());
			}
		}
		contained.addAll(fitness.getOperands().stream().map(JSONOperand::getName).collect(Collectors.toList()));
		return contained.stream().allMatch(str -> all.contains(str));
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Model\n[");
		builder.append("\nname=");
		builder.append(name);
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
		builder.append("]");
		return builder.toString();
	}

}
